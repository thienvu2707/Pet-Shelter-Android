package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by thienvu on 3/19/17.
 */

public final class PetContract {

    //the content authority name of entire content provider
    //it's the relationship between domain and website
    //content authority is the package name of the app which is unique
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

    //Use CONTENT_AUTHORITY to create base of all Uri use to contact
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //this is the path to look into pets data
    public static final String PATH_PETS = "pets";


    /**
     * Private contract class to prevent accidentally
     * make a private constructor
     */
    private PetContract()
    {

    }

    public static final class PetEntry implements BaseColumns
    {
        //The content Uri to access the pets data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        public static final String TABLE_NAME = "Pets";
        /**
         * Name of each column
         */
        public static String _ID_PET = BaseColumns._ID;
        public static final String BREED_OF_PET= "breed";
        public static final String NAME_OF_PET = "name";
        public static final String GENDER_OF_PET = "gender";
        public static final String WEIGHT_OF_PET = "weight";

        /**
         * Integer for pets gender
         */
        public static final int MALE_GENDER = 1;
        public static final int FEMALE_GENDER = 2;
        public static final int UNKNOWN_GENDER = 0;

        public static boolean isValidGender(int gender)
        {
            if (gender == UNKNOWN_GENDER || gender == MALE_GENDER || gender == FEMALE_GENDER)
                return true;
            else
                return false;
        }
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +CONTENT_AUTHORITY + "/";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";
    }
}
