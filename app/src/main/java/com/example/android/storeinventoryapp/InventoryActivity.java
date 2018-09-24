package com.example.android.storeinventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

        // setup floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

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

    private void updateItemFromInventory() {

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
                InventoryEntry.COLUMN_BOOK_ISBN
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

    public void onClickHandler(View view) {
        // get the row the clicked button is in
        LinearLayout grandParentRow = (LinearLayout) view.getParent().getParent();

        // get view containing book name, price, and isbn
        LinearLayout childView1 = (LinearLayout) grandParentRow.getChildAt(0);
        // get view containing button and quantity
        LinearLayout childView2 = (LinearLayout) grandParentRow.getChildAt(1);

        // get textViews for book name, price, and isbn
        TextView titleTextView = (TextView) childView1.getChildAt(0);
        TextView priceTextView = (TextView) childView1.getChildAt(1);
        TextView isbnTextView = (TextView) childView1.getChildAt(2);

        // get text values from textViews for name and price
        String bookTitle = titleTextView.getText().toString().trim();
        String bookPrice = priceTextView.getText().toString().trim();
        String bookIsbn = isbnTextView.getText().toString().trim();

        // get textView for book quantity
        TextView quantityTextView = (TextView) childView2.getChildAt(0);

        // get text value for quantity
        String bookQuantity = quantityTextView.getText().toString().trim();

        Log.i(TAG, "clicked button");
        Log.i(TAG, "book: " + bookTitle);
        Log.i(TAG, "isbn: " + bookIsbn);
        Log.i(TAG, "price: " + bookPrice);
        Log.i(TAG, "quantity: " + bookQuantity);
    }
}
