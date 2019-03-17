package amuniz.com.honeygogroceryshoppping.database;

public class ShoppingItemDbSchema {
    public static final class ShoppingItemTable
    {
        public static final String NAME = "shoppingItems";

        public static final class Cols {
            public static final String PANTRY_ITEM_ID = "pantry_item_id";
            public static final String SELECTED = "selected";
            public static final String IN_CART = "in_cart";
            public static final String QUANTITY = "quantity";
            public static final String UNITS = "units";
        }

    }
}
