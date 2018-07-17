package se.mah.af2015.dinekonomi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EconomyDBHelper extends SQLiteOpenHelper {
    private static final String EXPENSES_TABLE_NAME = "expenses";
    private static final String EXPENSES_COLUMN_ID = "_id";
    private static final String EXPENSES_COLUMN_DATE = "date";
    private static final String EXPENSES_COLUMN_TITLE = "title";
    private static final String EXPENSES_COLUMN_CATEGORY = "category";
    private static final String EXPENSES_COLUMN_PRICE = "price";

    private static final String INCOME_TABLE_NAME = "income";
    private static final String INCOME_COLUMN_ID = "_id";
    private static final String INCOME_COLUMN_DATE = "date";
    private static final String INCOME_COLUMN_TITLE = "title";
    private static final String INCOME_COLUMN_CATEGORY = "category";
    private static final String INCOME_COLUMN_PRICE = "price";

    private static final String BARCODE_TABLE_NAME = "barcodes";
    private static final String BARCODE_COLUMN_ID = "_id";
    private static final String BARCODE_COLUMN_EID = "expense_id";

    private static final String DATABASE_CREATE_EXPENSES =
            "create table " + EXPENSES_TABLE_NAME + "(" +
                    EXPENSES_COLUMN_ID + " integer primary key autoincrement," +
                    EXPENSES_COLUMN_DATE + " date not null," +
                    EXPENSES_COLUMN_TITLE + " text not null," +
                    EXPENSES_COLUMN_CATEGORY + " integer not null default 0," +
                    EXPENSES_COLUMN_PRICE + " double not null);";

    private static final String DATABASE_CREATE_INCOME =
            "create table " + INCOME_TABLE_NAME + "(" +
                    INCOME_COLUMN_ID + " integer primary key autoincrement," +
                    INCOME_COLUMN_DATE + " text not null," +
                    INCOME_COLUMN_TITLE + " text not null," +
                    INCOME_COLUMN_CATEGORY + " integer not null default 0," +
                    INCOME_COLUMN_PRICE + " double not null);";

    private static final String DATABASE_CREATE_BARCODE =
            "create table " + BARCODE_TABLE_NAME + "(" +
                    BARCODE_COLUMN_ID + " integer primary key autoincrement," +
                    BARCODE_COLUMN_EID + " integer not null);";

    private static final String DATABASE_NAME = "economics.db";
    private static final int DATABASE_VERSION = 2;

    public EconomyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_EXPENSES);
        db.execSQL(DATABASE_CREATE_INCOME);
        db.execSQL(DATABASE_CREATE_BARCODE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + EXPENSES_TABLE_NAME);
        db.execSQL("drop table if exists " + INCOME_TABLE_NAME);
        db.execSQL("drop table if exists " + BARCODE_TABLE_NAME);
        onCreate(db);
    }

    public void addExpense(String date, String title, int category, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EXPENSES_COLUMN_DATE, date);
        values.put(EXPENSES_COLUMN_TITLE, title);
        values.put(EXPENSES_COLUMN_CATEGORY, category);
        values.put(EXPENSES_COLUMN_PRICE, price);

        db.insert(EXPENSES_TABLE_NAME, "", values);
    }

    public void addIncome(String date, String title, int category, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(INCOME_COLUMN_DATE, date);
        values.put(INCOME_COLUMN_TITLE, title);
        values.put(INCOME_COLUMN_CATEGORY, category);
        values.put(INCOME_COLUMN_PRICE, amount);

        db.insert(INCOME_TABLE_NAME, "", values);
    }

    public double getTotalExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select sum(" + EXPENSES_COLUMN_PRICE + ") as \"total_expenses\" from " + EXPENSES_TABLE_NAME, null);
        if(cursor != null && cursor.moveToFirst()) {
            double result = cursor.getDouble(cursor.getColumnIndex("total_expenses"));
            return result;
        }

        return 0.0;
    }

    public double getTotalIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select sum(" + INCOME_COLUMN_PRICE + ") as \"total_income\" from " + INCOME_TABLE_NAME, null);
        if(cursor != null && cursor.moveToFirst()) {
            double result = cursor.getDouble(cursor.getColumnIndex("total_income"));
            return result;
        }

        return 0.0;
    }
}
