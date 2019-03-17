package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.HashSet;
import java.util.UUID;

import amuniz.com.honeygogroceryshoppping.model.Store;

public class EditStoreActivity  extends SingleFragmentActivity implements EditStoreFragment.EditStoreCallbacks {

    private static final String EXTRA_STORE_ID = "com.amuniz.editstoreintent.store_id";
    private static final String EXTRA_STORES = "com.amuniz.addstoreintent.store_list";
    private long mId;
    private Fragment mFragment = null;
    private HashSet<String> mStoreNames;

    public static Intent newIntent(Context packageContext, long storeId, HashSet<String> existingStoreNames)
    {
        Intent intent = new Intent(packageContext, EditStoreActivity.class);
        intent.putExtra(EXTRA_STORE_ID, storeId);
        intent.putExtra(EXTRA_STORES, existingStoreNames);

        return intent;
    }

    @Override
    protected Fragment createFragment()  {
        mId = getIntent().getLongExtra(EXTRA_STORE_ID, -1);
        mStoreNames = (HashSet<String>) getIntent().getSerializableExtra(EXTRA_STORES);

        mFragment = EditStoreFragment.newInstance(mId, mStoreNames);
        return mFragment;
    }

    @Override
    public void onStoreUpdated(Store store) {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();

        mFragment = null;
        this.finish();
    }

    @Override
    public void onStoreDeleted(Store store) {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();

        mFragment = null;
        this.finish();
    }

    @Override
    public void onEditStoreCancelled(Store store) {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        mFragment = null;
        this.finish();
    }
}
