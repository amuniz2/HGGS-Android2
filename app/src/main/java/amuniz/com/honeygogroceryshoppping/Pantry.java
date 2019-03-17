package amuniz.com.honeygogroceryshoppping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import amuniz.com.honeygogroceryshoppping.database.PantryItemCursorWrapper;
import amuniz.com.honeygogroceryshoppping.database.PantryItemDbSchema.PantryItemTable;
import amuniz.com.honeygogroceryshoppping.database.PantryItemLocationCursorWrapper;
import amuniz.com.honeygogroceryshoppping.database.PantryItemLocationDbSchema.PantryItemLocationTable;
import amuniz.com.honeygogroceryshoppping.database.ShoppingItemCursorWrapper;
import amuniz.com.honeygogroceryshoppping.database.ShoppingItemDbSchema.ShoppingItemTable;
import amuniz.com.honeygogroceryshoppping.database.StoreAislesCursorWrapper;
import amuniz.com.honeygogroceryshoppping.database.StoreCursorWrapper;
import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema;
import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.LocationTable;
import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.StoreGroceryAisleTable;
import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.StoreGrocerySectionTable;
import amuniz.com.honeygogroceryshoppping.database.StoreDbSchema.StoreTable;
import amuniz.com.honeygogroceryshoppping.database.StoreGrocerySectionsCursorWrapper;
import amuniz.com.honeygogroceryshoppping.database.StoreLocationCursorWrapper;
import amuniz.com.honeygogroceryshoppping.model.PantryItemQuantity;
import amuniz.com.honeygogroceryshoppping.model.StoreLocation;
import amuniz.com.honeygogroceryshoppping.model.PantryItem;
import amuniz.com.honeygogroceryshoppping.model.PantryItemLocation;
import amuniz.com.honeygogroceryshoppping.model.ShoppingItem;
import amuniz.com.honeygogroceryshoppping.model.Store;

/**
 * Created by amuni on 4/2/2018.
 */

public class Pantry {
    private static Pantry sPantry;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private Pantry(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new PantryBaseHelper(mContext).getWritableDatabase();
        mDatabase.setForeignKeyConstraintsEnabled(true);
    }

    public List<PantryItem> getPantryItems() {
        List<PantryItem> pantryItems =  new ArrayList<PantryItem>();

        PantryItemCursorWrapper cursor = queryPantryItems(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                pantryItems.add(cursor.getPantryItem());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }

        return pantryItems;
    }

