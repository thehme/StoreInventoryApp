package com.example.android.storeinventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.storeinventoryapp.data.InventoryContract.InventoryEntry;

import java.text.DecimalFormat;

public class InventoryCursorAdaptor extends CursorAdapter {
    private static final String TAG = InventoryCursorAdaptor.class.getSimpleName();

    public InventoryCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method bind the inventory data (in the current row pointed to by
     * the cursor) to the given list item layout, allowing the book name to be set
     * on the text view with id book_name, etc.
     *
     * @param view      Existing view
     * @param context   app context
     * @param cursor    cursor from which to get data
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // find sale button
        final Button saleButton = (Button) view.findViewById(R.id.sale_book_button);

        // find list item text views were data will be populated
        TextView bookName = (TextView) view.findViewById(R.id.book_name);
        TextView bookPrice = (TextView) view.findViewById(R.id.book_price);
        TextView bookQuantity = (TextView) view.findViewById(R.id.book_quantity);

        // get data from current cursor item
        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_BOOK_NAME));
        int bookQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_QUANTITY);
        int bookQuantityInt = cursor.getInt(bookQuantityColumnIndex);
        if (bookQuantityInt == 1) {
            saleButton.setEnabled(false);
        }
        final String bookQuantityAvailable = Integer.toString(bookQuantityInt);
        int bookPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_PRICE_CENTS);
        int currentPriceCents = cursor.getInt(bookPriceColumnIndex);
        String price = calculateFormattedPrice(currentPriceCents);
        price = "$" + price;


        // populate textViews with data
        bookName.setText(name);
        bookPrice.setText(price);
        bookQuantity.setText(bookQuantityAvailable);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // find book id based on cursor
                int bookId = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry._ID));
                // create uri to match book by id
                Uri mCurrentBookUri = Uri.withAppendedPath(InventoryEntry.CONTENT_URI, Integer.toString(bookId));
                // get book quantity
                int bookQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_QUANTITY);
                int bookQuantityInt = cursor.getInt(bookQuantityColumnIndex);

                Log.i(TAG, "book id: " + Integer.toString(bookId));
                Log.i(TAG, "book uri: " + mCurrentBookUri.toString());
                if (bookQuantityInt > 1) {
                    ContentValues values = new ContentValues();
                    bookQuantityInt = bookQuantityInt - 1;
                    values.put(InventoryEntry.COLUMN_BOOK_QUANTITY, bookQuantityInt);

                    int numUpdated = context.getContentResolver().update(
                            mCurrentBookUri,
                            values,
                            null,
                            null
                    );

                    if (numUpdated > 0) {
                        Log.i(TAG, "updated quantity of " + numUpdated + " book");
                    }
                }
            }
        });
    }

    private String calculateFormattedPrice(int priceInCents) {
        DecimalFormat formatter = new DecimalFormat("##.00");
        double currentBookPrice = priceInCents / 100.0;
        return formatter.format(currentBookPrice);
    }
}
