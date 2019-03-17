package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashSet;

import amuniz.com.honeygogroceryshoppping.model.Store;

public class AddStoreAisleActivity extends SingleFragmentActivity implements AddStoreAisleFragment.AddStoreAisleCallbacks {

    private static final String EXTRA_STORE_ID = "com.amuniz.addstoreaisleintent.store_id";
    private static final String EXTRA_STORE_AISLES = "com.amuniz.addstoreaisleintent.store_aisle_list";
    private HashSet<String> mStoreAisles;
    private Long mStoreId;
    private Fragment mFragment = null;

    public static Intent newIntent(Context packageContext, long storeId, HashSet<String> storeAiles)
    {
        Intent intent = new Intent(packageContext, AddStoreAisleActivity.class);
        intent.putExtra(EXTRA_STORE_ID, storeId);
        intent.putExtra(EXTRA_STORE_AISLES, storeAiles);

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mStoreId = getIntent().getLongExtra(EXTRA_STORE_ID, -1);
        mStoreAisles = (HashSet<String>) getIntent().getSerializableExtra(EXTRA_STORE_AISLES);
        mFragment = AddStoreAisleFragment.newInstance(mStoreId, mStoreAisles);

        return mFragment;
    }


    @Override
    public void onStoreAisleAdded() {
        mFragment = null;
        this.finish();
    }

    @Override
    public void onCancelAdd() {
        mFragment = null;
        this.finish();
    }
}
