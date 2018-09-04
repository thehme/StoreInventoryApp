package com.example.android.storeinventoryapp.data;
import com.example.android.storeinventoryapp.data.InventoryContract.InventoryEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDbHelper extends SQLiteOpenHelper {
    public final static int DATABASE_VERSION = 1;
    public final static String DATABASE_NAME = "inventory.db";

    private final static String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + InventoryEntry.TABLE_NAME + " (" +
            InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
            InventoryEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
            InventoryEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
            InventoryEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT, " +
            InventoryEntry.COLUMN_SUPPLIER_PHONE + " TEXT, " +
            InventoryEntry.COLUMN_PRODUCT_DESCRIPTION + " TEXT );";

    private final static String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
