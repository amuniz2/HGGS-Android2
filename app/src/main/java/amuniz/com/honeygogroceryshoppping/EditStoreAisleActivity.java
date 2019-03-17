package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashSet;
import java.util.List;

import amuniz.com.honeygogroceryshoppping.model.Store;

public class EditStoreAisleActivity extends SingleFragmentActivity implements EditStoreAisleFragment.EditStoreAisleCallbacks {

    private static final String EXTRA_STORE_AISLE = "com.amuniz.editstoreaisleintent.store_aisle";
    private static final String EXTRA_STORE_ID = "com.amuniz.editstoreaisleintent.store_id";
    private static final String EXTRA_STORE_AISLES = "com.amuniz.editstoreaisleintent.aisle_list";
    private long mStoreId;
    private String mAisle;

    private Fragment mFragment = null;
    private HashSet<String> mStoreAisles;

    public static Intent newIntent(Context packageContext, long storeId, String storeAisle, HashSet<String> existingStoreAisles)
    {
        Intent intent = new Intent(packageContext, EditStoreAisleActivity.class);
        intent.putExtra(EXTRA_STORE_ID, storeId);
        intent.putExtra(EXTRA_STORE_AISLES, existingStoreAisles);
        intent.putExtra(EXTRA_STORE_AISLE, storeAisle);

        return intent;
    }

    @Override
    protected Fragment createFragment()  {
        mStoreId = getIntent().getLongExtra(EXTRA_STORE_ID, -1);
        mStoreAisles = (HashSet<String>) getIntent().getSerializableExtra(EXTRA_STORE_AISLES);
        mAisle = getIntent().getStringExtra(EXTRA_STORE_AISLE);

        mFragment = EditStoreAisleFragment.newInstance(mStoreId, mAisle, mStoreAisles);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onStoreAisleUpdated() {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();

        mFragment = null;
        this.finish();
    }

    @Override
    public void onStoreAisleDeleted() {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();

        mFragment = null;
        this.finish();
    }

    @Override
    public void onEditStoreAisleCancelled() {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        mFragment = null;
        this.finish();
    }
}
