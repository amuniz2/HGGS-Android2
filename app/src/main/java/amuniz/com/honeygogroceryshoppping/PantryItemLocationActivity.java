package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import amuniz.com.honeygogroceryshoppping.model.PantryItemLocation;
import amuniz.com.honeygogroceryshoppping.model.Store;
import amuniz.com.honeygogroceryshoppping.model.StoreLocation;

public class PantryItemLocationActivity extends SingleFragmentActivity {

    private static final String EXTRA_PANTRY_ITEM_ID = "com.amuniz.pantryitemlocationintent.pantry_item_id";
    private static final String EXTRA_PANTRY_ITEM_LOCATION_ID = "com.amuniz.pantryitemlocationintent.pantry_item_location_id";
    private static final String EXTRA_EXISTING_PANTRY_ITEM_LOCATIONS = "com.amuniz.pantryitemlocationintent.pantry_item_existingLocations";
    private Long mPantryItemId;
    private Long mOriginalLocationId;

//    private List<StoreLocation> mExistingLocations;
    private Pantry mPantry;

    @Override
    protected Fragment createFragment() {
        mPantryItemId = getIntent().getLongExtra(EXTRA_PANTRY_ITEM_ID, -1);
        mOriginalLocationId = getIntent().getLongExtra(EXTRA_PANTRY_ITEM_LOCATION_ID, -1);
        if (mOriginalLocationId == -1) {
            mOriginalLocationId = null;
        }
        mPantry = Pantry.get(this);
        return PantryItemLocationFragment.newInstance(mPantryItemId, getExistingStoreLocations(), mOriginalLocationId);
    }

    public static Intent newIntent(Context packageContext, Long locationId, Long pantryItemId) {

        Intent intent = new Intent(packageContext, PantryItemLocationActivity.class);
        intent.putExtra(EXTRA_PANTRY_ITEM_ID, pantryItemId);
        intent.putExtra(EXTRA_PANTRY_ITEM_LOCATION_ID, locationId);

        return intent;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    private HashSet<StoreLocation> getExistingStoreLocations() {
        return Pantry.get(this).getPantryItemStoreLocations(mPantryItemId).stream().collect(Collectors.toCollection(HashSet::new));
    }

//    @Override
//    public void onStoreSelected(Store store) {
//        if (findViewById(R.id.detail_fragment_container) == null) {
//            Intent intent = EditStoreActivity.newIntent(this, store.getId());
//            startActivity(intent);
//        }
//        else {
//            Fragment newDetail = EditStoreFragment.newInstance(store.getId());
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.detail_fragment_container, newDetail)
//                    .commit();
//        }
//    }
}
