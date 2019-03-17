package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.HashSet;

public class AddStoreSectionActivity extends SingleFragmentActivity implements AddStoreSectionFragment.AddStoreSectionCallbacks {

    private static final String EXTRA_STORE_ID = "com.amuniz.addstoresectionintent.store_id";
    private static final String EXTRA_STORE_SECTIONS = "com.amuniz.addstoresectionintent.store_aisle_list";
    private HashSet<String> mStoreSections;
    private Long mStoreId;
    private Fragment mFragment = null;

    public static Intent newIntent(Context packageContext, long storeId, HashSet<String> storeSections)
    {
        Intent intent = new Intent(packageContext, AddStoreSectionActivity.class);
        intent.putExtra(EXTRA_STORE_ID, storeId);
        intent.putExtra(EXTRA_STORE_SECTIONS, storeSections);

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mStoreId = getIntent().getLongExtra(EXTRA_STORE_ID, -1);
        mStoreSections = (HashSet<String>) getIntent().getSerializableExtra(EXTRA_STORE_SECTIONS);
        mFragment = AddStoreSectionFragment.newInstance(mStoreId, mStoreSections);

        return mFragment;
    }


    @Override
    public void onStoreSectionAdded() {
        mFragment = null;
        this.finish();
    }

    @Override
    public void onCancelAdd() {
        mFragment = null;
        this.finish();
    }
}
