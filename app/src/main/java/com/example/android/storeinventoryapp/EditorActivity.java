package com.example.android.storeinventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.storeinventoryapp.data.InventoryContract.InventoryEntry;

import java.text.DecimalFormat;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = EditorActivity.class.getSimpleName();
    private static final int URL_LOADER = 0;
    // to keep track of whether data has been change or not
    private boolean mBookHasChanged = false;

    private Uri mCurrentBookUri;

    // fields to enter book data
    private EditText mBookTitleEditText;
    private EditText mBookPriceDollarsCentsEditText;
    private TextView mBookQuantityTextView;
    private int bookQuantity;
    private EditText mBookSupplierNameEditText;
    private EditText mBookSupplierPhoneNumberEditText;
    private String phoneNumber;
    private EditText mBookISBNEditText;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        final Button reOrderButton = (Button) findViewById(R.id.re_order_button);

        if (mCurrentBookUri != null) {
            setTitle(R.string.edit_book);
            reOrderButton.setVisibility(View.VISIBLE);
            // initialize loader
            getLoaderManager().initLoader(URL_LOADER, null, this);
        } else {
            setTitle(R.string.add_book);
            reOrderButton.setVisibility(View.GONE);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        }

        // find all relevant views from which data will be read from
        mBookTitleEditText = (EditText) findViewById(R.id.edit_book_name);
        mBookPriceDollarsCentsEditText = (EditText) findViewById(R.id.edit_book_price);
        mBookQuantityTextView = (TextView) findViewById(R.id.edit_book_quantity);
        mBookSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mBookSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);
        mBookISBNEditText = (EditText) findViewById(R.id.edit_book_isbn);

        mBookTitleEditText.setOnTouchListener(mTouchListener);
        mBookPriceDollarsCentsEditText.setOnTouchListener(mTouchListener);
        mBookQuantityTextView.setOnTouchListener(mTouchListener);
        mBookSupplierNameEditText.setOnTouchListener(mTouchListener);
        mBookSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mBookISBNEditText.setOnTouchListener(mTouchListener);

        final Button decreaseButton = (Button) findViewById(R.id.minus_button);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookQuantity >= 1) {
                    if (!decreaseButton.isEnabled()) {
                        decreaseButton.setEnabled(true);
                    }
                    bookQuantity = bookQuantity - 1;
                    displayQuantity(bookQuantity);
                } else {
                    decreaseButton.setEnabled(false);
                }
            }
        });

        final Button increaseButton = (Button) findViewById(R.id.plus_button);
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookQuantity = bookQuantity + 1;
                displayQuantity(bookQuantity);
            }
        });

        reOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });
    }

    private void displayQuantity(int numberQuantity) {
        TextView quantityTextView = (TextView) findViewById(R.id.edit_book_quantity);
        quantityTextView.setText("" + numberQuantity);
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
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (mBookHasChanged) {
                    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded.
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, navigate to parent activity.
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            };
                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                } else {
                    // Navigate back to parent activity (InventoryActivity)
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        try {
            if (mCurrentBookUri != null) {
                int numDeleted = getContentResolver().delete(
                        mCurrentBookUri, null, null
                );
                if (numDeleted > 0) {
                    Log.i(TAG, "book deleted successfully");
                    Toast.makeText(this, R.string.editor_delete_book_successful, Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "failed to delete book");
                    Toast.makeText(this, R.string.editor_delete_book_failure, Toast.LENGTH_SHORT).show();
                }
            }
            // close activity
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting book");
        }
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private int convertPriceToCents(String dollarCents) {
        String[] splitPriceStrings = dollarCents.split("\\.");
        if (splitPriceStrings.length == 1 && splitPriceStrings[0].equals("0")) {
            return 0;
        } else {
            int priceInCents = 0;
            priceInCents += (Integer.parseInt(splitPriceStrings[0]) * 100);
            priceInCents += Integer.parseInt(splitPriceStrings[1]);
            return priceInCents;
        }
    }

    private boolean validateInputValues() {
        boolean inputsAreValid = true;
        String title = mBookTitleEditText.getText().toString().trim();
        if (inputsAreValid && TextUtils.isEmpty(title)) {
            Toast.makeText(this, R.string.editor_validate_title_error_message, Toast.LENGTH_SHORT).show();
            return false;
        }

        String priceString = mBookPriceDollarsCentsEditText.getText().toString().trim();
        if (inputsAreValid && TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, R.string.editor_validate_price_missing_message, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            int price = convertPriceToCents(priceString);
            if (inputsAreValid && price <= 0) {
                Toast.makeText(this, R.string.editor_validate_price_negative_message, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        String quantityString = mBookQuantityTextView.getText().toString().trim();
        if (inputsAreValid && TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, R.string.editor_validate_quantity_missing_message, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            int quantity = Integer.parseInt(quantityString);
            if (inputsAreValid && Double.isNaN(quantity) || quantity < 0) {
                Toast.makeText(this, R.string.editor_validate_quantity_invalid_message, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        String isbn = mBookISBNEditText.getText().toString().trim();
        if (inputsAreValid && TextUtils.isEmpty(isbn) || isbn.length() < 13) {
            Toast.makeText(this, R.string.editor_validate_isbn_invalid_message, Toast.LENGTH_SHORT).show();
            return false;
        }

        String bookSupplier = mBookSupplierNameEditText.getText().toString().trim();
        if (inputsAreValid && TextUtils.isEmpty(bookSupplier)) {
            Toast.makeText(this, R.string.editor_validate_supplier_name_invalid_message, Toast.LENGTH_SHORT).show();
            return false;
        }

        String bookSupplierPhoneNumber = mBookSupplierPhoneNumberEditText.getText().toString().trim();
        if (inputsAreValid && TextUtils.isEmpty(bookSupplierPhoneNumber)) {
            Toast.makeText(this, R.string.editor_validate_supplier_phone_invalid_message, Toast.LENGTH_SHORT).show();
            return false;
        }
        return inputsAreValid;
    }

    public void saveBook() {
        if (validateInputValues()) {
            try {
                // retrieve values from input fields
                String titleString = mBookTitleEditText.getText().toString().trim();
                String priceInDollarsCents = mBookPriceDollarsCentsEditText.getText().toString().trim();
                int priceInCents = convertPriceToCents(priceInDollarsCents);
                String bookQuantity = mBookQuantityTextView.getText().toString().trim();
                String bookSupplierName = mBookSupplierNameEditText.getText().toString().trim();
                String bookSupplierPhoneNumber = mBookSupplierPhoneNumberEditText.getText().toString().trim();
                String bookISBN = mBookISBNEditText.getText().toString().trim();

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
                    Uri uri = getContentResolver().insert(
                            InventoryEntry.CONTENT_URI,
                            values
                    );
                    if (uri == null) {
                        Toast.makeText(this, R.string.editor_insert_failure, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.editor_insert_success, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    int numUpdated = getContentResolver().update(
                            mCurrentBookUri,
                            values,
                            null,
                            null
                    );
                    if (numUpdated > 0) {
                        Toast.makeText(this, R.string.editor_update_success, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, R.string.editor_update_failure, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error saving to db", e);
            }
        } else {
            Log.e(TAG, "Error saving to db");
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

    private String calculateFormattedPrice(int priceInCents) {
        DecimalFormat formatter = new DecimalFormat("##.00");
        double currentBookPrice = priceInCents / 100.0;
        return formatter.format(currentBookPrice);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // get data from current cursor item
            String title = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_BOOK_NAME));
            mBookTitleEditText.setText(title);

            int priceInCents = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_BOOK_PRICE_CENTS));
            String price = calculateFormattedPrice(priceInCents);
            mBookPriceDollarsCentsEditText.setText(price);

            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_BOOK_QUANTITY));
            String quantityString = Integer.toString(quantity);
            mBookQuantityTextView.setText(quantityString);
            bookQuantity = Integer.parseInt(quantityString);

            String supplierName = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_BOOK_SUPPLIER));
            mBookSupplierNameEditText.setText(supplierName);

            String supplierPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_SUPPLIER_PHONE));
            phoneNumber = supplierPhoneNumber;
            mBookSupplierPhoneNumberEditText.setText(supplierPhoneNumber);

            String isbn = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_BOOK_ISBN));
            mBookISBNEditText.setText(isbn);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookTitleEditText.setText("");
        mBookPriceDollarsCentsEditText.setText("");
        mBookQuantityTextView.setText("");
        mBookSupplierNameEditText.setText("");
        mBookSupplierPhoneNumberEditText.setText("");
        mBookISBNEditText.setText("");
    }
}
