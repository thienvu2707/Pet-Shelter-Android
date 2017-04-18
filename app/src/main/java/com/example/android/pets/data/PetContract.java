package com.example.android.pets.data;

import android.provider.BaseColumns;

/**
 * Created by thienvu on 3/19/17.
 */

public final class PetContract {

    /**
     * Private contract class to prevent accidentally
     * make a private constructor
     */
    private PetContract()
    {

    }

    public static final class PetEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Pets";
        /**
         * Name of each column
         */
        public static final String _ID_PET = BaseColumns._ID;
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
    }
}
