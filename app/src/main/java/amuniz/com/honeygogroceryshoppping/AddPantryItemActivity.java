package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;

import java.util.HashSet;

import amuniz.com.honeygogroceryshoppping.model.PantryItem;

import static amuniz.com.honeygogroceryshoppping.AddPantryItemFragment.AddItemCallbacks;

public class AddPantryItemActivity extends SingleFragmentActivity implements AddItemCallbacks {
    private AddPantryItemFragment mFragment;

    private static final String EXTRA_PANTRY_ITEMS = "com.amuniz.addstoreintent.pantry_list";
    private HashSet<String> mPantryItemNames;
    public static Intent newIntent(Context packageContext, HashSet<String> pantryItemNames)
    {
        Intent intent = new Intent(packageContext, AddPantryItemActivity.class);
        intent.putExtra(EXTRA_PANTRY_ITEMS, pantryItemNames);

        return intent;
    }


    @Override
    protected Fragment createFragment() {
        mPantryItemNames = (HashSet<String>) getIntent().getSerializableExtra(EXTRA_PANTRY_ITEMS);
        mFragment = AddPantryItemFragment.newInstance(mPantryItemNames);
        return mFragment;
    }


    @Override
    public void onPantryItemAdded(PantryItem pantryItem, HashSet<Location> assignedLocations) {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        mFragment = null;
        this.finish();
    }

    @Override
    public void onCancelAdd() {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        mFragment = null;
        this.finish();
    }
}
