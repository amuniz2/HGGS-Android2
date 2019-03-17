package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.security.cert.PKIXRevocationChecker;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import amuniz.com.honeygogroceryshoppping.model.PantryItem;
import amuniz.com.honeygogroceryshoppping.model.ShoppingItem;
import amuniz.com.honeygogroceryshoppping.model.Store;
import amuniz.com.honeygogroceryshoppping.model.StoreLocation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PantryTests {

    static PantryBaseHelper  dbHelper;
    Context mContext;
    Pantry mPantry;
    private static String testStoreName = "testStore";
    private Store mTestStore;

    @BeforeClass
    public static void fixtureSetUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        dbHelper = new PantryBaseHelper(context);
        Pantry.get(context).addStore(testStoreName);
    }

    @Before
    public void setup() {
        mContext = InstrumentationRegistry.getTargetContext();
        mPantry = Pantry.get(mContext);
        mTestStore = getStore(testStoreName);
    }

    @AfterClass
    public static void fixtureTearDown() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        assertTrue(context.deleteDatabase(PantryBaseHelper.DATABASE_NAME));
    }
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void ShouldAddAndDeleteStore() {
        String tempStore = "temp store";
        long storeId = mPantry.addStore(tempStore);

        assertTrue(storeId >= 0);
        Assert.assertEquals(tempStore, mPantry.getStore(storeId).getName());

        mPantry.deleteStore(storeId);
        Assert.assertEquals(null, mPantry.getStore(storeId));
    }

    @Test
    public void ShouldAddAndDeleteStoreAisle() {
        String aisle = "1";

        long aisleId = mPantry.addStoreAisle(mTestStore.getId(), aisle);
        assertTrue(aisleId >= 0);
        List<String> aisles = mPantry.getStoreAisles(mTestStore.getId());
        assertTrue(aisles.contains(aisle));

        mPantry.deleteStoreAisle(mTestStore.getId(), aisle);
        assertFalse(mPantry.getStoreAisles(mTestStore.getId()).contains(aisle));

    }

    @Test
    public void ShouldAddAndDeleteStoreGrocerySection() {
        String section = "Dairy";

        long sectionId = mPantry.addStoreSection(mTestStore.getId(), section);
        assertTrue(sectionId >= 0);
        List<String> sections = mPantry.getStoreGrocerySections(mTestStore.getId());
        assertTrue(sections.contains(section));

        mPantry.deleteStoreGrocerySection(mTestStore.getId(), section);
        assertFalse(mPantry.getStoreGrocerySections(mTestStore.getId()).contains(section));

    }

    @Test
    public void ShouldAddAndDeletePantryItem() {
        Long pantryItemId = mPantry.addPantryItem("Lowfat Milk", "McArthur", true, 1.0, "gallon");
        assertTrue(pantryItemId >= 0);

        PantryItem milk = mPantry.getPantryItem(pantryItemId);
        Assert.assertEquals("Lowfat Milk", milk.getName());
        Assert.assertEquals("McArthur", milk.getDescription());
        assertTrue(milk.isSelectByDefault());
        Assert.assertEquals(1.0, milk.getDefaultQuantity().getQuantity());
        Assert.assertEquals("gallon", milk.getDefaultQuantity().getUnit());
        Assert.assertEquals("Name: Lowfat Milk\r\nDescription: McArthur\r\n", milk.getTextDefinition());

        mPantry.deletePantryItem(pantryItemId);
        Assert.assertEquals(null, mPantry.getPantryItem(pantryItemId));
    }

    @Test
    public void ShouldAddGetAndDeleteStoreLocation() {
        Long locationId = mPantry.addStoreLocation(mTestStore.getId(), "1", "Dairy");
        Long locationId2 = mPantry.addStoreLocation(mTestStore.getId(), "1", "Packaged Deli");
        assertTrue(locationId >= 0);

        StoreLocation location = mPantry.getStoreLocation(locationId);
        Assert.assertEquals(mTestStore.getId(), location.getStoreId());
        Assert.assertEquals("1", location.getAisle());
        Assert.assertEquals("Dairy", location.getSection());

         location = mPantry.getStoreLocation(locationId2);
        Assert.assertEquals(mTestStore.getId(), location.getStoreId());
        Assert.assertEquals("1", location.getAisle());
        Assert.assertEquals("Packaged Deli", location.getSection());

        List storeLocations = mPantry.getStoreLocations(mTestStore.getId());
        Assert.assertEquals(2, storeLocations.size());

        List<String> sections = mPantry.getStoreGrocerySections(mTestStore.getId());
        assertTrue(sections.contains("Dairy"));
        assertTrue(sections.contains("Packaged Deli"));

        List<String> aisles = mPantry.getStoreAisles(mTestStore.getId());
        assertTrue(aisles.contains("1"));

        mPantry.deleteStoreGrocerySection(mTestStore.getId(), "Packaged Deli");
        mPantry.deleteStoreGrocerySection(mTestStore.getId(), "Dairy");
        mPantry.deleteStoreAisle(mTestStore.getId(), "1");
        mPantry.deleteStoreLocation(locationId);
        Assert.assertEquals(null, mPantry.getStoreLocation(locationId));
    }

    @Test
    public void ShouldAddAndDeletePantryItemLocation() throws Exception {
        Long locationId = mPantry.addStoreLocation(mTestStore.getId(), "1", "Dairy");
        Long pantryItemId = mPantry.addPantryItem("lowfat milk",  "McArthur", true, 1/4, "gallon");

        Long pantryItemLocationId = mPantry.addPantryItemLocation(locationId, pantryItemId);

        assertTrue(pantryItemLocationId >= 0);
        List<StoreLocation> locations = mPantry.getPantryItemStoreLocations(pantryItemId);
        Assert.assertEquals(1, locations.size());
        Object[] arrLocations = locations.toArray();

        Assert.assertEquals("1",((StoreLocation)arrLocations[0]).getAisle());
        Assert.assertEquals("Dairy",((StoreLocation)arrLocations[0]).getSection());
        Assert.assertEquals(mTestStore.getId(),((StoreLocation)arrLocations[0]).getStoreId());


        mPantry.deletePantryItemLocation(pantryItemId, locationId);
        locations = mPantry.getPantryItemStoreLocations(pantryItemId);
        Assert.assertEquals(0, locations.size());

        mPantry.deletePantryItem(pantryItemId);
        mPantry.deleteStoreLocation(locationId);
        mPantry.deleteStoreGrocerySection(mTestStore.getId(), "Dairy");
        mPantry.deleteStoreAisle(mTestStore.getId(), "1");

        Assert.assertEquals(0, mPantry.getPantryItemStoreLocations(locationId).size());
    }

    @Test
    public void ShouldGetShoppingList() throws Exception {
        Long locationId = mPantry.addStoreLocation(mTestStore.getId(), "1", "Dairy");
        Long milkPantryItemId = mPantry.addPantryItem("lowfat milk",  "McArthur", true, 1/4, "gallon");
        Long yogurtPantryItemId = mPantry.addPantryItem("yogurt",  "Dannon", false, 1, "pint");

        Long milkLocationId = mPantry.addPantryItemLocation(milkPantryItemId, locationId);
        Long yogurtLocationId = mPantry.addPantryItemLocation(yogurtPantryItemId, locationId);

        assertTrue(milkLocationId >= 0);
        assertTrue(yogurtLocationId >= 0);

        List<ShoppingItem> shoppingList = mPantry.getShoppingList(mTestStore.getId());

        assertEquals(1, shoppingList.size());
        Assert.assertEquals((long)milkPantryItemId, shoppingList.get(0).getPantryItemId());

        mPantry.deletePantryItemLocation(milkPantryItemId, locationId);
        mPantry.deletePantryItemLocation(yogurtPantryItemId, locationId);

        assertEquals(0, mPantry.getPantryItemStoreLocations(milkPantryItemId).size());
        assertEquals(0, mPantry.getPantryItemStoreLocations(yogurtPantryItemId).size());

        mPantry.deleteStoreLocation(locationId);
        assertEquals(0, mPantry.getStoreLocations(mTestStore.getId()).size());

        mPantry.deleteStoreGrocerySection(mTestStore.getId(), "Dairy");
        assertEquals(0, mPantry.getStoreGrocerySections(mTestStore.getId()).size());
        mPantry.deleteStoreAisle(mTestStore.getId(), "1");
        assertEquals(0, mPantry.getStoreAisles(mTestStore.getId()).size());

        mPantry.deletePantryItem(milkPantryItemId);
        mPantry.deletePantryItem(yogurtPantryItemId);
        assertEquals(0, mPantry.getPantryItems().size());
}

    private Store addStore(String storeName)
    {
        long id = mPantry.addStore(storeName);
        return mPantry.getStore(id);
    }
    private void deleteStore(long storeId)
    {
        mPantry.deleteStore(storeId);
    }
    private Store getStore(String storeName) {
        Optional<Store> store = mPantry.getStores().stream().filter(x -> x.getName().equals(storeName)).findFirst();
        if (store.isPresent())
            return store.get();
        else
            return null;
    }
    private void deleteStore(String storeName)
    {
        Optional<Store> store = mPantry.getStores().stream().filter(x -> x.getName().equals(storeName)).findFirst();
        mPantry.deleteStore(store.get().getId());
    }

}