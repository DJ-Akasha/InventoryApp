package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.BookDbHelper;

import java.text.NumberFormat;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Initialise the private variables
     */
    private TextView mNameTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private Button mSaleButton;

    /**
     * Database helper object.
     */
    private BookDbHelper mDbHelper;

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
     * @param cursor  The cursor from which to get the data. The cursor is already
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
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout.
        mNameTextView = view.findViewById(R.id.name);
        mPriceTextView = view.findViewById(R.id.price);
        mQuantityTextView = view.findViewById(R.id.quantity_in_stock);
        mSaleButton = view.findViewById(R.id.sale_button);

        // Find and read the book attributes from the Cursor for the current book.
        String bookName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
        String bookPrice = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE));
        final String mBookQuantity = cursor.getString
                (cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY));
        final int id = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));

        // Add the local currency to the price.
        // Found how to do this here - http://www.avajava.com/tutorials/lessons/how-do-i-use-
        // numberformat-to-format-currencies.html
        // Author Deron Eriksson written in 2014.
        NumberFormat format = NumberFormat.getCurrencyInstance();

        // Update the TextViews with the attributes for the current book.
        mNameTextView.setText(bookName);
        // Solution to the app crashing was found here - https://stackoverflow.com/questions/
        // 45274167/formating-using-decimalformat-thows-exception-cannot-format-given-object-as-a
        // Author davidxxx July 24th, 2017.
        mPriceTextView.setText(format.format(Double.valueOf(bookPrice)));
        mQuantityTextView.setText(String.valueOf(mBookQuantity));

        mSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If there is no more stock display toast message else decrease quantity by 1.
                if (Integer.parseInt(mBookQuantity) == 0) {
                    Toast.makeText(context, R.string.no_stock,
                            Toast.LENGTH_SHORT).show();
                } else {
                    // This decreases the items' stock quantity.
                    int quantity = Integer.parseInt(mBookQuantity) - 1;
                    // This sets up the values to change.
                    ContentValues values = new ContentValues();
                    // This changes the values.
                    values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    // This points to which row needs changing.
                    String selection = BookEntry._ID + "+?";
                    // This points to which database item on the list to change.
                    Uri currentBook = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                    // This points to which column needs changing.
                    String[] selectionArgs = new String[]{String.valueOf(id)};
                    //This updates the database via the content resolver.
                    context.getContentResolver().update
                            (currentBook, values, selection, selectionArgs);
                }
            }
        });
    }
}
