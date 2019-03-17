package amuniz.com.honeygogroceryshoppping.database;

public class StoreDbSchema {

    public static final class StoreTable {

        public static final String NAME = "stores";

        public static final class Cols {
            public static final String ID = "store_id";
            public static final String STORE_NAME = "store_name";
        }
    }

    public static final class StoreGrocerySectionTable {
        public static final String NAME = "storeGrocerySections";
        public static final class Cols {
            public static final String GROCERY_SECTION = "grocery_section";
            public static final String STORE_ID = "store_id";
        }
    }

    public static final class StoreGroceryAisleTable {
        public static final String NAME = "storeGroceryAisles";
        public static final class Cols {
            public static final String GROCERY_AISLE = "grocery_aisle";
            public static final String STORE_ID = "store_id";
        }
    }

    public static final class LocationTable {

        public static final String NAME = "grocery_item_locations";

        public static final class Cols {
            public static final String ID = "location_id";
            public static final String STORE_ID = "store_id";
            public static final String SECTION_NAME = "section_name";
            public static final String AISLE = "store_aisle";
        }
    }
}
