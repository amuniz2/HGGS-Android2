package amuniz.com.honeygogroceryshoppping.model;

/**
 * Created by amuni on 4/1/2018.
 */

public class PantryItemLocation {
    private long mPantryItemId;
    private long mLocationId;

    public PantryItemLocation(long pantryItemId, long locationId) {
        mPantryItemId = pantryItemId;
        mLocationId = locationId;
    }
    public long getLocationId() {
        return mLocationId;
    }

    public long getPantryItemId() {
        return mPantryItemId;
    }
}
