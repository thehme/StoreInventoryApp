package com.example.android.storeinventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void bindView(View view, Context context, Cursor cursor) {
        // find list item text views were data will be populated
        TextView bookName = (TextView) view.findViewById(R.id.book_name);
        TextView bookPrice = (TextView) view.findViewById(R.id.book_price);
        TextView bookQuantity = (TextView) view.findViewById(R.id.book_quantity);

        // get data from current cursor item
        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_BOOK_NAME));

        int bookQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_QUANTITY);
        String bookQuantityAvailable = Integer.toString(cursor.getInt(bookQuantityColumnIndex));

        int bookPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_PRICE_CENTS);
        int currentPriceCents = cursor.getInt(bookPriceColumnIndex);
        String price = calculateFormattedPrice(currentPriceCents);
        price = "$" + price;

        int isbnColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_ISBN);
        int currentIsbn = cursor.getInt(isbnColumnIndex);
        Object data = new Object();
        data.setName = "isbn";
        data.setId = 

        // populate textViews with data
        bookName.setText(name);
        bookName.setTag(currentIsbn);
        bookPrice.setText(price);
        bookQuantity.setText(bookQuantityAvailable);
    }
    private String calculateFormattedPrice(int priceInCents) {
        DecimalFormat formatter = new DecimalFormat("##.00");
        double currentBookPrice = priceInCents / 100.0;
        return formatter.format(currentBookPrice);
    }
}
