package amuniz.com.honeygogroceryshoppping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import amuniz.com.honeygogroceryshoppping.model.StoreLocation;
import amuniz.com.honeygogroceryshoppping.model.Store;

public class PantryItemLocationFragment extends Fragment  {

    private static final String ARG_PANTRY_ITEM_LOCATION_ID = "com.amuniz.pantryitemlocationintent.pantry_item_location_id";
    private static final String ARG_EXISTING_LOCATIONS = "com.amuniz.pantryitemlocationintent.pantry_item_locations";
    private static final String ARG_PANTRY_ITEM_ID = "com.amuniz.pantryitemlocationintent.pantry_item_id";

    private static final String STATE_STORE_ID = "storeId";
    private static final String STATE_AISLE = "aisle";
    private static final String STATE_SECTION = "section";

    public static final String EXTRA_ITEM_LOCATION= "com.amuniz.pantryitemlocationintent.item_location";
    //public static final String EXTRA_ITEM_ORIGINAL_LOCATION_ID = "com.amuniz.pantryitemlocationintent.original_item_location_id";

    private Long mPantryItemLocationId;
    private Long mPantryItemId;

    private Long mStoreId;
    private String mAisle;
    private String mSection;

    private Spinner mStoreSpinner;
    private Adapter mStoreAdapter;
    private ImageButton mEditStores;

    private Spinner mGroceryStoreSections;
    private ImageButton mEditGroceryStoreSections;

    private Spinner mGroceryStoreAisles ;
    private ImageButton mEditGroceryStoreAisles;

    private Button mOkButton;
    private Button mCancelButton;
    private ArrayList<Store> mStoreArray;
    private ArrayList<String> mAislesArray;
    private ArrayList<String> mSectionArray;

    private Pantry mPantry;

    private static class PantryItemLocationActions {
        public static int REQUEST_ADD_STORE = 1;
        public static int REQUEST_ADD_STORE_AISLE = 2;
        public static int REQUEST_ADD_STORE_SECTION = 3;
    }

    public static PantryItemLocationFragment newInstance(Long pantryItemId, HashSet<StoreLocation> existingItemLocations)
    {
        return newInstance(pantryItemId, existingItemLocations, null);
    }

    public static PantryItemLocationFragment newInstance(Long pantryItemId, HashSet<StoreLocation> existingItemLocations, Long pantryItemLocationId)

