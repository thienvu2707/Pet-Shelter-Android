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

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Content of Uri for the existing Pet
    private Uri mCurrentPetUri;

    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_PET_LOADER = 0;

    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mBreedEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWeightEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = PetContract.PetEntry.UNKNOWN_GENDER;

    /**boolean flag that keeps track of whether the pet has been edited or not*/
    private boolean mPetHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on View
     * Implying that they are modifying the view,
     * we change mPetHasChanged boolean to true
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //get the content Intent
        Intent intent = getIntent();
        //get the data of the Intent
        mCurrentPetUri = intent.getData();

        //check if  intent that contain the Uri or not
        if (mCurrentPetUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_pet));

            //invalid option menu, so the "delete" menu option can be hidden
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_editor_pet));

            //Initialize a loader to read the pet data from database
            //display the current pet
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        //setup OnTouchListener on all input fields to know which has unsaved change
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.MALE_GENDER; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.FEMALE_GENDER; // Female
                    } else {
                        mGender = PetContract.PetEntry.UNKNOWN_GENDER; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    /**
     * Get user input from editor and save pet into database
     */
    private void savePet() {
        String editPetName = mNameEditText.getText().toString().trim();
        String editPetBreed = mBreedEditText.getText().toString();
        String editPetWeight = mWeightEditText.getText().toString().trim();

        //check if this supposed to be a new pet
        //check if all field editor are blank
        if (mCurrentPetUri == null && TextUtils.isEmpty(editPetName) && TextUtils.isEmpty(editPetBreed) && TextUtils.isEmpty(editPetWeight) && mGender == PetContract.PetEntry.UNKNOWN_GENDER)
        {
            //since no field modified, return without new pet
            return;
        }

//        //call PetDbHelper to access to database
//        PetDbHelper petHelper = new PetDbHelper(this);
//        //get the database to write mode
//        SQLiteDatabase db = petHelper.getWritableDatabase();

        //set ContentValue where column are key
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetContract.PetEntry.NAME_OF_PET, editPetName);
        contentValues.put(PetContract.PetEntry.BREED_OF_PET, editPetBreed);
        int changeWeightToInteger = 0;
        if (!TextUtils.isEmpty(editPetWeight))
        {
            changeWeightToInteger = Integer.parseInt(editPetWeight);
        }
        contentValues.put(PetContract.PetEntry.WEIGHT_OF_PET, changeWeightToInteger);
        contentValues.put(PetContract.PetEntry.GENDER_OF_PET, mGender);
//        long newRowId = db.insert(PetContract.PetEntry.TABLE_NAME, null, contentValues);

        //Check if this is a new or existing pet
        // So the currentPetUri null or not
        if (mCurrentPetUri == null) {
            //insert new pet into pet provider, return the Uri for the content uri for the new pet
            Uri newRowId = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, contentValues);

            //show toast depending whether insert or not
            if (newRowId == null) {
                Toast.makeText(this, getString(R.string.editor_activity_insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_activity_insert_successful), Toast.LENGTH_LONG).show();
            }
        } else {
            //Otherwise if there an existing pet so update the Uri content
            //Pass new content new value
            int rowAffected = getContentResolver().update(mCurrentPetUri, contentValues, null, null);

            //show toast whether success or not
            if (rowAffected == 0)
            {
                Toast.makeText(this, getString(R.string.editor_activity_update_failed), Toast.LENGTH_SHORT).show();
            } else
            {
                Toast.makeText(this, getString(R.string.editor_activity_update_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //if this is new pet, hide "delete" menu item
        if (mCurrentPetUri == null)
        {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // save what we edit
                savePet();
                //finish it to save
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                //If the pet hasn't changed, continue with navigating parent activity
                if (!mPetHasChanged)
                {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                //Otherwise if there are unsaved changes, setup a dialog to warn user
                //Create a click listener to handle the user warning
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //user clicked "Discard" button, navigate to EditorActivity
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                //show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //if the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged)
        {
            super.onBackPressed();
            return;
        }
        //Otherwise, if there unsaved changes, setup dialog
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //user clicked "Discard" button
                finish();
            }
        };

        //show dialog that there are unsaved
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Since editor pet activity need to show all pet attribute
        //show we need to have projection contain all column of pet table
        String[] projection = {
                PetContract.PetEntry._ID_PET,
                PetContract.PetEntry.NAME_OF_PET,
                PetContract.PetEntry.BREED_OF_PET,
                PetContract.PetEntry.GENDER_OF_PET,
                PetContract.PetEntry.WEIGHT_OF_PET
        };

        //loader will execute the contentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentPetUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Bail early if the cursor is null or there is less than 1 row in cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        //proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.NAME_OF_PET);
            int breedColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.BREED_OF_PET);
            int genderColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.GENDER_OF_PET);
            int weightColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.WEIGHT_OF_PET);

            //extract the value of the cursor from given index
            String name = cursor.getString(nameColumnIndex);
            String breed = cursor.getString(breedColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            int weight = cursor.getInt(weightColumnIndex);

            //update the views on the screen with the value from the database
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));
            switch (gender) {
                case PetContract.PetEntry.MALE_GENDER:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetContract.PetEntry.FEMALE_GENDER:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        //create an AlertDialog.Builder and set the message
        //click listener for the positive or negatie button on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                //user clicked the "keep editing" button so dismiss the dialog
                if (dialogInterface != null)
                {
                    dialogInterface.dismiss();
                }
            }
        });

        //create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}