package com.example.android.storeinventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.storeinventoryapp.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = EditorActivity.class.getSimpleName();
    private Uri mCurrentBookUri;

    // fields to enter book data
    private EditText mBookTitleEditText;
    private EditText mBookPriceDollarsCents;
    private EditText mBookQuantity;
    private EditText mBookSupplierName;
    private EditText mBookSupplierPhoneNumber;
    private EditText mBookISBN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri != null) {
            setTitle(R.string.edit_book);
        } else {
            setTitle(R.string.add_book);
        }

        // find all relevant views from which data will be read from
        mBookTitleEditText = (EditText) findViewById(R.id.edit_book_name);
        mBookPriceDollarsCents = (EditText) findViewById(R.id.edit_book_price);
        mBookQuantity = (EditText) findViewById(R.id.edit_book_quantity);
        mBookSupplierName = (EditText) findViewById(R.id.edit_supplier_name);
        mBookSupplierPhoneNumber = (EditText) findViewById(R.id.edit_supplier_phone_number);
        mBookISBN = (EditText) findViewById(R.id.edit_book_isbn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the options menu from the /res/menu/menu_editor.xml file
        // to add menu item in the nav bar
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // depending on menu option selected, take action
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            case R.id.action_delete:
                return true;
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int convertPriceToCents(String dollarCents) {
        String[] splitPriceStrings = dollarCents.split("\\.");
        int priceInCents = 0;
        Log.i(TAG, "price: " + Integer.toString(priceInCents));
        priceInCents += (Integer.parseInt(splitPriceStrings[0]) * 100);
        Log.i(TAG, "price: " + Integer.toString(priceInCents));
        priceInCents += Integer.parseInt(splitPriceStrings[1]);
        Log.i(TAG, "price: " + Integer.toString(priceInCents));
        return priceInCents;
    }

    public void saveBook() {
        String titleString = mBookTitleEditText.getText().toString().trim();
        String priceInDollarsCents = mBookPriceDollarsCents.getText().toString().trim();
        Log.i(TAG,"price string: " + priceInDollarsCents);
        int priceInCents = convertPriceToCents(priceInDollarsCents);
        String bookQuantity = mBookQuantity.getText().toString().trim();
        String bookSupplierName = mBookSupplierName.getText().toString().trim();
        String bookSupplierPhoneNumber = mBookSupplierPhoneNumber.getText().toString().trim();
        String bookISBN = mBookISBN.getText().toString().trim();

        try {
            // create content values
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_BOOK_NAME, titleString);
            values.put(InventoryEntry.COLUMN_BOOK_PRICE_CENTS, priceInCents);
            values.put(InventoryEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
            values.put(InventoryEntry.COLUMN_BOOK_SUPPLIER, bookSupplierName);
            values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, bookSupplierPhoneNumber);
            values.put(InventoryEntry.COLUMN_BOOK_ISBN, bookISBN);

            // save or update book accordingly
            if (mCurrentBookUri == null) {
                Log.i(TAG, "insert new book");
                Uri uri = getContentResolver().insert(
                        InventoryEntry.CONTENT_URI,
                        values
                );
                if (uri == null) {
                    Log.i(TAG, "Error inserting data");
                    Toast.makeText(this, R.string.editor_insert_failure, Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "New data inserted");
                    Toast.makeText(this, R.string.editor_insert_success, Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.i(TAG, "update existing book");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error saving to db", e);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        // create projection array with all columns needed to store to db
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_BOOK_NAME,
                InventoryEntry.COLUMN_BOOK_PRICE_CENTS,
                InventoryEntry.COLUMN_BOOK_QUANTITY,
                InventoryEntry.COLUMN_BOOK_SUPPLIER,
                InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryEntry.COLUMN_BOOK_ISBN
        };
        return new CursorLoader(
                this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
