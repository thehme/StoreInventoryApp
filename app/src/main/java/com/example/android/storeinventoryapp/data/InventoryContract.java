package com.example.android.storeinventoryapp.data;

import android.provider.BaseColumns;

public final class InventoryContract {
    private InventoryContract() {
    }

    public final static class InventoryEntry implements BaseColumns {
        public final static String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME = "title";
        public final static String COLUMN_BOOK_PRICE_CENTS = "price_cents";
        public final static String COLUMN_BOOK_QUANTITY = "quantity";
        public final static String COLUMN_BOOK_SUPPLIER = "supplier_name";
        public final static String COLUMN_SUPPLIER_PHONE = "phone_number";
        public final static String COLUMN_BOOK_ISBN = "isbn";
        public final static String COLUMN_BOOK_CONDITION = "condition";

        public final static int BOOK_CONDITION_NEW = 1;
        public final static int BOOK_CONDITION_USED = 0;
        public final static int BOOK_CONDITION_UNKNOWN = -1;
    }
}
