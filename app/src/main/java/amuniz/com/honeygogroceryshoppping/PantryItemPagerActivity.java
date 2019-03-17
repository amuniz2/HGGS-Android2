package amuniz.com.honeygogroceryshoppping;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import amuniz.com.honeygogroceryshoppping.model.PantryItem;
import amuniz.com.honeygogroceryshoppping.model.PantryItemLocation;
import amuniz.com.honeygogroceryshoppping.model.StoreLocation;

public class PantryItemPagerActivity extends AppCompatActivity implements PantryItemFragment.EditPantryItemCallbacks {

    ViewPager mViewPager;
    private List<PantryItem> mPantryItems;

    private static final String EXTRA_PANTRY_ITEM_ID = "com.amuniz.pantryitemintent.pantry_item_id";

    private boolean mItemDeleted;
    private Long mId;
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;
    private PantryItem mPantryItem;

    public static Intent newIntent(Context packageContext, Long pantryItemId)
    {
        Intent intent = new Intent(packageContext, PantryItemPagerActivity.class);
        intent.putExtra(EXTRA_PANTRY_ITEM_ID, pantryItemId);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_item_pager);

        try {
            mViewPager = (ViewPager) findViewById(R.id.pantry_item_pager);
            mPantryItems = Pantry.get(this).getPantryItems();
            mId = getIntent().getLongExtra(EXTRA_PANTRY_ITEM_ID, -1);
            mPantryItem = Pantry.get(this).getPantryItem(mId);

            FragmentManager fragmentManager = getSupportFragmentManager();
            mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
                @Override
                public Fragment getItem(int position) {
                    PantryItem pantryItem = mPantryItems.get(position);
                    return PantryItemFragment.newInstance(pantryItem.getId());
                }

                @Override
                public int getCount() {
                    return mPantryItems.size();
                }
            });

            for (int i = 0; i < mPantryItems.size(); i++) {
                if (mPantryItems.get(i).getId() == mId) {
                    mViewPager.setCurrentItem(i);
                    break;
                }
            }
        } catch (Exception ex) {
            DialogUtils.showErrorMessage(this, String.format("Error initializing pager activity: %s.", ex.getMessage()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.fragment_pantry_item, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);;

        setShareIntent(createShareIntent(mPantryItem));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.delete_pantry_item:
                confirmDelete();
//                if (mItemDeleted)
//                    this.finish();
                return true;

            case R.id.menu_item_share:
                startActivity(Intent.createChooser(mShareIntent, getResources().getText(R.string.send_to_text)));
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private Intent createShareIntent(PantryItem pantryItem) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, pantryItem.getTextDefinition());
        shareIntent.setType("text/plain"); // todo: send image? html? see https://developer.android.com/training/sharing/send
        return shareIntent;
    }
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareIntent = shareIntent;
            mShareActionProvider.setShareIntent(mShareIntent);
        }
    }

    private void confirmDelete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //PantryItem pantryItem = Pantry.get(this).getPantryItem(mId);
        builder.setMessage(getString(R.string.conform_delete_item_message, mPantryItem.getName()));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getActivity(), "You've changed your mind to delete all records", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void deleteItem()
    {
        //PantryItem pantryItem = Pantry.get(this).getPantryItem(mId);
        Pantry.get(this).removePantryItem(mPantryItem);
        mItemDeleted = true;
        finish();
    }

    @Override
    public void onPantryItemUpdated(PantryItem pantryItem) {
        this.mViewPager.getAdapter().notifyDataSetChanged();
    }
}
