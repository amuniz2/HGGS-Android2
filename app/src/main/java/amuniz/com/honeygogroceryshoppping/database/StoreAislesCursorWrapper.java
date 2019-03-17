package amuniz.com.honeygogroceryshoppping.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Set;

import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.StoreTable;
import amuniz.com.honeygogroceryshoppping.model.Store;

public class StoreAislesCursorWrapper extends CursorWrapper {

    public StoreAislesCursorWrapper(Cursor cursor) {super(cursor);}

    public String getAisle()
    {
        return getString(getColumnIndex(StoreDbSchema.StoreGroceryAisleTable.Cols.GROCERY_AISLE));
    }

}
