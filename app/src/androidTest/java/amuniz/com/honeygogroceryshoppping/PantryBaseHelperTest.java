package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PantryBaseHelperTest {

    static PantryBaseHelper  dbHelper;
    Context mContext;

    @BeforeClass
    public static void fixtureSetUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        dbHelper = new PantryBaseHelper(context);
    }

    @Before
    public void setup() {
        mContext = InstrumentationRegistry.getTargetContext();
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
    public void onCreate() {
        assertTrue(dbHelper.getWritableDatabase().isOpen());
    }
}