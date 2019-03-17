package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import amuniz.com.honeygogroceryshoppping.model.Store;

import static java.lang.System.in;


public class EditStoresActivity extends SingleFragmentActivity implements EditStoresFragment.EditStoresCallbacks, EditStoreFragment.EditStoreCallbacks, AddStoreFragment.AddStoreCallbacks {
    private EditStoresFragment mFragment;
//    private int mSelectedStorePosition;
    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, EditStoresActivity.class);

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return EditStoresFragment.newInstance();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    private HashSet<String> getExistingStoreNames() {
        return Pantry.get(this).getStores().stream().map(store -> store.getName()).collect(Collectors.toCollection(HashSet::new));
    }
    @Override
    public void onCreateNewStore() {

        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = AddStoreActivity.newIntent(this, getExistingStoreNames());
            startActivity(intent);
        }
        else {
            Fragment newDetail = AddStoreFragment.newInstance(getExistingStoreNames());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }

    }

    @Override
    public void onStoreSelected(Store store, int position) {
//        mSelectedStorePosition = position;
        EditStoresFragment listFragment = (EditStoresFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.setSelectedStorePosition(position);
        listFragment.updateUI();

        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = EditStoreActivity.newIntent(this, store.getId(), getExistingStoreNames());
            startActivity(intent);
        }
        else {
            Fragment newDetail = EditStoreFragment.newInstance(store.getId(), getExistingStoreNames());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }

    }

    @Override
    public void onStoreUpdated(Store store) {
        EditStoresFragment listFragment = (EditStoresFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onStoreDeleted(Store store) {
        if (findViewById(R.id.detail_fragment_container) != null) {
            mFragment = (EditStoresFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.detail_fragment_container)).commit();
            mFragment.setSelectedStorePosition(-1);
            mFragment.updateUI();
        }
    }

    @Override
    public void onEditStoreCancelled(Store store) {
        // ?
    }

    @Override
    public void onStoreAdded(Store store) {
        mFragment = (EditStoresFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        mFragment.setSelectedStorePosition(-1);
        HashSet<String> storeNames = getExistingStoreNames();
        int position = 0;
        for(String storeName: storeNames) {
            if (store.getName().equals(storeName)) {
                mFragment.setSelectedStorePosition(position);
                break;
            }
            else
                position++;
        }
        mFragment.updateUI();
    }

    @Override
    public void onCancelAdd() {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        mFragment = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
