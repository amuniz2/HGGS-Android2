package amuniz.com.honeygogroceryshoppping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import java.util.HashSet;
import java.util.stream.Collectors;


public class EditStoreGroceryAislesActivity extends SingleFragmentActivity implements EditStoreGroceryAislesFragment.EditStoreAislesCallbacks , AddStoreAisleFragment.AddStoreAisleCallbacks {
    private static final String EXTRA_STORE_ID = "com.amuniz.editstoreaisleintent.store_id";
    private static final String EXTRA_STORE_AISLE_NAME = "com.amuniz.editstoreaisleintent.aisle_name";
    public static int REQUEST_UPATE_AISLE = 0;
    private Long mStoreId;
    private String mAisle;
    private EditStoreGroceryAislesFragment mFragment = null;

    public static Intent newIntent(Context packageContext, Long storeId, String aisle) {
        Intent intent = new Intent(packageContext, EditStoreGroceryAislesActivity.class);
        intent.putExtra(EXTRA_STORE_ID, storeId);
        intent.putExtra(EXTRA_STORE_AISLE_NAME, aisle);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        mStoreId = getIntent().getLongExtra(EXTRA_STORE_ID, -1);
        mAisle = getIntent().getStringExtra(EXTRA_STORE_AISLE_NAME);

        return EditStoreGroceryAislesFragment.newInstance(mStoreId, mAisle);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

//    @Override
//    public void onStoreAisleUpdated(Store store, String aisle) {
//        if (findViewById(R.id.detail_fragment_container) == null) {
//            Intent intent = EditStoreGroceryAislesFragment.newIntent(this, store.getId(), aisle);
//            startActivity(intent);
//        }
//        else {
//            Fragment newDetail = EditStoreGroceryAislesFragment.newInstance(store.getId(), aisle);
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.detail_fragment_container, newDetail)
//                    .commit();
//        }
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

//    @Override
//    public void onStoreAisleUpdated(Store store, String aisle) {
//        EditStoreGroceryAislesFragment listFragment = (EditStoreGroceryAislesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        listFragment.updateUI();
//    }

    @Override
    public void onStoreAisleSelected(String storeAisle, int position) {
        EditStoreGroceryAislesFragment listFragment = (EditStoreGroceryAislesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.setSelectedAislePosition(position);
        listFragment.updateUI(Pantry.get(this).getStoreAisles(mStoreId));

        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = EditStoreAisleActivity.newIntent(this, mStoreId, storeAisle, getExistingStoreAisles());
            startActivityForResult(intent, REQUEST_UPATE_AISLE);
        }
        else {
            Fragment newDetail = EditStoreAisleFragment.newInstance(mStoreId, storeAisle, getExistingStoreAisles());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCreateNewStoreAisle(long storeId) {

        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = AddStoreAisleActivity.newIntent(this, storeId, getExistingStoreAisles());
            startActivity(intent);
        }
        else {
            Fragment newDetail = AddStoreAisleFragment.newInstance(storeId, getExistingStoreAisles());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

//    @Override
//    public void onAisleDeleted(String aisle) {
//        if (findViewById(R.id.detail_fragment_container) != null) {
//            EditStoreGroceryAislesFragment listFragment = (EditStoreGroceryAislesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.detail_fragment_container)).commit();
//            listFragment.setSelectedAislePosition(-1);
//            listFragment.updateUI();
//        }
//    }

    private HashSet<String> getExistingStoreAisles() {
        return Pantry.get(this).getStoreAisles(mStoreId).stream().collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public void onStoreAisleAdded() {
        mFragment = (EditStoreGroceryAislesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        mFragment.setSelectedAislePosition(-1);
        mFragment.updateUI();
    }

    @Override
    public void onCancelAdd() {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        mFragment = null;
    }
}
