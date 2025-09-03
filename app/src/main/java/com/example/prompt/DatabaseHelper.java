package com.example.prompt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shopping_list.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_DATE = "date";

    public static final String STATUS_PENDING = "pendiente de compra";
    public static final String STATUS_BOUGHT = "comprado";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_STATUS + " TEXT,"
                + COLUMN_DATE + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public void addProduct(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_STATUS, STATUS_PENDING);
        values.put(COLUMN_DATE, getCurrentDate());
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                );
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return productList;
    }

    public void updateProductStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        db.update(TABLE_PRODUCTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Product> getProductsByFilter(String status, String date) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (status != null && !status.isEmpty() && !status.equals("Todos")) {
            selection += COLUMN_STATUS + " = ?";
            selectionArgs.add(status);
        }
        if (date != null && !date.isEmpty()) {
            if (!selection.isEmpty()) {
                selection += " AND ";
            }
            selection += COLUMN_DATE + " = ?";
            selectionArgs.add(date);
        }

        Cursor cursor = db.query(TABLE_PRODUCTS, null, selection, selectionArgs.toArray(new String[0]), null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                );
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return productList;
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}

