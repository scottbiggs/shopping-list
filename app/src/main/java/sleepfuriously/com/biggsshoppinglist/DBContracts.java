package sleepfuriously.com.biggsshoppinglist;

import android.provider.BaseColumns;

/**
 * Essentially a Header class for all our Databases.
 *
 * (Since this has just 1 database right now, it may seem kind
 * of over-kill.)
 */
public class DBContracts {

    /** Prevents accidental construction of this class */
    private DBContracts() {}


    /**
     * Holds all our stuff for DB access to the ShoppingList.
     *
     * @see ShoppingItem
     */
    public static final class ShoppingListContract
                implements BaseColumns {

        public static final String
            TABLE_NAME = "shoppingListTable",
            COL_DATA = "item",
            COL_CHECKED = "checked",
            COL_TIMESTAMP = "timestamp";
    }

}
