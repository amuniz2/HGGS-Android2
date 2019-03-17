package amuniz.com.honeygogroceryshoppping.model;

import java.io.Serializable;
import java.util.UUID;

public class Store implements Serializable {

    private long mId;
    private String mName;

    public Store(long id, String name) {
        mId = id;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public boolean equals(Object object) {
        Store store = (Store)object;
        return store.getId() == this.getId() &&
                store.getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        Long id = getId();
        return id.hashCode();
    }
}

