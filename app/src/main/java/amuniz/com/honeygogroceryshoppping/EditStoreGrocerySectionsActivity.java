package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.HashSet;
import java.util.stream.Collectors;


public class EditStoreGrocerySectionsActivity extends SingleFragmentActivity implements EditStoreGrocerySectionsFragment.EditStoreSectionsCallbacks , AddStoreSectionFragment.AddStoreSectionCallbacks {
    private static final String EXTRA_STORE_ID = "com.amuniz.editstoreaisleintent.store_id";
    private static final String EXTRA_STORE_SECTION_NAME = "com.amuniz.editstoresectionintent.section_name";
    private Long mStoreId;
    private String mSection;
    private EditStoreGrocerySectionsFragment mFragment = null;

    public static Intent newIntent(Context packageContext, Long storeId, String aisle) {
        Intent intent = new Intent(packageContext, EditStoreGrocerySectionsActivity.class);
        intent.putExtra(EXTRA_STORE_ID, storeId);
        intent.putExtra(EXTRA_STORE_SECTION_NAME, aisle);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        mStoreId = getIntent().getLongExtra(EXTRA_STORE_ID, -1);
        mSection = getIntent().getStringExtra(EXTRA_STORE_SECTION_NAME);

        return EditStoreGrocerySectionsFragment.newInstance(mStoreId, mSection);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onStoreSectionSelected(String storeSection, int position) {
        EditStoreGrocerySectionsFragment listFragment = (EditStoreGrocerySectionsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.setSelectedSectionPosition(position);
        listFragment.updateUI();

        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = EditStoreSectionActivity.newIntent(this, mStoreId, storeSection, getExistingStoreSections());
            startActivity(intent);
        }
        else {
            Fragment newDetail = EditStoreSectionFragment.newInstance(mStoreId, storeSection, getExistingStoreSections());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCreateNewStoreSection(long storeId) {

        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = AddStoreSectionActivity.newIntent(this, storeId, getExistingStoreSections());
            startActivity(intent);
        }
        else {
            Fragment newDetail = AddStoreSectionFragment.newInstance(storeId, getExistingStoreSections());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    private HashSet<String> getExistingStoreSections() {
        return Pantry.get(this).getStoreGrocerySections(mStoreId).stream().collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public void onStoreSectionAdded() {
        mFragment = (EditStoreGrocerySectionsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        mFragment.setSelectedSectionPosition(-1);
        mFragment.updateUI();
    }

    @Override
    public void onCancelAdd() {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        mFragment = null;
    }
}
