package se.mah.af2015.dinekonomi;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SQLiteCursorAdapter extends CursorAdapter {
    public SQLiteCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView tvImage = (ImageView) view.findViewById(R.id.list_image);

        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        TextView tvDate = (TextView) view.findViewById(R.id.date);
        TextView tvCategory = (TextView) view.findViewById(R.id.category);
        AmountTextView tvAmount = (AmountTextView) view.findViewById(R.id.amount);

        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        int category = cursor.getInt(cursor.getColumnIndexOrThrow("category"));
        double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

        int imageResource = context.getResources().getIdentifier(getDrawableFromId(category), null, context.getPackageName());
        tvImage.setImageResource(imageResource);

        tvTitle.setText(title);
        tvDate.setText(date);
        tvCategory.setText(MainActivity.CATEGORIES[category]);
        tvAmount.setText(Double.toString(amount));
    }

    private String getDrawableFromId(int id) {
        String res = "@drawable/ic_other";
        if(id >= 0 && id < 6) {
            switch (id) {
                case 0:
                    res = "@drawable/ic_other";
                    break;
                case 1:
                    res = "@drawable/ic_accomodation";
                    break;
                case 2:
                    res = "@drawable/ic_food";
                    break;
                case 3:
                    res = "@drawable/ic_shopping";
                    break;
                case 4:
                    res = "@drawable/ic_transport";
                    break;
                case 5:
                    res = "@drawable/ic_savings";
                    break;
            }
        }

        return res;
    }
}
