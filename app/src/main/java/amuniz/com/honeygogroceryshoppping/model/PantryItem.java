package amuniz.com.honeygogroceryshoppping.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Created by amuni on 4/1/2018.
 */

public class PantryItem {
    private long mId;
    private String mName;
    private String mDescription;
    private List<StoreLocation> mLocations;
    private boolean mSelectByDefault;
    private PantryItemQuantity mDefaultQuantity;
    private ShoppingItem mShoppingItem;

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<StoreLocation> getLocations() {
        return mLocations;
    }

    public void setLocations(List<StoreLocation> locations) {
        mLocations = locations;
    }
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getPhotoFileName()
    {
        return "IMG_" + getId() + ".jpg";
    }

    public PantryItem(long id, String name, String description, String units, double defaultQuantity, boolean selectByDefault) {
        mId = id;
        mLocations = new ArrayList<>();
        mDefaultQuantity = new PantryItemQuantity(defaultQuantity, units);
        mName = name;
        mSelectByDefault = selectByDefault;
        mDescription = description;
    }

    public PantryItem(long id, String name, String description, String units)  {
        this(id, name, description, units, 1, false);
    }

    public PantryItem(long id, String name, String description, String units, int defaultQuantity)  {
        this(id, name, description, units, defaultQuantity, false);
    }

    public void setSelectByDefault(boolean selectByDefault) {
        mSelectByDefault = selectByDefault;
    }
    public boolean isSelectByDefault() {
        return mSelectByDefault;
    }

    public String getTextDefinition()
    {
        String def = "Name: " + getName() + "\r\n" +
                "Description: " + getDescription() + "\r\n";

        return def;
    }

    public PantryItemQuantity getDefaultQuantity() { return mDefaultQuantity; }

    public ShoppingItem getShoppingItem() {
        return mShoppingItem;
    }
}

