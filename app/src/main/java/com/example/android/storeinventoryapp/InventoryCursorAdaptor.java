package com.example.android.storeinventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.storeinventoryapp.data.InventoryContract.InventoryEntry;

import java.text.DecimalFormat;

import static com.example.android.storeinventoryapp.data.InventoryContract.InventoryEntry._ID;

public class InventoryCursorAdaptor extends CursorAdapter {
    private static final String TAG = InventoryCursorAdaptor.class.getSimpleName();

    public InventoryCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    TextView bookQuantity;
    String bookQuantityAvailable;
    /**
     * This method bind the inventory data (in the current row pointed to by
     * the cursor) to the given list item layout, allowing the book name to be set
     * on the text view with id book_name, etc.
     *
     * @param view    Existing view
     * @param context app context
     * @param cursor  cursor from which to get data
     */
    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        // find list item text views were data will be populated
        TextView bookName = (TextView) view.findViewById(R.id.book_name);
        TextView bookPrice = (TextView) view.findViewById(R.id.book_price);
        bookQuantity = (TextView) view.findViewById(R.id.book_quantity);

        // get data from current cursor item
        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_BOOK_NAME));
        int bookQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_QUANTITY);
        bookQuantityAvailable = Integer.toString(cursor.getInt(bookQuantityColumnIndex));
        int bookPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_PRICE_CENTS);
        int currentPriceCents = cursor.getInt(bookPriceColumnIndex);

        //int idColumnIndex = cursor.getColumnIndexOrThrow(_ID);
        //final int bookId = cursor.getInt(idColumnIndex);
        String price = calculateFormattedPrice(currentPriceCents);
        price = "$" + price;


        // populate textViews with data
        bookName.setText(name);
        bookPrice.setText(price);
        bookQuantity.setText(bookQuantityAvailable);

        int currentId = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        // Make the content uri for the current Id
        final Uri contentUri = Uri.withAppendedPath(InventoryEntry.CONTENT_URI, Integer.toString(currentId));

        Button button = (Button) view.findViewById(R.id.sale_book_button);
        // Change the quantity when you click the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView quantityView = (TextView) view.findViewById(R.id.book_quantity);
                int quantity = Integer.valueOf(quantityView.getText().toString());

                if (quantity > 0) {
                    quantity = quantity - 1;
                }
                // Content Values to update quantity
                ContentValues values = new ContentValues();
                values.put(InventoryEntry.COLUMN_BOOK_QUANTITY, quantity);

                // update the database
                context.getContentResolver().update(contentUri, values, null, null);
            }
        });

    }

    private String calculateFormattedPrice(int priceInCents) {
        DecimalFormat formatter = new DecimalFormat("##.00");
        double currentBookPrice = priceInCents / 100.0;
        return formatter.format(currentBookPrice);
    }

}