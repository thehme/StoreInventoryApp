package com.example.android.storeinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.storeinventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryProvider extends ContentProvider {
    public static final String TAG = InventoryProvider.class.getSimpleName();
    private InventoryDbHelper inventoryDbHelper;

    // this cursor will hold the result of the query
    private Cursor cursor;

    // uri codes
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;

    // define uri matcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, BOOKS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", BOOK_ID);
    }

    /**
     * To initialize the content provider and db helper object
     * @return true
     */
    @Override
    public boolean onCreate() {
        // initialize db helper object to gain access to pets db
        inventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable db
        SQLiteDatabase database = inventoryDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // query directly with the parameters provider
                // cursor will be returned with all data
                cursor = database.query(InventoryEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                // setup query string
                selection = InventoryEntry._ID + "=?";
                // get uri id to use in query string
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // perform query on db for single item
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // set listener up for change in db
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                Uri uriResult = insertBook(uri, contentValues);
                return uriResult;
            default:
                // can't insert on row where pet already exists
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the db with the given content values.
     * @param uri
     * @param values
     * @return new content URI for the specified row in the db
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        SQLiteDatabase database = inventoryDbHelper.getWritableDatabase();

        long id = database.insert(
                InventoryEntry.TABLE_NAME,
                null,
                values
        );
        if (id == -1) {
            Log.e(TAG, "Error inserting new book into db for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
