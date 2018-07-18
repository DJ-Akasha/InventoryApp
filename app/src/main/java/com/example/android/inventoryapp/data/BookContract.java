package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {
    }

    /**
     * The "Content authority" to be used for the URI
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    /**
     * Base for all URI's which the app will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path appended to base content URI for possible URI's.
     */
    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

        /** The content URI to access the book data in the provider. */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * Name of database table for books.
         */
        public final static String TABLE_NAME = "books";

        /**
         * Unique ID number for the product (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the book.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "name";

        /**
         * Genre of the book.
         *
         * The only possible values are {@link #GENRE_UNKNOWN}, {@link #GENRE_FANTASY},
         * {@link #GENRE_SCI_FI}, {@link #GENRE_MYSTERY}, {@link #GENRE_ROMANCE},
         * {@link #GENRE_HORROR}, {@link #GENRE_ACTION_AND_ADVENTURE} or {@link #GENRE_DRAMA}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_GENRE = "genre";

        /**
         * Price of the book.
         * <p>
         * Type: REAL
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";

        /**
         * How much stock there is of the book.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        /**
         * Name of the supplier.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";

        /**
         * Phone number for the supplier.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone_number";

        /**
         * Possible values for the genre of the book.
         */
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENRE_FANTASY = 1;
        public static final int GENRE_SCI_FI = 2;
        public static final int GENRE_MYSTERY = 3;
        public static final int GENRE_ROMANCE = 4;
        public static final int GENRE_HORROR = 5;
        public static final int GENRE_ACTION_AND_ADVENTURE = 6;
        public static final int GENRE_DRAMA = 7;

        /**
         * Returns whether or not the given genre is {@link #GENRE_UNKNOWN}, {@link #GENRE_FANTASY},
         * {@link #GENRE_SCI_FI}, {@link #GENRE_MYSTERY}, {@link #GENRE_ROMANCE},
         * {@link #GENRE_HORROR}, {@link #GENRE_ACTION_AND_ADVENTURE} or {@link #GENRE_DRAMA}.
         */
        public static boolean isValidGenre(int genre) {
            if (genre == GENRE_UNKNOWN || genre == GENRE_FANTASY || genre == GENRE_SCI_FI ||
                    genre == GENRE_MYSTERY || genre == GENRE_ROMANCE || genre == GENRE_HORROR ||
                    genre == GENRE_ACTION_AND_ADVENTURE || genre == GENRE_DRAMA) {
                return true;
            }
            return false;
        }
    }
}