    public PantryItem getPantryItem(Long id) {
        PantryItemCursorWrapper cursor = queryPantryItems(
                PantryItemTable.Cols.ID + " = ?",
                new String[]{ id.toString() }
        );

        PantryItem ret = null;
        try {
            if (cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            ret = cursor.getPantryItem();
            ret.setLocations(getPantryItemStoreLocations(id));
        }
        catch(Exception ex) {
            Log.e("getPantryItem(long)", ex.getMessage());
            ret = new PantryItem(id, "Error loading", "n/a", "");
        }
        finally {
            cursor.close();
        }
        return ret;
    }

    public ShoppingItem getShoppingItem(Long pantryItemId) {
        ShoppingItemCursorWrapper cursor = queryShoppingItems(
                ShoppingItemTable.Cols.PANTRY_ITEM_ID + " = ?",
                new String[]{ pantryItemId.toString() }
        );

        ShoppingItem ret = null;
        try {
            if (cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            ret = cursor.getShoppingItem();
        }
        catch(Exception ex) {
            Log.e("getShoppingItem(long)", ex.getMessage());
            ret = new ShoppingItem(pantryItemId);
        }
        finally {
            cursor.close();
        }
        return ret;
    }

    public List<StoreLocation> getPantryItemStoreLocations(Long pantryItemId) {
        PantryItemLocationCursorWrapper cursor = null;
        List<StoreLocation> storeLocations = new ArrayList<>();
        try {
            cursor = queryPantryItemLocations(
                    PantryItemLocationTable.Cols.PANTRY_ITEM_ID + " = ?",
                    new String[]{pantryItemId.toString()});

            if (cursor.getCount() == 0)
                return storeLocations;

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                storeLocations.add(getStoreLocation(cursor.getPantryItemLocation().getLocationId()));
                cursor.moveToNext();
            }

        } catch(Exception ex) {
            Log.e("getPantryItemStoreLocations(long)", ex.getMessage());
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return storeLocations;
    }

    public Long findStoreLocation(Long storeId, String aisle, String sectionName) {
        return getStoreLocationId(storeId, aisle, sectionName);
    }
    public Store getStore(Long id) {
        StoreCursorWrapper cursor = queryStores(
                StoreTable.Cols.ID + " = ?",
                new String[]{ id.toString() }
        );

        Store ret = null;
        try {
            if (cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            ret = cursor.getStore();
        }
        catch(Exception ex)
        {
            Log.e("getStore", ex.getMessage());
        }
        finally {
            cursor.close();
        }
        return ret;
    }

    public List<Store> getStores() {
        List<Store> stores =  new ArrayList<Store>();

        StoreCursorWrapper cursor = queryStores(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                stores.add(cursor.getStore());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }

        return stores;
    }

    private Long getStoreLocationId(Long storeId, String aisle, String section) {
        List<StoreLocation> storeLocations =  new ArrayList<StoreLocation>();

        StoreLocationCursorWrapper cursor = queryStoreLocationId(LocationTable.Cols.STORE_ID + " = ?  AND "
                + LocationTable.Cols.AISLE + " = ? AND "
                + LocationTable.Cols.SECTION_NAME + " = ? ", new String[]{storeId.toString(), aisle, section});

        // PantryItemTable.Cols.UUID + " = ?",
        //                new String[]{ id.toString()
        try {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                return cursor.getLong(cursor.getColumnIndex(LocationTable.Cols.ID));
            }
        }
        finally {
            cursor.close();
        }

        List<StoreLocation> locationsForComparison = getStoreLocations(storeId);
        return Long.valueOf(0);
    }

    public List<StoreLocation> getStoreLocations(Long storeId) {
        List<StoreLocation> storeLocations =  new ArrayList<StoreLocation>();

        StoreLocationCursorWrapper cursor = queryStoreItemLocations(
                LocationTable.Cols.STORE_ID + " = ?",
                new String[]{storeId.toString()});

        // PantryItemTable.Cols.UUID + " = ?",
        //                new String[]{ id.toString()
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                storeLocations.add(cursor.getStoreLocation());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }

        return storeLocations;
    }

    public StoreLocation getStoreLocation(Long locationId) {

           StoreLocationCursorWrapper cursor = queryStoreItemLocations(
                    LocationTable.Cols.ID + " = ?",
                    new String[]{ locationId.toString() }
            );

            StoreLocation ret = null;
            try {
                if (cursor.getCount() == 0)
                    return null;

                cursor.moveToFirst();
                ret = cursor.getStoreLocation();
                ret.setStore(sPantry.getStore(ret.getStoreId()));
            }
            catch(Exception ex)
            {
                Log.e("getStoreLocation", ex.getMessage());
            }
            finally {
                cursor.close();
            }
            return ret;
    }


    public List<String> getStoreAisles(Long storeId) {

        List<String> ret = new ArrayList<>();
        if (storeId == null)
            return ret;

        StoreAislesCursorWrapper cursor = queryStoreAisles(
                StoreGroceryAisleTable.Cols.STORE_ID + " = ? ",
                new String[]{ storeId.toString() });

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ret.add(cursor.getAisle());
                cursor.moveToNext();
            }

        }
        catch(Exception ex)
        {
            Log.e("getItemLocation", ex.getMessage());
        }
        finally {
            cursor.close();
        }
        return ret;
    }

    public List<String> getStoreGrocerySections(Long storeId) {

        List<String> ret = new ArrayList<>();
        if (storeId == null)
            return ret;

        StoreGrocerySectionsCursorWrapper cursor = queryStoreGrocerySections(
                StoreGrocerySectionTable.Cols.STORE_ID + " = ? ",
                new String[]{ storeId.toString() });

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ret.add(cursor.getGrocerySection());
                cursor.moveToNext();
            }

        }
        catch(Exception ex)
        {
            Log.e("getStoreGrocerySections", ex.getMessage());
        }
        finally {
            cursor.close();
        }
        return ret;
    }

    public List<ShoppingItem> getShoppingList(Long storeId) {
        List<ShoppingItem> ret = new ArrayList<>();
        ShoppingItemCursorWrapper cursor = queryShoppingItems(storeId);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ret.add(cursor.getShoppingItem());
                cursor.moveToNext();
            }
        }
        catch(Exception ex) {
            Log.e("getShoppingItem(long)", ex.getMessage());
        }
        finally {
            cursor.close();
        }
        return ret;
    }
    public File getPhotoFile(PantryItem pantrItem)
    {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, pantrItem.getPhotoFileName());
    }
    public static Pantry get(Context context){
        if (sPantry == null)
            sPantry = new Pantry(context);
        return sPantry;
    }

    public Long addPantryItem(String name, String description, boolean isSelectByDefault, double defaultQuantity, String unit)
    {
        Long pantryItemId = null;
        try {
            pantryItemId = mDatabase.insertOrThrow(PantryItemTable.NAME, null,
                    getPantryItemContentValues(name, description, isSelectByDefault, defaultQuantity, unit));
            addShoppingItem(pantryItemId, defaultQuantity, unit, isSelectByDefault);
        }

        catch (Exception ex){
            Log.e("Pantry.add", "Exception thrown in addPantryItem().");
        }
        return pantryItemId;
    }

    private Long addShoppingItem(long pantryItemId, double defaultQuantity, String unit, boolean includeInShoppingList)
    {
        Long shoppingItemId = null;
        try {
            shoppingItemId = mDatabase.insertOrThrow(ShoppingItemTable.NAME, null,
                    getShoppingItemContentValues(pantryItemId, defaultQuantity, unit, includeInShoppingList));
        }

        catch (Exception ex){
            Log.e("Pantry.add", "Exception thrown in addShoppingItem().");
        }
        return shoppingItemId;
    }

    public long addStore(String storeName)
    {
        try {
            return mDatabase.insertOrThrow(StoreTable.NAME, null, getStoreContentValues(storeName));
        }

        catch (Exception ex){
            Log.e("Pantry.addStore", "Exception thrown in addStore().");
            throw ex;
        }
    }

    public Long addStoreLocation(Long storeId, String aisle, String section)
    {
        try {
            if (aisle != null && aisle.trim().length() > 0 && !getStoreAisles(storeId).contains(aisle)) {
                addStoreAisle(storeId, aisle);
            }
            if (section != null && section.trim().length() > 0 && !getStoreGrocerySections(storeId).contains(section)) {
                addStoreSection(storeId, section);
            }
            return mDatabase.insertOrThrow(LocationTable.NAME, null, getStoreLocationContentValues(storeId, aisle, section));
        }

        catch (Exception ex){
            Log.e("addPantryItemLocation", "Exception thrown in addPantryItemLocation().");
        }
        return null;
    }


    public void removePantryItem(PantryItem pantryItem)
    {
        mDatabase.delete(PantryItemTable.NAME, PantryItemTable.Cols.ID + " = ?", new String []{Long.toString(pantryItem.getId())});
    }

    public void deleteStore(long storeId)
    {
        mDatabase.delete(StoreTable.NAME, StoreTable.Cols.ID + " = ?", new String []{Long.toString(storeId)});
    }

    public void deleteStoreAisle(long storeId, String aisle)
    {
        mDatabase.delete(StoreGroceryAisleTable.NAME, StoreGroceryAisleTable.Cols.STORE_ID + " = ? AND " + StoreGroceryAisleTable.Cols.GROCERY_AISLE + " = ? ",
                new String []{Long.toString(storeId), aisle});
    }

    public void deleteStoreGrocerySection(long storeId, String section)
    {
        mDatabase.delete(StoreGrocerySectionTable.NAME, StoreGrocerySectionTable.Cols.STORE_ID + " = ? AND " + StoreGrocerySectionTable.Cols.GROCERY_SECTION + " = ? ",
                new String []{Long.toString(storeId), section});
    }
    public void deleteStoreLocation(long id)
    {
        mDatabase.delete(LocationTable.NAME, LocationTable.Cols.ID + " = ?", new String []{Long.toString(id)});
    }
    public void deletePantryItem(long id)
    {
        mDatabase.delete(ShoppingItemTable.NAME, ShoppingItemTable.Cols.PANTRY_ITEM_ID + " = ?", new String [] {Long.toString(id)});
        mDatabase.delete(PantryItemTable.NAME, PantryItemTable.Cols.ID + " = ?", new String []{Long.toString(id)});
    }
    public void deletePantryItemLocation(long pantryItemId, long locationId)
    {
        mDatabase.delete(PantryItemLocationTable.NAME, PantryItemLocationTable.Cols.PANTRY_ITEM_ID + " = ? AND " + PantryItemLocationTable.Cols.LOCATION_ID + " = ? ",
                new String []{Long.toString(pantryItemId), Long.toString(locationId)});
    }
    private ContentValues getPantryItemContentValues(PantryItem item) {
        return getPantryItemContentValues(item.getId(), item.getName(), item.getDescription(), item.isSelectByDefault(),
                item.getDefaultQuantity().getQuantity(), item.getDefaultQuantity().getUnit());

    }
    private ContentValues getPantryItemContentValues(Long id, String name, String description, boolean isSelectByDefault, double defaultQuantity, String unit) {
        ContentValues values = new ContentValues();

        if (id != null) {
            values.put(PantryItemTable.Cols.ID, id);
        }
        values.put(PantryItemTable.Cols.NAME, name);
        values.put(PantryItemTable.Cols.DESCRIPTION, description);
        values.put(PantryItemTable.Cols.SELECT_BY_DEFAULT, isSelectByDefault);
        values.put(PantryItemTable.Cols.DEFAULT_QUANTITY, defaultQuantity);
        values.put(PantryItemTable.Cols.UNITS, unit);

        return values;
    }

    private ContentValues getPantryItemContentValues(String name, String description, boolean isSelectByDefault, double defaultQuantity, String unit) {
        return getPantryItemContentValues(null, name, description, isSelectByDefault, defaultQuantity, unit);
    }

    private ContentValues getShoppingItemContentValues(long pantryItemId, double quantity, String unit, boolean includeInShoppingList) {
        ContentValues values = new ContentValues();

        values.put(ShoppingItemTable.Cols.PANTRY_ITEM_ID, pantryItemId);
        values.put(ShoppingItemTable.Cols.SELECTED, includeInShoppingList);
        values.put(ShoppingItemTable.Cols.IN_CART, false);
        values.put(ShoppingItemTable.Cols.QUANTITY, quantity);
        values.put(ShoppingItemTable.Cols.UNITS, unit);
        return values;
    }

    private ContentValues getPantryItemLocationContentValues(long pantryItemId, long locationId) {
        ContentValues values = new ContentValues();

        values.put(PantryItemLocationTable.Cols.PANTRY_ITEM_ID, pantryItemId);
        values.put(PantryItemLocationTable.Cols.LOCATION_ID, locationId);

        return values;
    }

    public Long addPantryItemLocation(Long pantryItemId, Long storeLocationId)
    {
        Long ret = null;
        try {
            ret = mDatabase.insertOrThrow(PantryItemLocationTable.NAME, null, getPantryItemLocationContentValues(pantryItemId, storeLocationId));
        }

        catch (Exception ex){
            Log.e("Pantry.addPantryItemLocation", "Exception thrown in addPantryItemLocation().");
        }
        return ret;
    }

    private ContentValues getPantryItemLocationContentValues(PantryItemLocation itemLocation) {
        ContentValues values = new ContentValues();

        values.put(PantryItemLocationTable.Cols.LOCATION_ID, itemLocation.getLocationId());
        values.put(PantryItemLocationTable.Cols.PANTRY_ITEM_ID, itemLocation.getPantryItemId());

        return values;
    }

    public void updatePantryItem(PantryItem pantryItem) {
        String idString = Long.toString(pantryItem.getId());
        ContentValues pantryItemContentValues = getPantryItemContentValues(pantryItem);

        for (StoreLocation location : pantryItem.getLocations()) {
            ContentValues pantryItemLocationValues = getPantryItemLocationContentValues(pantryItem.getId(), location.getId());
            mDatabase.update(PantryItemLocationTable.NAME, pantryItemLocationValues,
                    PantryItemLocationTable.Cols.PANTRY_ITEM_ID + " = ? AND "+ PantryItemLocationTable.Cols.LOCATION_ID + " = ?",
                    new String[]{idString, Long.toString(location.getId())});

        }
        mDatabase.update(PantryItemTable.NAME, pantryItemContentValues, PantryItemTable.Cols.ID + " = ?", new String[]{idString});
    }
    public void updatePantryItemLocation(long pantryItemId, long oldLocationId, Long newLocationId) {
        deletePantryItemLocation(pantryItemId, oldLocationId);
        addPantryItemLocation(pantryItemId, newLocationId);
    }

    public void updateShoppingItem(ShoppingItem shoppingItem) {
        String idString = Long.toString(shoppingItem.getPantryItemId());
        ContentValues shoppingItemContentValues = getShoppingItemContentValues(shoppingItem.getPantryItemId(),
        shoppingItem.getQuantity().getQuantity(), shoppingItem.getQuantity().getUnit(), shoppingItem.getSelected());

        mDatabase.update(ShoppingItemTable.NAME, shoppingItemContentValues, ShoppingItemTable.Cols.PANTRY_ITEM_ID + " = ?", new String[]{idString});

    }

    public void updateStore(Store store) {
        String idString = Long.toString(store.getId());
        ContentValues values = getStoreContentValues(store.getName());

        mDatabase.update(StoreTable.NAME, values, StoreTable.Cols.ID + " = ?", new String[]{idString});
    }

    public void updateStoreAisle(long storeId, String oldAisle, String newAisle) {
        // todo: replace all usages of oldSection from existing locations
        deleteStoreAisle(storeId, oldAisle);
        addStoreAisle(storeId, newAisle);

    }

    public void updateStoreSection(long storeId, String oldSection, String newSection) {
        // todo: replace all usages of oldSection from existing locations
        deleteStoreGrocerySection(storeId, oldSection);
        addStoreSection(storeId, newSection);

    }
    public void updateStoreLocation(Long storeId, Long locationId, String aisle, String section) {

        ContentValues values = getStoreLocationContentValues(storeId, locationId, aisle, section);
        mDatabase.update(LocationTable.NAME, values, LocationTable.Cols.ID + " = ?", new String[]{locationId.toString()});
    }

    private PantryItemCursorWrapper queryPantryItems(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                PantryItemTable.NAME,
                null, // selects all columns
                whereClause,
                whereArgs,
                null, // group by
                null, // having
                null // order by
        );
        return new PantryItemCursorWrapper(cursor);
    }

    private ShoppingItemCursorWrapper queryShoppingItems(Long storeId) {
        final String shoppingTableName = ShoppingItemTable.NAME; // shoppingItems
        final String pantryItemTableName = PantryItemTable.NAME; // pantryItems
        final String itemLocationTableName = PantryItemLocationTable.NAME; // pantryitemlocationtable
        final String locationTableName = LocationTable.NAME; // grocery_item_locations

        String selectClause = "SELECT * ";

        String fromClause = " FROM " + shoppingTableName +
                " INNER JOIN " + pantryItemTableName + " ON " + shoppingTableName + "." + ShoppingItemTable.Cols.PANTRY_ITEM_ID + " = " + pantryItemTableName + "." + PantryItemTable.Cols.ID +
                " INNER JOIN " + itemLocationTableName + " ON " + pantryItemTableName + "." + PantryItemTable.Cols.ID + " = " + itemLocationTableName + "." + PantryItemLocationTable.Cols.PANTRY_ITEM_ID +
                " INNER JOIN " + locationTableName + " ON " + itemLocationTableName + "." + PantryItemLocationTable.Cols.LOCATION_ID + " = " + locationTableName + "." + LocationTable.Cols.ID;

        String whereClause = " WHERE " + locationTableName + "." + LocationTable.Cols.STORE_ID + " = " + storeId +
                " AND " + shoppingTableName + "." + ShoppingItemTable.Cols.SELECTED + " = 1";
//        String whereClause = "";
        Cursor cursor = mDatabase.rawQuery(selectClause + fromClause + whereClause, null);

//        Cursor cursor = mDatabase.query(
//                ShoppingItemTable.NAME,
//                null, // selects all columns
//                whereClause,
//                new String[]{ storeId.toString() },
//                null, // group by
//                null, // having
//                null // order by
//        );
        return new ShoppingItemCursorWrapper(cursor);
    }
    private ShoppingItemCursorWrapper queryShoppingItems(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ShoppingItemTable.NAME,
                null, // selects all columns
                whereClause,
                whereArgs,
                null, // group by
                null, // having
                null // order by
        );
        return new ShoppingItemCursorWrapper(cursor);
    }

    private StoreCursorWrapper queryStores(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                StoreTable.NAME,
                null, // selects all columns
                whereClause,
                whereArgs,
                null, // group by
                null, // having
                null // order by
        );
        return new StoreCursorWrapper(cursor);
    }

    private StoreAislesCursorWrapper queryStoreAisles(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                StoreGroceryAisleTable.NAME,
                null, // selects all columns
                whereClause,
                whereArgs,
                null, // group by
                null, // having
                null // order by
        );
        return new StoreAislesCursorWrapper(cursor);
    }

    private StoreGrocerySectionsCursorWrapper queryStoreGrocerySections(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                StoreGrocerySectionTable.NAME,
                null, // selects all columns
                whereClause,
                whereArgs,
                null, // group by
                null, // having
                null // order by
        );
        return new StoreGrocerySectionsCursorWrapper(cursor);
    }
    private PantryItemLocationCursorWrapper queryPantryItemLocations(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                PantryItemLocationTable.NAME,
                null, // selects all columns
                whereClause,
                whereArgs,
                null, // group by
                null, // having
                null // order by
        );
        return new PantryItemLocationCursorWrapper(cursor);
    }

    private StoreLocationCursorWrapper queryStoreItemLocations(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                LocationTable.NAME,
                null, // selects all columns
                whereClause,
                whereArgs,
                null, // group by
                null, // having
                null // order by
        );
        return new StoreLocationCursorWrapper(cursor);
    }

    private StoreLocationCursorWrapper queryStoreLocationId(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                LocationTable.NAME,
                null, // selects all columns
                whereClause,
                whereArgs,
                null, // group by
                null, // having
                null // order by
        );
        return new StoreLocationCursorWrapper(cursor);
    }

    /*
//
//  selec s.sotre_name, st.SectionName, st/Aisle from pantryItemsectionTable st
//  join sectiontable  on pantry_item_id  = id
//    join storetable on store_id = sectiontable.store_id
//  where pantry_item_id = id
//
db.rawQuery(MY_QUERY, new String[]{String.valueOf(propertyId)});
 */
