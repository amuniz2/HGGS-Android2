package amuniz.com.honeygogroceryshoppping.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.LocationTable;
import amuniz.com.honeygogroceryshoppping.model.StoreLocation;

public class StoreLocationCursorWrapper extends CursorWrapper {
   public StoreLocationCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public StoreLocation getStoreLocation()
    {
        long storeId = getLong(getColumnIndex(LocationTable.Cols.STORE_ID));
        long id = getLong(getColumnIndex(LocationTable.Cols.ID));
        String aisle = getString(getColumnIndex(LocationTable.Cols.AISLE));
        String section = getString(getColumnIndex(LocationTable.Cols.SECTION_NAME));

        return new StoreLocation(id, storeId, aisle, section);
    }

}
