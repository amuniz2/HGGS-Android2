package amuniz.com.honeygogroceryshoppping;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.Fragment;

import java.util.HashSet;
import java.util.stream.Collectors;

import amuniz.com.honeygogroceryshoppping.model.PantryItem;

/**
 * Created by amuni on 4/2/2018.
 */

public class PantryListActivity extends SingleFragmentActivity
        implements PantryItemFragment.EditPantryItemCallbacks, PantryListFragment.PantryItemCallbacks, AddPantryItemFragment.AddItemCallbacks {
    private static final int ADD_NEW_PANTRY_ITEM = 0;
    private Fragment mFragment;

    @Override
    protected Fragment createFragment() {
        return new PantryListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    public void onPantryItemSelected(PantryItem pantryItem) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = PantryItemPagerActivity.newIntent(this, pantryItem.getId());
            startActivity(intent);
        }
        else {
            Fragment newDetail = PantryItemFragment.newInstance(pantryItem.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                .commit();
        }
    }

    public void onPantryItemUpdated(PantryItem pantryItem) {
        PantryListFragment listFragment = (PantryListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onCreateNewPantryItem() {
        HashSet<String> pantryItemNames = Pantry.get(this).getPantryItems().stream().map(item -> item.getName()).collect(Collectors.toCollection(HashSet::new));
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = AddPantryItemActivity.newIntent(this, pantryItemNames);
            startActivity(intent);
        }
        else {
            mFragment = AddPantryItemFragment.newInstance(pantryItemNames);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, mFragment)
                    .commit();
        }
    }

    @Override
    public void onPantryItemAdded(PantryItem pantryItem, HashSet<Location> assignedLocations) {
        PantryListFragment listFragment = (PantryListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        mFragment = null;
    }

    @Override
    public void onCancelAdd() {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        mFragment = null;
    }
}
