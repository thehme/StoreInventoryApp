package com.example.android.storeinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class InventoryProvider extends ContentProvider {
    public static final String TAG = InventoryProvider.class.getSimpleName();
    private InventoryDbHelper inventoryDbHelper;

    // define cursor to hold result from query operation
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
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                break;
        }
        // set listener up for change in db
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
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
