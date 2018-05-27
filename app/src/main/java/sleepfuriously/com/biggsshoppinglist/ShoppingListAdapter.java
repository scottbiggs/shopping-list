package sleepfuriously.com.biggsshoppinglist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import sleepfuriously.com.biggsshoppinglist.DBContracts.*;


/**
 * Adapter for using the Shopping List RecyclerView
 */
public class ShoppingListAdapter
        extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>{

    //----------------------
    //  Constants
    //----------------------

    private static final String TAG = ShoppingListAdapter.class.getSimpleName();

    //----------------------
    //  Data
    //----------------------

    private Context m_ctx;

    private Cursor m_cursor;


    //----------------------
    //  Methods
    //----------------------

    public ShoppingListAdapter (Context ctx, Cursor cursor) {

        m_ctx = ctx;
        m_cursor = cursor;
    }


    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Create a new View, setting the contents is done in onBindViewHolder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_entry, parent, false);
        return new ShoppingListViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int pos) {

        if (m_cursor.moveToPosition(pos) == false) {
            Log.e(TAG, "Illegal position in onBindViewHolder()!");
            return; //
        }

        // get data from our Cursor
        String str = m_cursor.getString(m_cursor.getColumnIndex(ShoppingListContract.COL_DATA));
        int tmp = m_cursor.getInt(m_cursor.getColumnIndex(ShoppingListContract.COL_CHECKED)) ;
        boolean checked = (tmp == 1);

        long id = m_cursor.getLong(m_cursor.getColumnIndex(ShoppingListContract._ID));

        // apply data to UI
        holder.checkBox.setText(str);
        holder.checkBox.setChecked(checked);
        holder.itemView.setTag(id);     // Connect the db's id with the UI by saving it in the Tag.
    }

    @Override
    public int getItemCount() {
        return m_cursor.getCount();
    }


    public void swapCursor (Cursor cursor) {

        if (m_cursor != null) {
            m_cursor.close();
        }

        m_cursor = cursor;
        if (m_cursor != null) {
            notifyDataSetChanged();     // todo: This may not be that efficient!!!
        }
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //  Classes
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    class ShoppingListViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = ShoppingListViewHolder.class.getSimpleName();

        CheckBox checkBox;

        public ShoppingListViewHolder(View v) {
            super(v);
            checkBox = v.findViewById(R.id.entry_cb);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                // To provide strike-thru support
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int flags = buttonView.getPaintFlags();

                    DisplayMetrics metrics = m_ctx.getResources().getDisplayMetrics();
                    float density = metrics.density;
                    float text_size = checkBox.getTextSize() / density;

                    if (isChecked) {
                        flags |= Paint.STRIKE_THRU_TEXT_FLAG;
                        // reduce the text size by 4
                        text_size -= 3;
                    }
                    else {
                        flags &= ~Paint.STRIKE_THRU_TEXT_FLAG;
                        text_size += 3;
                    }
                    buttonView.setPaintFlags(flags);
                    checkBox.setTextSize(text_size);
                }
            });
        }

    }
}