    {
        Bundle args = new Bundle();

        args.putSerializable(ARG_PANTRY_ITEM_LOCATION_ID, pantryItemLocationId);
        args.putSerializable(ARG_EXISTING_LOCATIONS, existingItemLocations);
        args.putSerializable(ARG_PANTRY_ITEM_ID, pantryItemId);

        PantryItemLocationFragment fragment = new PantryItemLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mPantryItemLocationId = getArguments().getLong(ARG_PANTRY_ITEM_LOCATION_ID);
            mPantryItemId = getArguments().getLong(ARG_PANTRY_ITEM_ID);

            if ((mPantryItemLocationId != null) && mPantryItemLocationId > 0) {
                StoreLocation initialLocation = Pantry.get(getActivity()).getStoreLocation(mPantryItemLocationId);
                mStoreId = initialLocation.getStore().getId();
                mAisle = initialLocation.getAisle();
                mSection = initialLocation.getSection();
            }

        } else {
            mStoreId = savedInstanceState.getLong(STATE_STORE_ID);
            mAisle = savedInstanceState.getString(STATE_AISLE);
            mSection = savedInstanceState.getString(STATE_SECTION);
        }
    }
    @Override
    public void onResume()
    {
        updateUI();

        super.onResume();
    }

    private void updateUI() {
        populateStoreArrayAdaper(mStoreId);
        populateAisleArrayAdapter(mStoreId);
        populateGrocerySectionArrayAdapter(mStoreId);

        //        Pantry pantry = Pantry.get(getActivity());
//        List<String> storeSections = pantry.getStoreGrocerySections(mStoreId);
//
//        if (mAdapter == null) {
//            mAdapter = new EditStoreGrocerySectionsFragment.StoreSectionsAdapter(storeSections, mSelectedSectionPosition);
//            mStoreSectionsRecyclerView.setAdapter(mAdapter);
//        }
//        else {
//            mAdapter.setStoreSections(storeSections, mSelectedSectionPosition);
//            mAdapter.notifyDataSetChanged();
//        }
    }
    private HashSet<String> getExistingStoreNames() {
        return Pantry.get(getActivity()).getStores().stream().map(store -> store.getName()).collect(Collectors.toCollection(HashSet::new));
    }
    private HashSet<String> getExistingStoreAisles(long storeId) {
        return Pantry.get(getActivity()).getStoreAisles(storeId).stream().collect(Collectors.toCollection(HashSet::new));
    }
    private HashSet<String> getExistingStoreSections(long storeId) {
        return Pantry.get(getActivity()).getStoreGrocerySections(storeId).stream().collect(Collectors.toCollection(HashSet::new));
    }

    private void populateStoreArrayAdaper(Long selectStoreId) {
        mStoreArray.clear();
        mStoreArray.addAll(Pantry.get(getActivity()).getStores());
        ArrayAdapter<Store> adapter = (ArrayAdapter<Store>)mStoreSpinner.getAdapter();
        synchronized (adapter) {
            adapter.notifyAll();
            adapter.notifyDataSetChanged();
        }
        if (selectStoreId != null) {
            mStoreSpinner.setSelection(adapter.getPosition(Pantry.get(getActivity()).getStore(selectStoreId)));
        }
    }
    private void initializeStoreSpinner() {
        mStoreArray = new ArrayList<>( Pantry.get(getActivity()).getStores());
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item,
                        mStoreArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        mStoreSpinner.setAdapter(spinnerArrayAdapter);
        if (mStoreId != null) {
            Store store = Pantry.get(getActivity()).getStore(mStoreId);
            int position = spinnerArrayAdapter.getPosition(store);
            mStoreSpinner.setSelection(position);
        }

        mStoreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if store is changed, we need to get the new options for aisles and sections
                Store selectedStore = (Store)mStoreSpinner.getSelectedItem();
                populateAisleArrayAdapter(selectedStore.getId());
                populateGrocerySectionArrayAdapter(selectedStore.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // if no store is selected, then we need to clear values for aisle and section
                populateAisleArrayAdapter(null);
                populateGrocerySectionArrayAdapter(null);
            }
        });
    }

    private void populateAisleArrayAdapter(Long storeId) {
        mAislesArray.clear();
        ArrayAdapter<String> adapter = (ArrayAdapter<String>)mGroceryStoreAisles.getAdapter();
        if (storeId != null) {
            mAislesArray.add("<None>");
            mAislesArray.addAll(Pantry.get(getActivity()).getStoreAisles(storeId));
        }
        synchronized (adapter) {
            adapter.notifyAll();
            adapter.notifyDataSetChanged();
        }
        if ((mAisle != null) && (mAisle.length() > 0)) {
            mGroceryStoreAisles.setSelection(adapter.getPosition(mAisle) + 1);
        } else {
            mGroceryStoreAisles.setSelection(0);
        }

    }
    private void populateGrocerySectionArrayAdapter(Long storeId) {
        mSectionArray.clear();
        ArrayAdapter<String> adapter = (ArrayAdapter<String>)mGroceryStoreSections.getAdapter();
        if (storeId != null) {
            mSectionArray.add("<None>");
            mSectionArray.addAll(Pantry.get(getActivity()).getStoreGrocerySections(storeId));
        }
        synchronized (adapter) {
            adapter.notifyAll();
            adapter.notifyDataSetChanged();
        }
        if ((mSection != null) && (mSection.length() > 0)) {
            mGroceryStoreSections.setSelection(adapter.getPosition(mSection) + 1);
        } else {
            mGroceryStoreSections.setSelection(0);
        }
    }
    private void initializeAislesSpinner() {
        mAislesArray = new ArrayList<>(Pantry.get(getActivity()).getStoreAisles(mStoreId));
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item,
                        mAislesArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        mGroceryStoreAisles.setAdapter(spinnerArrayAdapter);
        if ((mAisle != null) && (mAisle.length() > 0)) {
            mGroceryStoreAisles.setSelection(spinnerArrayAdapter.getPosition(mAisle) + 1);
        } else {
            mGroceryStoreAisles.setSelection(0);
        }
    }

    private void initailizeSectionSpinner() {
        mSectionArray = new ArrayList<>(Pantry.get(getActivity()).getStoreGrocerySections(mStoreId));

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item,
                        mSectionArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        mGroceryStoreSections.setAdapter(spinnerArrayAdapter);
        if ((mSection != null) && (mSection.length() > 0)) {
            mGroceryStoreSections.setSelection(spinnerArrayAdapter.getPosition(mSection) + 1);
        } else {
            mGroceryStoreSections.setSelection(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == PantryItemLocationActions.REQUEST_ADD_STORE) {
            mStoreId = AddStoreFragment.getAddedStoreId(data);
        }
        else if (requestCode == PantryItemLocationActions.REQUEST_ADD_STORE_AISLE) {
            mAisle = AddStoreAisleFragment.getAddedStoreAisle(data);
        }
        else if (requestCode == PantryItemLocationActions.REQUEST_ADD_STORE_SECTION) {
            mSection = AddStoreSectionFragment.getAddedStoreSection(data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mStoreId != null) {
            savedInstanceState.putLong(STATE_STORE_ID, mStoreId);
            savedInstanceState.putString(STATE_AISLE, mAisle);
            savedInstanceState.putString(STATE_SECTION, mSection);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.fragment_select_pantry_item_location, container, false);
        mStoreSpinner = v.findViewById(R.id.stores_spinner);
        mEditStores = v.findViewById(R.id.edit_stores_button);
        mGroceryStoreAisles = v.findViewById(R.id.spinner_aisles);
        mEditGroceryStoreAisles = v.findViewById(R.id.edit_aisles_button);
        mGroceryStoreSections = v.findViewById(R.id.spinner_sections);
        mEditGroceryStoreSections = v.findViewById(R.id.edit_sections_button);
        mOkButton = v.findViewById(R.id.ok_button);
        mPantry = Pantry.get(getActivity());

        initializeStoreSpinner();
        initializeAislesSpinner();
        initailizeSectionSpinner();

        mEditStores.setOnClickListener(v1 -> {
            Intent activity = AddStoreActivity.newIntent(getActivity(), getExistingStoreNames());
            startActivityForResult(activity, PantryItemLocationActions.REQUEST_ADD_STORE);
        });

        mEditGroceryStoreAisles.setOnClickListener(v1 -> {
            Store selectedStore = (Store)mStoreSpinner.getSelectedItem();
            Long storeId = selectedStore.getId();

//            Intent activity = EditStoreGroceryAislesActivity.newIntent(getActivity(), storeId,
//                    (aisle == null || aisle.equals("<None>")) ? "" : aisle.toString());
            Intent activity = AddStoreAisleActivity.newIntent(getActivity(), storeId, getExistingStoreAisles(storeId));
            startActivityForResult(activity, PantryItemLocationActions.REQUEST_ADD_STORE_AISLE);
        });

        mEditGroceryStoreSections.setOnClickListener(v1 -> {
            Store selectedStore = (Store)mStoreSpinner.getSelectedItem();
            Long storeId = selectedStore.getId();
            Object section = mGroceryStoreSections.getSelectedItem();

//            Intent activity = EditStoreGrocerySectionsActivity.newIntent(getActivity(), storeId,
//                    (section == null || section.equals("<None>")) ? "" : section.toString());
            Intent activity = AddStoreSectionActivity.newIntent(getActivity(), storeId, getExistingStoreSections(storeId));
            startActivityForResult(activity, PantryItemLocationActions.REQUEST_ADD_STORE_SECTION);
        });

        mOkButton.setOnClickListener(v12 -> {
            Store selectedStore = (Store)mStoreSpinner.getSelectedItem();
            String selectedAisle = (String) mGroceryStoreAisles.getSelectedItem();
            String selectedSection = (String) mGroceryStoreSections.getSelectedItem();

            sendResult(selectedStore.getId(),
                    (selectedAisle  == null || selectedAisle.equals("<None>") ) ? "" : selectedAisle,
                    (selectedSection == null  || selectedSection.equals("<None>") ) ? "" : selectedSection);
            getActivity().finish();
        });
        mCancelButton = v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED, new Intent());
                getActivity().finish();
            }
        });
        return v;
    }

    private void sendResult(Long storeId, String aisle, String sectionName) {
        Intent intent = new Intent();
        int resultCode = Activity.RESULT_CANCELED;

        if (storeId == null) {
            // no valid location was selected - cancel
            getActivity().setResult(Activity.RESULT_CANCELED, intent);
            return;
        }
        Long selectedLocationId = mPantry.findStoreLocation(storeId, aisle, sectionName);
        if (selectedLocationId == null || selectedLocationId == 0) {
            // non-existing store location specified, add the store location
            selectedLocationId = mPantry.addStoreLocation(storeId, aisle, sectionName);
        }

        if (mPantryItemLocationId == selectedLocationId) {
            // the selected location is the same as the existing location, no need to do
            // anything
            getActivity().setResult(Activity.RESULT_CANCELED, intent);
            return;
        }

        if (mPantryItemLocationId == null || mPantryItemLocationId == 0) {
            // no original location for the pantry item; add
            mPantry.addPantryItemLocation(mPantryItemId, selectedLocationId);
        } else {
            // new location replaces old location
            mPantry.updatePantryItemLocation(mPantryItemId, mPantryItemLocationId, selectedLocationId);
        }

        intent.putExtra(EXTRA_ITEM_LOCATION, selectedLocationId);
        getActivity().setResult(Activity.RESULT_OK, intent);
    }
    private ArrayList<String> convertToArrayOfStoreNames(List<Store> stores) {
        ArrayList<String> storeNameArray = new ArrayList();

        for (Store store : stores) {

            storeNameArray.add(store.getName());

        }
        return storeNameArray;

    }


//    private class StoreAdapter extends RecyclerView.Adapter<StoreHolder>
//    {
//        private List<Store> mStores;
//
//        public StoreAdapter(List<Store> stores) { setStores(getStores());}
//
//        @Override
//        public StoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return null;
//        }
//
//        @Override
//        public void onBindViewHolder(StoreHolder holder, int position) {
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return 0;
//        }
//
//        public List<Store> getStores() {
//            return mStores;
//        }
//
//        public void setStores(List<Store> stores) {
//            mStores = stores;
//        }
//    }
//
//    private class StoreHolder extends RecyclerView.ViewHolder
//    {
//        public StoreHolder(LayoutInflater inflater, ViewGroup parent) {
//            super(inflater.inflate(R.layout?, parent, false));
//
//            mStoreName = itemView.findViewById(R.id.store_name);
//        }
//
//    }


}
