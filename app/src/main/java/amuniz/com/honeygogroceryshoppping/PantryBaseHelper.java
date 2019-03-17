package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Properties;

import amuniz.com.honeygogroceryshoppping.database.PantryItemDbSchema.PantryItemTable;
import amuniz.com.honeygogroceryshoppping.database.PantryItemLocationDbSchema.PantryItemLocationTable;
import amuniz.com.honeygogroceryshoppping.database.ShoppingItemDbSchema;
import amuniz.com.honeygogroceryshoppping.database.ShoppingItemDbSchema.ShoppingItemTable;
import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema;
import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.LocationTable;
import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.StoreGroceryAisleTable;
import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.StoreGrocerySectionTable;
import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.StoreTable;

public class PantryBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "pantryBase.db";
    private static final String FOREIGN_KEY_FORMAT = "FOREIGN KEY(%s) REFERENCES %s(%s)";
    private static final String AUTO_INCREMENT_PRIMARYKEY_DEFINITION = " _id integer primary key autoincrement, ";
    private static final String CREATE_INDEX_COMMAND_FORMAT = "CREATE INDEX %s ON %s (%s);";
    private static final String UNIQUE_CONSTRAINT_FORMAT = "CONSTRAINT %s UNIQUE (%s)";

    public PantryBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sectionColumnDefinitions =
                        StoreGrocerySectionTable.Cols.GROCERY_SECTION + " TEXT NOT NULL, " +
                        StoreGrocerySectionTable.Cols.STORE_ID + " INT, " +
                        BuildForeignKeyConstraintDefinition(StoreGrocerySectionTable.Cols.STORE_ID, StoreTable.NAME, StoreTable.Cols.ID);

        String aisleColumnDefinitions =
                        StoreGroceryAisleTable.Cols.GROCERY_AISLE + " TEXT NOT NULL, " +
                        StoreGroceryAisleTable.Cols.STORE_ID + " INTEGER, " +
                        BuildForeignKeyConstraintDefinition(StoreGroceryAisleTable.Cols.STORE_ID, StoreTable.NAME, StoreTable.Cols.ID);

        String locationColumnDefinitions =
                LocationTable.Cols.ID + " INTEGER PRIMARY KEY ASC, " +
                LocationTable.Cols.AISLE + ", " +
                LocationTable.Cols.SECTION_NAME + ", " +
                LocationTable.Cols.STORE_ID + " INTEGER, " +
                BuildForeignKeyConstraintDefinition(LocationTable.Cols.STORE_ID, StoreTable.NAME, StoreTable.Cols.ID);

        String pantryItemLocationColumnDefinitions =
            PantryItemLocationTable.Cols.LOCATION_ID + " INTEGER, " +
            PantryItemLocationTable.Cols.PANTRY_ITEM_ID + " INTEGER, " +
            BuildForeignKeyConstraintDefinition(PantryItemLocationTable.Cols.PANTRY_ITEM_ID, PantryItemTable.NAME,PantryItemTable.Cols.ID ) + ", " +
            BuildForeignKeyConstraintDefinition(PantryItemLocationTable.Cols.LOCATION_ID, LocationTable.NAME, LocationTable.Cols.ID);

        String shoppingItemColumnDefinitions =
                PantryItemTable.Cols.ID + " INTEGER PRIMARY KEY ASC, " +
                ShoppingItemTable.Cols.IN_CART + " TINYINT, " +
                ShoppingItemTable.Cols.SELECTED + " TINYINT, " +
                ShoppingItemTable.Cols.QUANTITY + " REAL, " +
                ShoppingItemTable.Cols.UNITS + " TEXT, " +
                BuildForeignKeyConstraintDefinition(ShoppingItemTable.Cols.PANTRY_ITEM_ID, PantryItemTable.NAME, PantryItemTable.Cols.ID ) + ", " +
                BuildUniqueConstraint(ShoppingItemTable.NAME, ShoppingItemTable.Cols.PANTRY_ITEM_ID);

        String pantryItemColumnDefinitions =
                PantryItemTable.Cols.ID + " INTEGER PRIMARY KEY ASC, " +
                        PantryItemTable.Cols.NAME + " TEXT NOT NULL, " +
                        PantryItemTable.Cols.DESCRIPTION + " TEXT, " +
                        PantryItemTable.Cols.SELECT_BY_DEFAULT + " TINYINT, " +
                        PantryItemTable.Cols.DEFAULT_QUANTITY + " REAL, " +
                        PantryItemTable.Cols.UNITS + " TEXT, " +
                        BuildUniqueConstraint(PantryItemTable.NAME, PantryItemTable.Cols.NAME);

        String storeColumnDefinitions =
                        StoreTable.Cols.ID + " INTEGER PRIMARY KEY ASC, " +
                        StoreTable.Cols.STORE_NAME + " TEXT NOT NULL, " +
                BuildUniqueConstraint(StoreTable.NAME, StoreTable.Cols.STORE_NAME);

        db.execSQL(String.format("create table %s (%s)", StoreTable.NAME, storeColumnDefinitions) );
        db.execSQL(String.format("create table %s (%s)", StoreGrocerySectionTable.NAME, sectionColumnDefinitions) );
        db.execSQL(String.format("create table %s (%s)", StoreGroceryAisleTable.NAME, aisleColumnDefinitions) );
        db.execSQL(String.format("create table %s (%s)", LocationTable.NAME, locationColumnDefinitions) );
        db.execSQL(String.format("create table %s (%s)", PantryItemTable.NAME, pantryItemColumnDefinitions) );
        db.execSQL(String.format("create table %s (%s)", PantryItemLocationTable.NAME, pantryItemLocationColumnDefinitions) );
        db.execSQL(String.format("create table %s (%s)", ShoppingItemTable.NAME, shoppingItemColumnDefinitions) );
    }

    private String BuildUniqueConstraint(String constraintName, String columnName) {
        return String.format(UNIQUE_CONSTRAINT_FORMAT, constraintName, columnName);
    }

    private String BuildForeignKeyConstraintDefinition(String columnName, String referenceTableName, String foreignKeyColumnName)
    {
        return String.format(FOREIGN_KEY_FORMAT, columnName, referenceTableName, foreignKeyColumnName);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + PantryItemLocationTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StoreDbSchema.LocationTable.NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + LocationTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StoreTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PantryItemTable.NAME);

        // create new tables
        onCreate(db);
    }
}
