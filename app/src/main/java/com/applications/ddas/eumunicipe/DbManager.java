package com.applications.ddas.eumunicipe;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ddas on 29-03-2015.
 */
public class DbManager extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "eumunicipe";
    private static final SQLiteDatabase.CursorFactory factory = null;
    private static final int DATABASE_VERSION = 1;
    private AssetManager assetManager;
    public static SQLiteDatabase database;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public DbManager(Context context) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        assetManager = context.getAssets();
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createString = readFromFile("territorial_hierarchy.sql");
        String []splits = createString.split(";");
        for(String command : splits ) {
            db.execSQL(command);
        }

        String insertString = readFromFile("territorial_insert.sql");
        splits = insertString.split(";");
        for(String command : splits ) {
            db.execSQL(command);
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private String readFromFile(String filename) {
        String ret = "";

        try {
            Log.d("Path" , assetManager.toString());
            InputStream inputStream = assetManager.open(filename);


            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                    Log.d("Path" , receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        Log.d("Create DB" , ret);
        return ret;
    }

    public static String[] getEmailAndMunicipality(String city) {
        if (database == null) {
            database = MainActivity.database;
        }
        Log.d("Location", city);
        String sql = "SELECT email,name FROM Municipality WHERE name like '%" + city + "%';";
        Log.d("SQL:", sql);
        Cursor cursor = database.rawQuery(sql, new String[]{});

        String dbMunicipalityMail = "";
        String dbMunicipalityName = "";
        cursor.moveToFirst();
        //while(cursor.moveToNext()) {
        dbMunicipalityMail = cursor.getString(
                cursor.getColumnIndexOrThrow("email")
        );
        dbMunicipalityName = cursor.getString(
                cursor.getColumnIndexOrThrow("name")
        );
        Log.d("DB", dbMunicipalityMail);
        Log.d("DB", dbMunicipalityName);
        //}
        return new String[]{dbMunicipalityMail, dbMunicipalityName};
    }
}