//    private PantryItemLocationCursorWrapper queryPantryItemLocations(Long pantryItemId) throws Exception {
//        try {
//            String selectClause = String.format("SELECT %s.%s As StoreId, %s.%s as Aisle, %s.%s As Section",
//                    StoreTable.NAME, StoreTable.Cols.ID,
//                    LocationTable.NAME, LocationTable.Cols.AISLE,
//                    LocationTable.NAME, LocationTable.Cols.SECTION_NAME);
//
//            String fromClause = String.format(" FROM %s JOIN %s ON %s.%s = %s.%s JOIN %s ON %s.%s = %s.%s",
//                    PantryItemLocationTable.NAME,
//                    LocationTable.NAME,
//                    PantryItemLocationTable.NAME, PantryItemLocationTable.Cols.LOCATION_ID, LocationTable.NAME, LocationTable.Cols.ID,
//                    StoreTable.NAME,
//                    LocationTable.NAME, LocationTable.Cols.STORE_ID, StoreTable.NAME, StoreTable.Cols.ID);
//
//            String whereClause = String.format(" WHERE %s.%s = ? ", PantryItemLocationTable.NAME, PantryItemLocationTable.Cols.PANTRY_ITEM_ID);
//
//            Cursor cursor = mDatabase.rawQuery(selectClause + fromClause + whereClause, new String[]{String.valueOf(pantryItemId)});
//
//            return new PantryItemLocationCursorWrapper(cursor);
//        }
//        catch (Exception ex){
//            Log.e("getPILocations", ex.getMessage());
//            throw ex;
//        }
//    }

    private ContentValues getStoreLocationContentValues(long storeId, String aisle, String section) {
        ContentValues values = new ContentValues();

        values.put(LocationTable.Cols.STORE_ID, storeId);
        values.put(LocationTable.Cols.AISLE, aisle);
        values.put(LocationTable.Cols.SECTION_NAME, section);

        return values;
    }

    private ContentValues getStoreLocationContentValues(StoreLocation location) {
        return getStoreLocationContentValues(location.getId(), location.getStoreId(), location.getAisle(), location.getSection());
    }

    private ContentValues getStoreLocationContentValues(Long locationId, Long storeId, String aisle, String section) {
        ContentValues values = getStoreLocationContentValues(storeId, aisle, section);

        values.put(LocationTable.Cols.ID, locationId);

        return values;
    }

    private ContentValues getStoreContentValues(String storeName) {
        ContentValues values = new ContentValues();

        values.put(StoreTable.Cols.STORE_NAME, storeName);

        return values;
    }

    private ContentValues getStoreAisleContentValues(Long storeId, String aisle) {
        ContentValues values = new ContentValues();

        values.put(StoreGroceryAisleTable.Cols.STORE_ID, storeId.toString());
        values.put(StoreGroceryAisleTable.Cols.GROCERY_AISLE, aisle);

        return values;
    }
    public Long addStoreAisle(Long storeId, String aisle) {

        return mDatabase.insertOrThrow(StoreGroceryAisleTable.NAME, null, getStoreAisleContentValues(storeId, aisle));
    }

    private ContentValues getStoreSectionContentValues(Long storeId, String section) {
        ContentValues values = new ContentValues();

        values.put(StoreGrocerySectionTable.Cols.STORE_ID, storeId.toString());
        values.put(StoreGrocerySectionTable.Cols.GROCERY_SECTION, section);

        return values;

    }
    public long addStoreSection(Long storeId, String section) {

        return mDatabase.insertOrThrow(StoreGrocerySectionTable.NAME, null, getStoreSectionContentValues(storeId, section));
    }


    public boolean storeInUse(Long storeId) {
        List<StoreLocation> existingStoreLocations = getStoreLocations(storeId);
        return existingStoreLocations.size() > 0;
    }
    public boolean storeAisleInUse(Long storeId, String aisle) {
        List<StoreLocation> existingStoreLocations = getStoreLocations(storeId);
        long numberOfLocationsUsingAisle = existingStoreLocations.stream()
                .filter(location -> aisle.equals(location.getSection()))
                .count();

        return numberOfLocationsUsingAisle > 0;
    }
    public boolean storeSectionInUse(Long storeId, String section) {
        List<StoreLocation> existingStoreLocations = getStoreLocations(storeId);
        long numberOfLocationsUsingSection = existingStoreLocations.stream()
                .filter(location -> section.equals(location.getSection()))
                .count();

        return numberOfLocationsUsingSection > 0;
    }

    public void addPantryItemToShoppingList(long pantryItemId) {
        ShoppingItem shoppingItem = getShoppingItem(pantryItemId);
        if (shoppingItem != null ) {
            shoppingItem.setSelected(true);
            updateShoppingItem(shoppingItem);
        }
    }
    public void removePantryItemFromShoppingList(long pantryItemId) {
        ShoppingItem shoppingItem = getShoppingItem(pantryItemId);
        shoppingItem.setSelected(false);
        updateShoppingItem(shoppingItem);
    }
}
