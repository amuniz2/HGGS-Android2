package amuniz.com.honeygogroceryshoppping.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.StoreGrocerySectionTable;

public class StoreGrocerySectionsCursorWrapper extends CursorWrapper {

    public StoreGrocerySectionsCursorWrapper(Cursor cursor) {super(cursor);}

    public String getGrocerySection()
    {
        return getString(getColumnIndex(StoreGrocerySectionTable.Cols.GROCERY_SECTION));
    }

}
