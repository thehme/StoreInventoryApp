package com.example.android.storeinventoryapp;

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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.storeinventoryapp.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri mCurrentBookUri;

    // fields to enter book data
    private EditText mBookTitleEditText;
    private EditText mBookPriceDollarsCents;
    private EditText mBookQuantity;
    private EditText mBookSupplierName;
    private EditText mBookSupplierPhoneNumber;
    private EditText mBookISBN;
    private Spinner mBookConditionSpinner;
    private int mBookCondition;

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
        mBookConditionSpinner = (Spinner) findViewById(R.id.spinner_condition);

        // setup spinner containing condition options
        setupSpinner();
    }

    private void setupSpinner() {
        // create adapter for spinner
        // use array of strings for the options
        ArrayAdapter conditionSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.array_condition_options,
                android.R.layout.simple_spinner_dropdown_item
        );
        // specify dropdown layout style - simple list view with 1 item per line
        conditionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // apply the adapter to the spinner
        mBookConditionSpinner.setAdapter(conditionSpinnerAdapter);

        // set the integer mSelected to the constant values for book condition
        mBookConditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.condition_new))) {
                        mBookCondition = InventoryEntry.BOOK_CONDITION_NEW;
                    } else if (selection.equals(getString(R.string.condition_used))) {
                        mBookCondition = InventoryEntry.BOOK_CONDITION_USED;
                    } else {
                        mBookCondition = InventoryEntry.BOOK_CONDITION_UNKNOWN;
                    }
                }
            }
            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBookCondition = InventoryEntry.BOOK_CONDITION_UNKNOWN;
            }
        });
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

    public void saveBook() {

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
                InventoryEntry.COLUMN_BOOK_ISBN,
                InventoryEntry.COLUMN_BOOK_CONDITION
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
