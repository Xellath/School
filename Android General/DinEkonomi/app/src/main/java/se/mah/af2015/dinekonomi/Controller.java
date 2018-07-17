package se.mah.af2015.dinekonomi;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Controller {
    private MainActivity activity;

    private String fromDate;
    private String toDate;

    private SimpleDateFormat dateFormatter;

    public Controller(MainActivity activity) {
        this.activity = activity;

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    public void updateSettings(String name, String surname) {
        SharedPreferences.Editor editor = activity.getSharedPreferences("user", MODE_PRIVATE).edit();
        editor.putString("name", name);
        editor.putString("surname", surname);
        editor.apply();

        activity.setHeaderUsername(name, surname);
        activity.showSnackbar(activity.getResources().getString(R.string.settings_updated), Snackbar.LENGTH_SHORT);
    }

    public void setFabVisibility(int visibility) {
        activity.setFabVisibility(visibility);
    }

    public void showInfoDialog(Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        int category = cursor.getInt(cursor.getColumnIndexOrThrow("category"));
        double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage( "Datum: " + date + "\n" +
                                "Kategori: " + MainActivity.CATEGORIES[category] + "\n" +
                                "Pris/Belopp: " + amount + " kr\n");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void showDatePicker(final EconomyFragment fragment, final int fragmentType) {
        final Calendar newCalendar = Calendar.getInstance();
        new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDate = dateFormatter.format(newDate.getTime());

                new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        toDate = dateFormatter.format(newDate.getTime());

                        setCursorForFragment(fragment, fragmentType);
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setCursorForFragment(EconomyFragment fragment, int fragmentType) {
        Cursor newCursor = getNewCursor(fragmentType, "WHERE date BETWEEN '" + fromDate + "' AND '" + toDate + "' order by date DESC");
        fragment.setCursor(newCursor);
        fragment.updateCursor();
    }

    public Cursor getNewCursor(int fragmentType, String res) {
        SQLiteDatabase db = activity.getDbHelper().getWritableDatabase();
        Cursor newCursor;
        String queryString;
        if(fragmentType == 0) {
            queryString = "SELECT * FROM expenses";
            if(res != null) {
                queryString += " " + res;
            }

            newCursor = db.rawQuery(queryString, null);
        } else {
            queryString = "SELECT * FROM income";
            if(res != null) {
                queryString += " " + res;
            }

            newCursor = db.rawQuery(queryString, null);
        }

        return newCursor;
    }

    public void formatHomeText(TextView homeText) {
        SharedPreferences prefs = activity.getSharedPreferences("user", MODE_PRIVATE);
        String name = prefs.getString("name", null);
        String surname = prefs.getString("surname", null);

        double expenses = activity.getDbHelper().getTotalExpenses();
        double income = activity.getDbHelper().getTotalIncome();
        if(income > 0 && expenses > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Hej " + name + " " + surname + "!\n");
            sb.append("Vi har sammanställt lite uppgifter om dig nedan.\n\n");
            sb.append("Du har totala utgifter på " + expenses + " kr och en total inkomst på " + income + " kr\n\n");

            double total = income - expenses;
            sb.append("Detta resulterar i ");
            if (total > 0) {
                sb.append("ett överskott på " + total + " kr\n" +
                        "\n:)");
            } else if (total < 0) {
                sb.append("ett underskott på " + total + " kr\n" +
                        "\n:(");
            }

            homeText.setText(sb.toString());
        } else {
            homeText.setText("Hej " + name + " " + surname + "!");
        }
    }
}
