package com.example.android.storeinventoryapp.data;

import android.provider.BaseColumns;

public final class InventoryContract {
    private InventoryContract() {
    }

    public final static class InventoryEntry implements BaseColumns {
        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "product name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_SUPPLIER = "supplier";
        public final static String COLUMN_SUPPLIER_PHONE = "phone number";
        public final static String COLUMN_PRODUCT_DESCRIPTION = "description";

        public final static double PRODUCT_PRICE = 0;
        public final static int PRODUCT_QUANTITY = 0;
    }
}
