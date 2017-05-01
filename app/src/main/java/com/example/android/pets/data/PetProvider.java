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
        if (weight != null && weight < 0) {
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
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //get a writable database
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case PETS:
                //Delete all row that match selection and selectionArgs
                return database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            case PETS_ID:
                //delete single row define by Uri id
                selection = PetContract.PetEntry._ID_PET + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Delete not supported Uri" + uri);
        }
    }

    /**
     * Update the data that give the selection and selection arguments with a new contentValues
     *
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetEntry._ID_PET + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported for Uri " + uri);
        }
    }

    /**
     * Update pets in the database with the given ContentValue. Apply change to rows
     * specified selection and selectionArgs row that need to be update
     * return the number of row that were successful updated
     *
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return
     */
    private int updatePet(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        //if the key is present then check value name not null
        if (contentValues.containsKey(PetContract.PetEntry.NAME_OF_PET)) {
            String name = contentValues.getAsString(PetContract.PetEntry.NAME_OF_PET);
            if (name == null) {
                throw new IllegalArgumentException("Pet required a name");
            }
        }

        //check gender is valid
        if (contentValues.containsKey(PetContract.PetEntry.GENDER_OF_PET)) {
            Integer gender = contentValues.getAsInteger(PetContract.PetEntry.GENDER_OF_PET);
            if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet required valid gender");
            }
        }

        //check weight is valid
        if (contentValues.containsKey(PetContract.PetEntry.WEIGHT_OF_PET)) {
            Integer weight = contentValues.getAsInteger(PetContract.PetEntry.WEIGHT_OF_PET);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet required valid weight");
            }
        }
        //if there no need to change in database, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }

        //Otherwise, get writable database to update the data
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();

        //return the number of databases row affected by update statement
        return database.update(PetContract.PetEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }
}
