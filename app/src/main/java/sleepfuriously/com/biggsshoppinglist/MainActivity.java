package sleepfuriously.com.biggsshoppinglist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //------------------
    //  Constants
    //------------------

    private static final String TAG = MainActivity.class.getSimpleName();


    //------------------
    //  Widgets
    //------------------

    /** A View for Snackbars to launch with */
    private View m_snackbarHack;

    /** where the users type in their shopping list item */
    private EditText m_entry_et;

    private RecyclerView m_shoppingList_rv;


    //------------------
    //  Data
    //------------------

    private SQLiteDatabase m_db;

    private ShoppingListAdapter m_shoppingListAdapter;


    //------------------
    //  Methods
    //------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // -- setup data -- //

        DBHelper dbHelper = new DBHelper(this);
        m_db = dbHelper.getWritableDatabase();

        m_shoppingListAdapter = new ShoppingListAdapter(this, getAllItems());

        // -- get widgets -- //

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        m_snackbarHack = toolbar;   // HACK!

        m_entry_et = findViewById(R.id.added_item_et);

        m_shoppingList_rv = findViewById(R.id.shopping_list_rv);
        m_shoppingList_rv.setHasFixedSize(true);
        m_shoppingList_rv.setLayoutManager(new LinearLayoutManager(this));
        m_shoppingList_rv.setAdapter(m_shoppingListAdapter);

        // Handle left and right swipes with this ItemTouchHelper
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback (
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // todo  Maybe allow user to re-arrange the list
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder vh, int direction) {

                int pos = vh.getAdapterPosition();
                long id = (long) vh.itemView.getTag();

                removeItem(pos, id);
            }
        }).attachToRecyclerView(m_shoppingList_rv);     // Connects the TouchHelper to our RV


        Button add_butt = findViewById(R.id.add_butt);
        add_butt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String entry = m_entry_et.getText().toString().trim();
                if (entry.length() > 0) {
                    add(entry);
                    m_entry_et.getText().clear();   // Clear the space for next entry
                }
            }
        });

    }

    @Override
    protected void onDestroy() {

        m_db.close();

        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        String toast_str;

        switch (id) {
            case R.id.remove_all:
                toast_str = "remove_all";
                break;

            case R.id.remove_checked:
                toast_str = "remove checked";
                break;

            case R.id.action_settings:
                toast_str = "settings";
                break;

            case R.id.order_alphabetical:
                toast_str = "alphabetical";
                break;

            case R.id.order_checked:
                toast_str = "checked first";
                break;

            case R.id.order_unchecked:
                toast_str = "unchecked first";
                break;

            case R.id.order_newest:
                toast_str = "newest first";
                break;

            case R.id.order_oldest:
                toast_str = "oldest first";
                break;

            default:
                return super.onOptionsItemSelected(item);

        }

//        Snackbar.make(m_snackbarHack, toast_str, Snackbar.LENGTH_SHORT).show();
        Toast.makeText(this, toast_str, Toast.LENGTH_SHORT).show(); // toast looks better here
        return true;
    }


    /**
     * Add the given string to our shopping list
     *
     * @param str   The entry to add to our shopping list.
     *              Should be valid and already trimmed.
     *
     * preconditions:
     *      m_shoppingList is setup and ready for modification
     *      m_shoppingListAdapter ready to use
     *      m_shoppingList_rv is ready to use (indirectly accessed)
     */
    private void add (String str) {

//        Log.d(TAG, "adding " + str);

        // by default, new shopping items are already checked as false
        ShoppingItem item = new ShoppingItem();
        item.item_str = str;

        ContentValues cv = new ContentValues();
        cv.put(DBContracts.ShoppingListContract.COL_DATA, item.item_str);
        cv.put(DBContracts.ShoppingListContract.COL_CHECKED, item.checked);

        m_db.insert(DBContracts.ShoppingListContract.TABLE_NAME, null, cv);

        m_shoppingListAdapter.swapCursor(getAllItems());    // todo: not that efficient!

        // add item to the data and notifiy the adapter
//        m_shoppingList.add(item);
//        m_shoppingListAdapter.notifyItemInserted(m_shoppingList.size() - 1);
    }


    /**
     * Removes the item at the given position of the recyclerview's ADAPTER
     * (not the visible part--the whole schbang) from both the recyclerview
     * and the data (Cursor).
     *
     * @param pos   The position within the Adapter
     * @param id    The DB id of the item to remove.
     */
    private void removeItem (int pos, long id) {

        Log.d (TAG, "removeItem(" + pos + ", " + id + ") called");
        m_db.delete (
                DBContracts.ShoppingListContract.TABLE_NAME,
                DBContracts.ShoppingListContract._ID + "=" + id,
                null);
//        m_shoppingListAdapter.swapCursor(getAllItems());
        m_shoppingListAdapter.notifyItemRemoved(pos);
        m_shoppingList_rv.removeViewAt(pos);

        Log.d(TAG, "removeItem, now " + m_shoppingList_rv.getChildCount() + " items");
    }


    /**
     * Gets all items in the entire database.
     *
     * preconditions:
     *      m_db    ready to use
     *
     * @return  a Cursor loaded with all the data in the DB.
     */
    private Cursor getAllItems() {
        return m_db.query(
                DBContracts.ShoppingListContract.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DBContracts.ShoppingListContract.COL_TIMESTAMP + " DESC"    // Sort by DESCENDING order
        );
    }

}
