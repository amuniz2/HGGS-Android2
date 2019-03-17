package amuniz.com.honeygogroceryshoppping.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import amuniz.com.honeygogroceryshoppping.database.PantryItemDbSchema.PantryItemTable;
import amuniz.com.honeygogroceryshoppping.model.PantryItem;
import amuniz.com.honeygogroceryshoppping.model.PantryItemQuantity;

public class PantryItemCursorWrapper extends CursorWrapper {
    public PantryItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public PantryItem getPantryItem()
    {
        long id = getLong(getColumnIndex(PantryItemTable.Cols.ID));
        String name = getString(getColumnIndex(PantryItemTable.Cols.NAME));
        String description = getString(getColumnIndex(PantryItemTable.Cols.DESCRIPTION));
        Boolean selectByDefault = getInt(getColumnIndex(PantryItemTable.Cols.SELECT_BY_DEFAULT)) != 0;
        String units = getString(getColumnIndex(PantryItemTable.Cols.UNITS));
        int quantity = getInt(getColumnIndex(PantryItemTable.Cols.DEFAULT_QUANTITY));

        return new PantryItem(id, name, description, units, quantity, selectByDefault);
    }
}
