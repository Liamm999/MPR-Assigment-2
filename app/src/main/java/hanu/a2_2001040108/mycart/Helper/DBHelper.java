package hanu.a2_2001040108.mycart.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import hanu.a2_2001040108.mycart.model.Product;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DB_NAME = "cartdb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "mycart";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "name";
    private static final String THUMBNAIL_COL = "thumbnail";
    private static final String PRICE_COL = "price";
    private static final String AMOUNT_COL = "amount";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, "
                + THUMBNAIL_COL + " TEXT,"
                + NAME_COL + " TEXT,"
                + PRICE_COL + " TEXT,"
                + AMOUNT_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addNewCourse(String itemId, String itemName, String itemThumbnail, String itemPrice, String itemAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ID_COL, itemId);
        values.put(NAME_COL, itemName);
        values.put(THUMBNAIL_COL, itemThumbnail);
        values.put(PRICE_COL, itemPrice);
        values.put(AMOUNT_COL, itemAmount);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    private Cursor getItemById(long id) {
        SQLiteDatabase db = new DBHelper(this.context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM mycart WHERE id=?", new String[]{String.valueOf(id)});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public List<Product> getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // on below line we are creating a new array list.
        List<Product> products = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorCourses.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                Product tempProduct = (new Product(
                        cursorCourses.getLong(0),
                        cursorCourses.getString(1),
                        cursorCourses.getString(2),
                        "",
                        cursorCourses.getInt(3)));
                tempProduct.setAmount(cursorCourses.getInt(4));

                products.add(tempProduct);

            } while (cursorCourses.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorCourses.close();
        return products;
    }

    public boolean ifItemExist(long id) {
        SQLiteDatabase db = new DBHelper(this.context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM mycart WHERE id=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public int getAmountItemsById(long id) {
        Cursor cursor = getItemById(id);
        int amount = cursor.getColumnIndex("amount");
        return cursor.getInt(amount);
    }

    public void editAmountItemsById(long id, int amount) {
        SQLiteDatabase db = new DBHelper(this.context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        db.update("mycart", values, "id=?", new String[]{String.valueOf(id)});
    }

    public Integer deleteItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
