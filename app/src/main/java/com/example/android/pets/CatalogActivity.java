/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private PetDbHelper mPetHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        //to access to database we instantiate subclass of SQLiteHelper
        //the context is the current activity
//        mPetHelper = new PetDbHelper(this);
//
//      SQLiteDatabase db = mPetHelper.getReadableDatabase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDataInfo();
    }

    private void displayDataInfo()
    {

//        // create or open database to read it
//        SQLiteDatabase db = mPetHelper.getReadableDatabase();

        //Perform a raw SQL query SELECT * FROM PETS
        //To get a cursor that contain all the row of the table
//        Cursor cursor = db.rawQuery("SELECT * FROM " + PetContract.PetEntry.TABLE_NAME, null);

        String[] projection = {
                PetContract.PetEntry._ID_PET,
                PetContract.PetEntry.NAME_OF_PET,
                PetContract.PetEntry.BREED_OF_PET,
                PetContract.PetEntry.GENDER_OF_PET,
                PetContract.PetEntry.WEIGHT_OF_PET
        };
        String selection = PetContract.PetEntry.GENDER_OF_PET + "=?";
        String[] selectionArgs = new String[] {String.valueOf(PetContract.PetEntry.FEMALE_GENDER)};

        //perform query on content provider using Content Resolver
        //to access to the database
        Cursor cursor = getContentResolver().query(PetContract.PetEntry.CONTENT_URI, projection, null, null, null);

//        Cursor cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, null, null, null, null, null);

        TextView displayView = (TextView) findViewById(R.id.text_view_pet);

        try {
//            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
//            displayView.setText("Number rows: " + cursor.getCount());
            displayView.setText("The pets table contains " +cursor.getCount() + " pets.\n\n");
            displayView.append(PetContract.PetEntry._ID_PET + " - " +
                    PetContract.PetEntry.NAME_OF_PET + " - " +
                    PetContract.PetEntry.BREED_OF_PET + " - " +
                    PetContract.PetEntry.GENDER_OF_PET + " - " +
                    PetContract.PetEntry.WEIGHT_OF_PET + "\n");

            //Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(PetContract.PetEntry._ID_PET);
            int nameColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.NAME_OF_PET);
            int breedColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.BREED_OF_PET);
            int genderColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.GENDER_OF_PET);
            int weightColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.WEIGHT_OF_PET);


            while (cursor.moveToNext())
            {
                //repeat through all the returned rows in the cursor
                int currentId = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentBreed = cursor.getString(breedColumnIndex);
                int currentGender = cursor.getInt(genderColumnIndex);
                int currentWeight = cursor.getInt(weightColumnIndex);

                displayView.append("\n" + currentId + " - "
                + currentName + " - "
                + currentBreed + " - "
                + currentGender + " - "
                + currentWeight);
            }
        } finally {
            //always remember to close the cursor
            //when cursor done reading
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet()
    {
//        mPetHelper = new PetDbHelper(this);
//        //Get data from the repository in write mode
//        SQLiteDatabase db = mPetHelper.getWritableDatabase();

        //create new map of values
        //where column are the keys
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.NAME_OF_PET, "ToTo");
        values.put(PetContract.PetEntry.BREED_OF_PET, "Terrier");
        values.put(PetContract.PetEntry.GENDER_OF_PET, PetContract.PetEntry.MALE_GENDER);
        values.put(PetContract.PetEntry.WEIGHT_OF_PET, 7);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
//        long newRowId = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
//
//        Log.v("CatalogActivity", "New row ID: " + newRowId);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);
        Log.v("CatalogActivity", "New row ID: " + newUri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDataInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
