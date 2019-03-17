package amuniz.com.honeygogroceryshoppping.model;

import java.io.Serializable;
import java.util.UUID;

public class StoreLocation implements Serializable {

    private long mId;
    private long mStoreId;
    private String mSection;
    private String mAisle;
    private Store mStore;

    public StoreLocation(long locationId, long storeId, String aisle, String section) {
        mId = locationId;
        mStoreId = storeId;
        mAisle = aisle;
        mSection = section;
    }

    public String getSection() {
        return mSection;
    }

    public void setSection(String name) {
        mSection = name;
    }

    public long getId() {
        return mId;
    }

    public String getAisle() {
        return mAisle;
    }

    public void setAisle(String aisle) {
        mAisle = aisle;
    }

    public void setStore(Store store) {
        mStore = store;
        mStoreId = store.getId();
    }

    public Store getStore() {
        return mStore;
    }

    public long getStoreId() {
        return mStoreId;
    }

    @Override
    public String toString() {

        String locationDescription = "undefined";
        if (mStore == null)
            return locationDescription;

        if (mSection != null && !mSection.isEmpty()) {
            if(mAisle != null && !mAisle.isEmpty()) {
                locationDescription = String.format("%s, Aisle %s, %s section", mStore.getName(), getAisle(), getSection());
            }
            else {
                locationDescription = String.format("%s, %s section", mStore.getName(), getSection());
            }
        }
        else if(mAisle != null && !mAisle.isEmpty()) {
            locationDescription = String.format("%s, Aisle %s", mStore.getName(), getAisle());
        }
        else {
            locationDescription = String.format(mStore.getName());
        }
        return locationDescription;
    }
}
