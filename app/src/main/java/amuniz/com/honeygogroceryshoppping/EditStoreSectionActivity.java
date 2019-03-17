package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashSet;

public class EditStoreSectionActivity extends SingleFragmentActivity implements EditStoreSectionFragment.EditStoreSectionCallbacks {

    private static final String EXTRA_STORE_SECTION = "com.amuniz.editstoresectionintent.store_section";
    private static final String EXTRA_STORE_ID = "com.amuniz.editstoresectionintent.store_id";
    private static final String EXTRA_STORE_SECTIONS = "com.amuniz.editstoresectionintent.section_list";
    private long mStoreId;
    private String mSection;

    private Fragment mFragment = null;
    private HashSet<String> mStoreSections;

    public static Intent newIntent(Context packageContext, long storeId, String storeSection, HashSet<String> existingStoreSections)
    {
        Intent intent = new Intent(packageContext, EditStoreSectionActivity.class);
        intent.putExtra(EXTRA_STORE_ID, storeId);
        intent.putExtra(EXTRA_STORE_SECTIONS, existingStoreSections);
        intent.putExtra(EXTRA_STORE_SECTION, storeSection);

        return intent;
    }

    @Override
    protected Fragment createFragment()  {
        mStoreId = getIntent().getLongExtra(EXTRA_STORE_ID, -1);
        mStoreSections = (HashSet<String>) getIntent().getSerializableExtra(EXTRA_STORE_SECTIONS);
        mSection = getIntent().getStringExtra(EXTRA_STORE_SECTION);

        mFragment = EditStoreSectionFragment.newInstance(mStoreId, mSection, mStoreSections);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onStoreSectionUpdated() {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();

        mFragment = null;
        this.finish();
    }

    @Override
    public void onStoreSectionDeleted() {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();

        mFragment = null;
        this.finish();
    }

    @Override
    public void onEditStoreSectionCancelled() {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        mFragment = null;
        this.finish();
    }
}
