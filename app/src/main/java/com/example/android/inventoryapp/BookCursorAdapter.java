package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

import java.text.NumberFormat;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context.
     * @param cursor  The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context.
     * @param cursor  The cursor from which to get the data. The cursor is already.
     *                moved to the correct position.
     * @param parent The parent to which the new view is attached to the newly created list
     *               item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in the list_item.xml.
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by the cursor) to the given
     * list item layout.
     *
     * @param view      Existing view, returned earlier by newView() method.
     * @param context   app context.
     * @param cursor    The cursor from which to get the data. The cursor is already moved to the
     *                  correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout.
        TextView nameTextView = view.findViewById(R.id.name);
        TextView summaryTextView = view.findViewById(R.id.summary);

        // Find the columns of pet attributes that we're interested in.
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);

        // Read the book attributes from the Cursor for the current book.
        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);

        // Add the local currency to the price.
        // Found how to do this here - http://www.avajava.com/tutorials/lessons/how-do-i-use-
        // numberformat-to-format-currencies.html
        // Author Deron Eriksson written in 2014.
        NumberFormat format = NumberFormat.getCurrencyInstance();

        // Update the TextViews with the attributs for the current book.
        nameTextView.setText(bookName);
        // Solution to the app crashing was found here - https://stackoverflow.com/questions/
        // 45274167/formating-using-decimalformat-thows-exception-cannot-format-given-object-as-a
        // Author davidxxx July 24th, 2017.
        summaryTextView.setText(format.format(Double.valueOf(bookPrice)));
    }
}
