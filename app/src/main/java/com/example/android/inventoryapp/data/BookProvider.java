package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.BookContract.BookEntry;

// TODO: CHANGE THE THROWILLEGALEXCEPTIONS TO TOASTS IF USER DOESN'T ADD INFO IN A FIELD.
/**
 * {@link ContentProvider} for book store inventory app.
 */
public class BookProvider extends ContentProvider {

    /** Tag for the log messages. */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    /**
     * Database helper object.
     */
    private BookDbHelper mDbHelper;

    /** URI matcher code for the content URI for the pets table. */
    private static final int BOOKS = 100;

    /** URI matcher code for the content URI for a single book in the books table. */
    private static final int BOOKS_ID = 101;

    /** UriMatcher object */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initialiser. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments,
     * and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database.
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query.
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code.
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOKS_ID:
                // For the BOOKS_ID code, extract out the ID from the URI.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor, so we know what content URI the Cursor
        // was created for.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor.
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return  BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {

        // Check that the name is not null.
        String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
        if (TextUtils.isEmpty(name)) { // This was (name == null) not sure why ????????????????????
            Toast.makeText(getContext(), R.string.provider_requires_name, Toast.LENGTH_SHORT).show();
        }

        // Check that the genre is valid
        Integer genre = values.getAsInteger(BookEntry.COLUMN_PRODUCT_GENRE);
        if (genre == null || !BookEntry.isValidGenre(genre)) {
            Toast.makeText(getContext(), R.string.provider_requires_genre, Toast.LENGTH_SHORT).show();
        }

        // Check that the price is greater than or equal to 0.
        Double price = values.getAsDouble(BookEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0){
            Toast.makeText(getContext(), R.string.provider_requires_price, Toast.LENGTH_SHORT).show();
        }

        // Check that the quantity is greater than or equal to 0.
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0){
            Toast.makeText(getContext(), R.string.provider_requires_quantity, Toast.LENGTH_SHORT).show();
        }

        // Check that the name is not null.
        String supplierName = values.getAsString(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (TextUtils.isEmpty(supplierName)) {
            Toast.makeText(getContext(), R.string.provider_requires_supplier_name, Toast.LENGTH_SHORT).show();
        }

        // Check that the phone number has been added.
        Long supplierPhone = values.getAsLong(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
        if (supplierPhone != null) {
            Toast.makeText(getContext(), R.string.provider_requires_supplier_phone, Toast.LENGTH_SHORT).show();
        }

        // Get writeable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values.
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "insertBook: Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end.
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Update the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values.
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a name");
            }
        }

        // If the {@link BookEntry#COLUMN_PRODUCT_GENRE} key is present,
        // check that the genre value is valid.
        if (values.containsKey(BookEntry.COLUMN_PRODUCT_GENRE)) {
            Integer genre = values.getAsInteger(BookEntry.COLUMN_PRODUCT_GENRE);
            if (genre == null || !BookEntry.isValidGenre(genre)) {
                throw new IllegalArgumentException("Book requires valid genre");
            }
        }

        // If the {@link PetEntry#COLUMN_PRODUCT_PRICE} key is present,
        // check that the price value is valid.

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_PRICE)) {
            Double price = values.getAsDouble(BookEntry.COLUMN_PRODUCT_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Book requires valid price");
            }
        }

        // If the {@link PetEntry#COLUMN_PRODUCT_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(BookEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Book requires valid price");
            }
        }

        // If the {@link PetEntry#COLUMN_PRODUCT_SUPPLIER_NAME} key is present,
        // check that the supplier name value is not null.
        if (values.containsKey(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Book requires a supplier's name");
            }
        }

        // If there are no values to update, then don't try to update the database.
        if (values.size() == 0){
            return 0;
        }

        // Otherwise, get writeable database to update the data.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the databse and get the number of rows affected.
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values,selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated.
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted.
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args.
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                // Delete a single row given by the ID in the URI.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed.
        if (rowsDeleted != 0) {
            // Notify all listeners that the data has changed for the book content URI.
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted.
        return rowsDeleted;
    }
}
