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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PET_LOADERS = 0;

    PetCursorAdapter mCursorLoader;

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

        //need to find listView
        ListView petListView = (ListView) findViewById(R.id.list);
        //find empty view and set it when only have 0 item
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        //set up adapter
        mCursorLoader = new PetCursorAdapter(this, null);
        petListView.setAdapter(mCursorLoader);

        //kick off the loader
        getLoaderManager().initLoader(PET_LOADERS, null, this);

        //set on Item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //create an intent to go to editor Activity
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                //form a Uri content that represent specific pet that press on
                Uri currentPet = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);

                //set the Uri data field of the content
                intent.setData(currentPet);

                //start the activity
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet() {
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

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);
        Log.v("CatalogActivity", "New row ID: " + newUri);
    }

    /**
     * Helper method to delete all pet
     */
    private void deletePet()
    {
        int rowDeleted = getContentResolver().delete(PetContract.PetEntry.CONTENT_URI, null, null);

        Log.v("CatalogActivity", rowDeleted + " rows delete from pet database");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
//                displayDataInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Delete all pet
                deletePet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define the projection that specifies the column that we need
        String[] projection = {PetContract.PetEntry._ID_PET, PetContract.PetEntry.NAME_OF_PET, PetContract.PetEntry.BREED_OF_PET};
        return new CursorLoader(this, PetContract.PetEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Update new Cursor that contain the updated data
        mCursorLoader.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //callback called when the data need to be deleted
        mCursorLoader.swapCursor(null);
    }
}
