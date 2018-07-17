package se.mah.af2015.dinekonomi;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

/*
TODO:
- Diagram (PieChart)
- Barcode Scanner
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private ViewGroup viewGroup;

    private EconomyDBHelper dbHelper;

    private EconomyFragment economyFragment;

    public static final String[] CATEGORIES = { "Övrigt", "Boende", "Mat och hushåll", "Nöje och shopping", "Transport", "Lön/Sparande" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setCancelable(true);
                dialogBuilder.setIcon(R.drawable.ic_add);

                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add, null);
                dialogBuilder.setView(dialogView);

                final EditText etTitle = (EditText) dialogView.findViewById(R.id.title);
                final EditText etAmount = (EditText) dialogView.findViewById(R.id.amount);
                final EditText etDate = (EditText) dialogView.findViewById(R.id.date);
                etTitle.requestFocus();

                final RadioButton rbIncome = (RadioButton) dialogView.findViewById(R.id.rb_income);
                final RadioButton rbExpense = (RadioButton) dialogView.findViewById(R.id.rb_expense);
                rbIncome.setChecked(true);

                final Spinner categorySpinner = (Spinner) dialogView.findViewById(R.id.category);
                final Button btnAdd = (Button) dialogView.findViewById(R.id.button_add);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, CATEGORIES);
                categorySpinner.setAdapter(adapter);

                final AlertDialog dialog = dialogBuilder.create();

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = etTitle.getText().toString();
                        String amount = etAmount.getText().toString();
                        String date = etDate.getText().toString();
                        int category = categorySpinner.getSelectedItemPosition();

                        if(!amount.isEmpty() && !title.isEmpty() && !date.isEmpty()) {
                            try {
                                double dAmount = Double.valueOf(amount);

                                if (rbExpense.isChecked()) {
                                    dialog.dismiss();
                                    dbHelper.addExpense(date, title, category, dAmount);
                                    economyFragment.forceUpdateCursor();
                                    showSnackbar("Utgiften lades till!", Snackbar.LENGTH_SHORT);
                                } else if (rbIncome.isChecked()) {
                                    dialog.dismiss();
                                    dbHelper.addIncome(date, title, category, dAmount);
                                    economyFragment.forceUpdateCursor();
                                    showSnackbar("Inkomsten lades till!", Snackbar.LENGTH_SHORT);
                                }
                            } catch(NumberFormatException e) {
                                Snackbar.make(v.getRootView(), "Beloppet är inte ett nummervärde", Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            Snackbar.make(v.getRootView(), "Skriv in värden", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String personalIdentity = prefs.getString("personal_id", null);
        String name = prefs.getString("name", null);
        String surname = prefs.getString("surname", null);
        if(findViewById(R.id.content_main) != null) {
            if (name != null && surname != null) {
                setHeaderUsername(name, surname);
                showHomeFragment();
            } else {
                showSettingsFragment(personalIdentity, name, surname);
                navigationView.getMenu().getItem(3).setChecked(true);
            }
        }

        dbHelper = new EconomyDBHelper(this);
        boolean added = prefs.getBoolean("added_once", false);
        if(!added) {
            dbHelper.addExpense("2014-11-22 12:45:34", "PayPal", 0, 355);
            dbHelper.addExpense("2014-11-25 12:45:44", "Ebay", 1, 125);
            dbHelper.addExpense("2014-11-23 14:45:54", "Ali", 2, 995);
            dbHelper.addExpense("2015-11-23 17:45:54", "Transportstyrelsen", 3, 3335);
            dbHelper.addExpense("2015-11-23 13:45:54", "IKEA", 4, 855);
            dbHelper.addExpense("2016-06-23 18:45:54", "OKQ8", 5, 557);

            dbHelper.addIncome("2014-11-22 12:45:34", "JOBB", 5, 355);
            dbHelper.addIncome("2014-11-25 12:45:44", "ÖVRIGT", 0, 400);
            dbHelper.addIncome("2014-11-23 14:45:54", "Ali", 0, 9666);
            dbHelper.addIncome("2015-11-23 17:45:54", "Transportstyrelsen", 5, 36808);
            dbHelper.addIncome("2015-11-23 13:45:54", "JOBB", 5, 25000);
            dbHelper.addIncome("2016-06-23 18:45:54", "OKQ8", 5, 42000);

            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
            editor.putBoolean("added_once", true);
            editor.apply();
        }
    }

    public void setHeaderUsername(String name, String surname) {
        TextView tvUsername = (TextView) navigationView.getHeaderView(0).findViewById(R.id.menu_username);
        tvUsername.setText(name + " " + surname);
    }

    public void setFabVisibility(int visibility) {
        fab.setVisibility(visibility);
    }

    public void showSnackbar(String text, int duration) {
        Snackbar.make(viewGroup, text, duration).setAction("Action", null).show();
    }

    public EconomyDBHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String personalIdentity = prefs.getString("personal_id", null);
        String name = prefs.getString("name", null);
        String surname = prefs.getString("surname", null);

        if (id == R.id.nav_home) {
            showHomeFragment();
        } else if (id == R.id.nav_expenses) {
            showExpensesFragment();
        } else if (id == R.id.nav_income) {
            showIncomeFragment();
        } else if (id == R.id.nav_settings) {
            showSettingsFragment(personalIdentity, name, surname);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_main, homeFragment).commit();

        fm.popBackStack();

        fab.setVisibility(View.INVISIBLE);
    }

    private void showExpensesFragment() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor expensesCursor = db.rawQuery("select * from expenses order by date DESC", null);

        economyFragment = EconomyFragment.createWithCursor(expensesCursor, "Utgifter", 0);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_main, economyFragment).commit();

        fm.popBackStack();

        fab.setVisibility(View.VISIBLE);
    }

    private void showIncomeFragment() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor expensesCursor = db.rawQuery("select * from income order by date DESC", null);

        economyFragment = EconomyFragment.createWithCursor(expensesCursor, "Inkomster", 1);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_main, economyFragment).commit();

        fm.popBackStack();

        fab.setVisibility(View.VISIBLE);
    }

    private void showSettingsFragment(String personalIdentity, String name, String surname) {
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setStrings(personalIdentity, name, surname);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_main, settingsFragment).commit();

        fm.popBackStack();

        fab.setVisibility(View.INVISIBLE);
    }
}
