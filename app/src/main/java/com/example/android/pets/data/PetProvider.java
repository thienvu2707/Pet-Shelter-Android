package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by thienvu on 4/24/17.
 */

public class PetProvider extends ContentProvider {

    //Uri matcher code for the content Uri for the pets table
    public static final int PETS = 100;

    //Uri matcher code for the content of single row of the table
    public static final int PETS_ID = 101;

    /**
     * UriMatcher object is to match the content of Uri corresponding to the int code
     * the input passed into constructor represent the code and return the root of Uri
     * No_match is the most common in Uri Matcher
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer this run the first time if anything is called from this class
    static {

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PETS_ID);
    }


    public static final String LOG_TAG = PetProvider.class.getName();

    private PetDbHelper mPetDbHelper;

    /**
     * Initialize provider and the database helper object
     *
     * @return
     */
    @Override
    public boolean onCreate() {
        //create a PetDbHelper object and gain access to the pets database
        mPetDbHelper = new PetDbHelper(getContext());
//      mPetDbHelper.getReadableDatabase();
        return true;
    }

    /**
     * Perform the query for the given Uri, use the given projection, selection, selectionArgs and sortOrder
     *
     * @param uri
     * @param projections
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projections, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //get the readable database
        SQLiteDatabase database = mPetDbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            //Query the table directly with projection... content multi rows
            case PETS:
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projections, selection, selectionArgs, null, null, sortOrder);
                break;
            case PETS_ID:
                selection = PetContract.PetEntry._ID_PET = "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                //perform a query return a cursor contain row of table
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projections, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown Uri" + uri);
        }

        return cursor;
    }

    /**
     * Return the MIME type of the data for the content Uri
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Insert new data to the provider with a new ContentValues
     *
     * @param uri
     * @param contentValues
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion not supported for" + uri);
        }
    }

    /**
     * Insert new pet using the given Uri and content Value
     *
     * @param uri
     * @param contentValues
     * @return
     */
    private Uri insertPet(Uri uri, ContentValues contentValues) {

        //check the name of the pet is not null
        String name = contentValues.getAsString(PetContract.PetEntry.NAME_OF_PET);
        if (name == null) {
            throw new IllegalArgumentException("Pet required a name");
        }
        Integer gender = contentValues.getAsInteger(PetContract.PetEntry.GENDER_OF_PET);
        if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet required valid gender");
        }
        Integer weight = contentValues.getAsInteger(PetContract.PetEntry.WEIGHT_OF_PET);
        if (weight == null && weight < 0) {
            throw new IllegalArgumentException("Pet need valid weight info");
        }
        //get writable database
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();

        //Insert new pet with given value
        long id = database.insert(PetContract.PetEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert new pet to table");
            return null;
        }

        //return with the new Uri with the id that append at the end
        //like content://com.example.android.pets/pet/#id
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete the data that given from the selection and selection arguments
     *
     * @param uri
     * @param s
     * @param strings
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    /**
     * Update the data that give the selection and selection arguments with a new contentValues
     *
     * @param uri
     * @param contentValues
     * @param s
     * @param strings
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
