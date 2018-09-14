package com.example.android.storeinventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.storeinventoryapp.data.InventoryContract;
import com.example.android.storeinventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.storeinventoryapp.data.InventoryDbHelper;
import java.text.DecimalFormat;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static String TAG = InventoryActivity.class.getSimpleName();
    private static final int URL_LOADER = 0;
    private SQLiteDatabase db;
    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        mDbHelper = new InventoryDbHelper(this);
        displayDatabaseEntries();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    private void insertItemToInventory() {
        // get db in writable mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

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

            long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);
            if (newRowId != -1) {
                displayDatabaseEntries();
                Log.i(TAG, "newRowId: " + newRowId);
            } else {
                Log.i(TAG, "Error inserting dummy data");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error inserting data into db: " + e);
        }
    }

    private void displayDatabaseEntries() {
        mDbHelper = new InventoryDbHelper(this);

        // need db in readable mode
        db = mDbHelper.getReadableDatabase();

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

        // create a cursor of the db with query string retrieving the above projection
        Cursor cursor = db.query(InventoryEntry.TABLE_NAME, projection, null, null, null, null, null);

        try {
            // find TextView with id inventory_text_view to temporarily display data
            TextView inventoryTextView = (TextView) findViewById(R.id.inventory_text_view);
            // use getCount cursor method to display today number of rows in db
            inventoryTextView.setText("Number of rows in books database table: " + cursor.getCount() + "\n\n");
            // append row with table column names, omitting supplier info for this exercise
            inventoryTextView.append(InventoryEntry._ID + "\t | " +
            InventoryEntry.COLUMN_BOOK_NAME + " | " +
            InventoryEntry.COLUMN_BOOK_PRICE + " | " +
            InventoryEntry.COLUMN_BOOK_QUANTITY + " | " +
            InventoryEntry.COLUMN_BOOK_ISBN + " | " +
            InventoryEntry.COLUMN_BOOK_CONDITION + "\n");

            // get column indices
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int bookNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_NAME);
            int bookPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_PRICE_CENTS);
            int bookQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_QUANTITY);
            int bookIsbnColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_ISBN);
            int bookConditionColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_CONDITION);

            // traverse cursor and display each book's info
            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(idColumnIndex);
                String currentBookName = cursor.getString(bookNameColumnIndex);
                int currentPriceCents = cursor.getInt(bookPriceColumnIndex);
                DecimalFormat formatter = new DecimalFormat("##.00");
                double currentBookPrice = currentPriceCents / 100.0;
                String currentPrice = formatter.format(currentBookPrice);
                String currentQuantity = Integer.toString(cursor.getInt(bookQuantityColumnIndex));
                String currentIsnb = Integer.toString(cursor.getInt(bookIsbnColumnIndex));
                String currentCondition = cursor.getString(bookConditionColumnIndex);
                inventoryTextView.append("\n" + currentId + " | "
                        + currentBookName + " | "
                        + currentPrice + " | "
                        + currentQuantity + " | "
                        + currentIsnb + " | "
                        + currentCondition);
            }
        } finally {
            // always dispose of cursor
            cursor.close();
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        String[] projection = {
            InventoryEntry.COLUMN_BOOK_NAME,
            InventoryEntry.COLUMN_BOOK_PRICE_CENTS,
            InventoryEntry.COLUMN_BOOK_PRICE,
            InventoryEntry.COLUMN_BOOK_QUANTITY,
            InventoryEntry.COLUMN_BOOK_SUPPLIER,
            InventoryEntry.COLUMN_SUPPLIER_PHONE,
            InventoryEntry.COLUMN_BOOK_ISBN,
            InventoryEntry.COLUMN_BOOK_CONDITION
        };

        switch (loaderId) {
            case URL_LOADER:
                return new CursorLoader(
                        this,
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
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
