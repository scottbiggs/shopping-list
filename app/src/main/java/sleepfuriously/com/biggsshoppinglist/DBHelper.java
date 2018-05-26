package sleepfuriously.com.biggsshoppinglist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import sleepfuriously.com.biggsshoppinglist.DBContracts.*;

/**
 * Provides access to the database(s).
 */
public class DBHelper extends SQLiteOpenHelper {

    //----------------------------------
    //  Constants
    //----------------------------------

    /** filename for our DB */
    public static final String DATABASE_FILENAME = "shoppinglist.db";

    /** Current Version */
    public static final int DATABASE_VERSION = 1;


    //----------------------------------
    //  Methods
    //----------------------------------

    // I removed many of the constructor's default params because I don't need the other elements
    public DBHelper(Context context) {

        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String create_str =
            "CREATE TABLE " +
            ShoppingListContract.TABLE_NAME + " (" +
            ShoppingListContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ShoppingListContract.COL_DATA + " TEXT NOT NULL, " +
            ShoppingListContract.COL_CHECKED + " INTEGER NOT NULL, " +
            ShoppingListContract.COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");";

        db.execSQL(create_str);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + ShoppingListContract.TABLE_NAME);
        onCreate(db);
    }

}
