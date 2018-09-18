package com.example.android.storeinventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.android.storeinventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.storeinventoryapp.data.InventoryDbHelper;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private final static String TAG = InventoryActivity.class.getSimpleName();
    private SQLiteDatabase db;
    private InventoryDbHelper mDbHelper;
    private static final int URL_LOADER = 0;
    private InventoryCursorAdaptor inventoryCursorAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // find reference to list view
        ListView listView = (ListView) findViewById(R.id.books_list);

        inventoryCursorAdaptor = new InventoryCursorAdaptor(this, null);
        listView.setAdapter(inventoryCursorAdaptor);

        // initialize loader
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    private void insertItemToInventory() {
        try {
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_BOOK_NAME, "Qué cosas dice mi abuela!");
            values.put(InventoryEntry.COLUMN_BOOK_PRICE_CENTS, 399);
            values.put(InventoryEntry.COLUMN_BOOK_QUANTITY, 1);
            values.put(InventoryEntry.COLUMN_BOOK_SUPPLIER, "Scholastic");
            values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, "1-800-770-4662");
            values.put(InventoryEntry.COLUMN_BOOK_ISBN, "9780545328630");
            values.put(InventoryEntry.COLUMN_BOOK_CONDITION, InventoryEntry.BOOK_CONDITION_NEW);

            Log.i(TAG, "values: " + values.toString());
            Uri uri = getContentResolver().insert(
                    InventoryEntry.CONTENT_URI,
                    values
            );
        } catch (Exception e) {
            Log.e(TAG, "Error inserting data into db: " + e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertItemToInventory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        // create projection array with all columns needed from db
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_BOOK_NAME,
                InventoryEntry.COLUMN_BOOK_PRICE_CENTS,
                InventoryEntry.COLUMN_BOOK_QUANTITY,
                InventoryEntry.COLUMN_BOOK_SUPPLIER,
                InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryEntry.COLUMN_BOOK_ISBN,
                InventoryEntry.COLUMN_BOOK_CONDITION
        };
        switch (loaderId) {
            case URL_LOADER:
                return new CursorLoader(this,
                        InventoryEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        inventoryCursorAdaptor.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        inventoryCursorAdaptor.swapCursor(null);
    }
}
