package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashSet;
import java.util.Set;

import amuniz.com.honeygogroceryshoppping.model.Store;

public class AddStoreActivity extends SingleFragmentActivity implements AddStoreFragment.AddStoreCallbacks {

    private static final String EXTRA_STORES = "com.amuniz.addstoreintent.store_list";
    private HashSet<String> mStoreNames;
    private Fragment mFragment = null;

    public static Intent newIntent(Context packageContext, HashSet<String> storeNames)
    {
        Intent intent = new Intent(packageContext, AddStoreActivity.class);
        intent.putExtra(EXTRA_STORES, storeNames);

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mStoreNames = (HashSet<String>) getIntent().getSerializableExtra(EXTRA_STORES);
        mFragment = AddStoreFragment.newInstance(mStoreNames);
        return mFragment;
    }


    @Override
    public void onStoreAdded(Store store) {
//        this.dismiss();
//        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
//
        mFragment = null;
        this.finish();
    }

    @Override
    public void onCancelAdd() {
//        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        mFragment = null;
        this.finish();
    }
}
