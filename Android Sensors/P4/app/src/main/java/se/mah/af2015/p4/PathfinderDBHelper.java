package se.mah.af2015.p4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * PathfinderDPHelper is helper class that allows database manipulation using SQLite
 *
 * @author Alexander Johansson (AF2015)
 */
public class PathfinderDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PathfinderDatabase";
    private static final int DATABASE_VERSION = 2;

    private static final String USER_TABLE_NAME = "users";
    private static final String USER_COLUMN_ID = "_id";
    private static final String USER_COLUMN_USERNAME = "username";
    private static final String USER_COLUMN_PASSWORD = "password";
    private static final String USER_COLUMN_STEPS = "steps";

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE_NAME + "(" +
            USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            USER_COLUMN_USERNAME + " TEXT," +
            USER_COLUMN_PASSWORD + " TEXT," +
            USER_COLUMN_STEPS + " INTEGER);";

    /**
     *
     * @param context
     */
    public PathfinderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        db.execSQL(CREATE_USER_TABLE);

        // Insert user
        insertUser(db, "alex", "password", 0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not used
    }

    /**
     * Method inserts user to database
     * @param db SQLiteDatabase
     * @param username String
     * @param password String
     * @param steps Integer
     */
    public void insertUser(SQLiteDatabase db, String username, String password, int steps) {
        // Values
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_USERNAME, username);
        values.put(USER_COLUMN_PASSWORD, password);
        values.put(USER_COLUMN_STEPS, steps);

        // Insert
        db.insert(USER_TABLE_NAME, "", values);
    }

    /**
     * Method returns current user steps from databaes
     * @param id Interger
     * @return Integer
     */
    public int getUserSteps(int id) {
        // Get database
        SQLiteDatabase db = this.getReadableDatabase();
        // Run query
        Cursor cursor = db.rawQuery("SELECT " + USER_COLUMN_STEPS + " FROM " + USER_TABLE_NAME + " WHERE " + USER_COLUMN_ID + "=" + id, null);
        if(cursor != null && cursor.moveToFirst()) {
            // Get steps and close Cursor, return steps
            int steps = cursor.getInt(cursor.getColumnIndex(USER_COLUMN_STEPS));
            cursor.close();
            return steps;
        }

        return 0;
    }

    /**
     * Method increments user steps where id equals input parameter
     * @param id Integer
     */
    public void incrementUserSteps(int id) {
        // Get database
        SQLiteDatabase db = this.getWritableDatabase();

        // Execute SQL
        db.execSQL("UPDATE " + USER_TABLE_NAME + " SET " + USER_COLUMN_STEPS + "=" + USER_COLUMN_STEPS + "+1 WHERE " + USER_COLUMN_ID + "=" + id);
    }

    /**
     * Method resets user steps where id equals input parameter
     * @param id
     */
    public void resetUserSteps(int id) {
        // Get database
        SQLiteDatabase db = this.getWritableDatabase();

        // Execute SQL
        db.execSQL("UPDATE " + USER_TABLE_NAME + " SET " + USER_COLUMN_STEPS + "=0 WHERE " + USER_COLUMN_ID + "=" + id);
    }

    /**
     * Method authorizes user by returning its user id if username and password is correct,
     * returns user id if successful, -1 if not
     * @param username String
     * @param password String
     * @return Integer
     */
    public int authorizeUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + USER_COLUMN_ID + " FROM " + USER_TABLE_NAME + " WHERE " + USER_COLUMN_USERNAME + "='" + username + "' AND " + USER_COLUMN_PASSWORD + "='" + password + "'", null);
        if(cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(USER_COLUMN_ID));

            cursor.close();
            return id;
        }

        return -1;
    }
}
