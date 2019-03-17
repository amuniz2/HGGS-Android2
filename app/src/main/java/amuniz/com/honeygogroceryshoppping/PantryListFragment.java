package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import amuniz.com.honeygogroceryshoppping.PantryItemFragment.EditPantryItemCallbacks;
import amuniz.com.honeygogroceryshoppping.model.PantryItem;
import amuniz.com.honeygogroceryshoppping.model.ShoppingItem;

/**
 * Created by amuni on 4/1/2018.
 */

public class PantryListFragment  extends Fragment implements EditPantryItemCallbacks {

    private static final int EDIT_PANTRY_ITEM = 1;
    private static int indexOfItemBeingEdited = -1;
    private static String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mPantryItemRecyclerView;
    private PantryItemAdapter mAdapter;
    private boolean mSubtitleVisible;
    private PantryItemCallbacks mCallbacks;

    @Override
    public void onPantryItemUpdated(PantryItem pantryItem) {

    }

    public interface PantryItemCallbacks {
        void onPantryItemSelected(PantryItem pantryItem);
        void onCreateNewPantryItem();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.fragment_pantry_list, container, false);

        mPantryItemRecyclerView = v.findViewById(R.id.pantry_item_recycler_view);
        mPantryItemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null)
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);

        updateUI();

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // todo: instead, save in state data the index of the item that was edited, then retrieve it in onResume(?) to only update the ui of that one element

        updateUI();
        updateSubtitle();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (PantryItemCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_pantry_item_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        subtitleItem.setTitle(mSubtitleVisible ? R.string.hide_subtitle : R.string.show_subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.new_pantry_item:
                mCallbacks.onCreateNewPantryItem();
                return true;

            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void updateUI()
    {
        Pantry pantry = Pantry.get(getActivity());
        List<PantryItem> pantryItems = pantry.getPantryItems();

        if (mAdapter == null) {
            mAdapter = new PantryItemAdapter(pantryItems);
            mPantryItemRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setPantryItems(pantryItems);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();

    }
    private void updateShoppingItem(ShoppingItem shoppingItem)
    {
        Pantry.get(getActivity()).updateShoppingItem(shoppingItem);
//        mEditItemCallbacks.onPantryItemUpdated(mPantryItem);
    }

    private void updateSubtitle()
    {
        Pantry pantry = Pantry.get(getActivity());
        int itemCount = pantry.getPantryItems().size();
        String subtitle = mSubtitleVisible ? getResources().getQuantityString(R.plurals.subtitle_plural, itemCount) : null;

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        if (requestCode == EDIT_PANTRY_ITEM) {
//
//            if (resultCode == Activity.RESULT_OK) {
//                // do something...?
//                mAdapter.notifyItemChanged(data.getIntExtra(PantryItemPagerActivity.EXTRA_PANTRY_ITEM_NDX, getIndexOfItemBeingEdited()));
//            }
//        }
//    }

    private class PantryItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CheckBox mSelected;
        private PantryItem mPantryItem;
        private ShoppingItem mShoppingItem;
        private ImageView mSolvedImageView;
        private TextView mDescription;
//        private TextView mQuantityView;
        private EditText mQuantityField;
        private TextView mUnitField;
        private int mIndex;

        public PantryItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_pantry, parent, false));

            mSelected = itemView.findViewById(R.id.pantry_item_checked);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
            mDescription = itemView.findViewById(R.id.pantry_item_description);
            mQuantityField = itemView.findViewById(R.id.quantity);
            mUnitField = itemView.findViewById(R.id.quantity_unit);
        }

        public void bind(PantryItem pantryItem, int index, ShoppingItem shoppingItem)
        {
            mPantryItem = pantryItem;
            mShoppingItem = shoppingItem;
            mIndex = index;
            mSelected.setText(pantryItem.getName());
            mSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    Pantry.get(getActivity()).addPantryItemToShoppingList(mPantryItem.getId());
                }
                else {
                    Pantry.get(getActivity()).removePantryItemFromShoppingList(mPantryItem.getId());
                }
                mQuantityField.setEnabled(isChecked);
            });

            mDescription.setText(pantryItem.getDescription());
            if (shoppingItem != null) {
                mQuantityField.setText(Double.toString(shoppingItem.getQuantity().getQuantity()));
                mUnitField.setText(shoppingItem.getQuantity().getUnit());
                mQuantityField.setEnabled(mShoppingItem.getSelected());

                mQuantityField.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after){}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mShoppingItem.getQuantity().setQuantity(Double.parseDouble(s.toString()));
                        updateShoppingItem(shoppingItem);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            } else {
                mQuantityField.setEnabled(false);
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onPantryItemSelected(mPantryItem);
//            Intent intent = PantryItemPagerActivity.newIntent(getActivity(), mPantryItem.getId());
//            startActivity(intent);
            //startActivityForResult(intent, EDIT_PANTRY_ITEM);
        }
    }

    private class PantryItemAdapter extends RecyclerView.Adapter<PantryItemHolder> {

        private List<PantryItem> mPantryItems;

        public PantryItemAdapter(List<PantryItem> pantryItems)
        {
            setPantryItems(pantryItems);
        }

        @Override
        public PantryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PantryItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PantryItemHolder holder, int position) {
            PantryItem pantryItem = getPantryItems().get(position);
            ShoppingItem shoppingItem = Pantry.get(getActivity()).getShoppingItem(pantryItem.getId());
            holder.bind(pantryItem, position, shoppingItem);
        }

        @Override
        public int getItemCount() {
            return getPantryItems().size();
        }

        public List<PantryItem> getPantryItems() {
            return mPantryItems;
        }

        public void setPantryItems(List<PantryItem> pantryItems) {
            mPantryItems = pantryItems;
        }
    }
}
