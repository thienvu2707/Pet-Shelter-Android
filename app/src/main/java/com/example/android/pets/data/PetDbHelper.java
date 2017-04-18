package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by thienvu on 3/20/17.
 */

public class PetDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = PetDbHelper.class.getName();

    //Name of the database
    private static final String DATABASE_NAME = "shelter.db";

    //Database version initial will be 1 but if anything update then increment the version
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructor of instance Pet Database helper
     * @param context
     */
    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create database for the first time
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create string contain the SQL for the pets table
        String SQL_CREATE_PET_TABLE = "CREATE TABLE "
                + PetContract.PetEntry.TABLE_NAME + " ("
                + PetContract.PetEntry._ID_PET + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetContract.PetEntry.NAME_OF_PET + " TEXT NOT NULL, "
                + PetContract.PetEntry.BREED_OF_PET + " TEXT, "
                + PetContract.PetEntry.GENDER_OF_PET + " INTEGER NOT NULL, "
                + PetContract.PetEntry.WEIGHT_OF_PET + " INTEGER NOT NULL DEFAULT 0);";
        Log.v(LOG_TAG, SQL_CREATE_PET_TABLE);
        //then we execute database
        sqLiteDatabase.execSQL(SQL_CREATE_PET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
