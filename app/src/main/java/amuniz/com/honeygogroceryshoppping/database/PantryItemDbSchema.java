package amuniz.com.honeygogroceryshoppping.database;

public class PantryItemDbSchema {
    public static final class PantryItemTable
    {
        public static final String NAME = "pantryItems";

        public static final class Cols {
            public static final String ID = "pantry_item_id";
            public static final String NAME = "name";
            public static final String DESCRIPTION = "description";
            public static final String SELECT_BY_DEFAULT = "select_by_default";
            public static final String DEFAULT_QUANTITY = "default_quantity";
            public static final String UNITS = "units";
        }

    }
}
