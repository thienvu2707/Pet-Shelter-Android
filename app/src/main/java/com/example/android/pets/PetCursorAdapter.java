package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;

/**
 * Created by thienvu on 5/2/17.
 */


/**
 * PetCursorAdapter is an adapter for the listView and gridView
 * that use cursor data as data source
 */
public class PetCursorAdapter extends CursorAdapter {

    /**
     * Constructor of PetCursorAdapter
     * @param context
     * @param c
     */
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }


    /**
     * This make a new blank of list view, no data is set to view yet
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    /**
     * Method to bind the pet data current pointed by cursor to given list item view
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //first need to find list item
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView summary = (TextView) view.findViewById(R.id.summary);

        //use the Cursor to find column in the table that needed
        int nameColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.NAME_OF_PET);
        int summaryColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.BREED_OF_PET);

        //read the value of the column name from Cursor for current pet
        String nameCursor = cursor.getString(nameColumnIndex);
        String summaryCursor = cursor.getString(summaryColumnIndex);

        //setText to the list view with given cursor database current pet
        name.setText(nameCursor);
        summary.setText(summaryCursor);

    }
}
