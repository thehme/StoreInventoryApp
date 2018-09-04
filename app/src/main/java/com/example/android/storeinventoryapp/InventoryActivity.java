package com.example.android.storeinventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.storeinventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.storeinventoryapp.data.InventoryDbHelper;

public class InventoryActivity extends AppCompatActivity {
    private final static String TAG = InventoryActivity.class.getSimpleName();
    private SQLiteDatabase db;
    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        mDbHelper = new InventoryDbHelper(this);
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
            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Qué cosas dice mi abuela!");
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 399);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, 1);
            values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, "Scholastic");
            values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, "1-800-770-4662");
//            values.put(InventoryEntry.COLUMN_PRODUCT_ISBN, "9780545328630");

//            InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
//            InventoryEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
//            InventoryEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
//            InventoryEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT, " +
//            InventoryEntry.COLUMN_SUPPLIER_PHONE + " TEXT, " +
//            InventoryEntry.COLUMN_PRODUCT_ISBN + " TEXT );";

            long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);
            if (newRowId != -1) {
//                displayDatabaseInfo();
                Log.i(TAG, "newRowId: " + newRowId);
            } else {
                Log.i(TAG, "Error inserting dummy data");
            }

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
}
