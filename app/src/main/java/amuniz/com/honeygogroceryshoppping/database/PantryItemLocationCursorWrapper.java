package amuniz.com.honeygogroceryshoppping.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import amuniz.com.honeygogroceryshoppping.model.PantryItemLocation;


public class PantryItemLocationCursorWrapper extends CursorWrapper {
    public PantryItemLocationCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public PantryItemLocation getPantryItemLocation()
    {
        Long pantryItemId = getLong(getColumnIndex(PantryItemLocationDbSchema.PantryItemLocationTable.Cols.PANTRY_ITEM_ID));
        Long locationId = getLong(getColumnIndex(PantryItemLocationDbSchema.PantryItemLocationTable.Cols.LOCATION_ID));

        return new PantryItemLocation(pantryItemId, locationId);
    }

}
