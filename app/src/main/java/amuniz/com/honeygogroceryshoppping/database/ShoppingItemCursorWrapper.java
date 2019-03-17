package amuniz.com.honeygogroceryshoppping.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import amuniz.com.honeygogroceryshoppping.database.PantryItemDbSchema.PantryItemTable;
import amuniz.com.honeygogroceryshoppping.database.ShoppingItemDbSchema.ShoppingItemTable;
import amuniz.com.honeygogroceryshoppping.model.PantryItem;
import amuniz.com.honeygogroceryshoppping.model.PantryItemQuantity;
import amuniz.com.honeygogroceryshoppping.model.ShoppingItem;

public class ShoppingItemCursorWrapper extends CursorWrapper {
    public ShoppingItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public ShoppingItem getShoppingItem()
    {
        Long pantryItemId = getLong(getColumnIndex(ShoppingItemTable.Cols.PANTRY_ITEM_ID));
        Boolean selected = getInt(getColumnIndex(ShoppingItemTable.Cols.SELECTED)) != 0;
        Boolean inCart = getInt(getColumnIndex(ShoppingItemTable.Cols.IN_CART)) != 0;
        int quantity = getInt(getColumnIndex(ShoppingItemTable.Cols.QUANTITY));
        String unit = getString(getColumnIndex(ShoppingItemTable.Cols.UNITS));

//        PantryItemQuantity pantryItemQuantity = new PantryItemQuantity();
//        pantryItemQuantity.setQuantity(quantity);
//        pantryItemQuantity.setUnit(units);

        return new ShoppingItem(pantryItemId, inCart, quantity, unit, selected );
    }
}
