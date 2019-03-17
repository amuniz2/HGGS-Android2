package amuniz.com.honeygogroceryshoppping.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.StoreTable;
import amuniz.com.honeygogroceryshoppping.model.Store;

public class StoreCursorWrapper extends CursorWrapper {

    public StoreCursorWrapper(Cursor cursor) {super(cursor);}

    public Store getStore()
    {
        Integer storeId = getInt(getColumnIndex(StoreTable.Cols.ID));
        String name = getString(getColumnIndex(StoreTable.Cols.STORE_NAME));

        return new Store(storeId, name);
    }

}
