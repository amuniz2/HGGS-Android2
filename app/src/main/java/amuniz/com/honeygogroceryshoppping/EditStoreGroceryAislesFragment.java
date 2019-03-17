package amuniz.com.honeygogroceryshoppping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import static android.graphics.Color.CYAN;
import static android.graphics.Color.WHITE;

public class   EditStoreGroceryAislesFragment extends Fragment {

    private static final String ARG_STORE_ID = "store_id";
    private static final String ARG_STORE_AISLE = "store_aisle";

    private String mAisle;
    private EditText mAisleField;
    private long mStoreId;
    private int mSelectedAislePosition;

    private RecyclerView mStoreAislesRecyclerView;
    private EditStoreGroceryAislesFragment.StoreAislesAdapter mAdapter;
    private EditStoreAislesCallbacks mEditStoreAislesCallbacks;

    public interface EditStoreAislesCallbacks {
        void onCreateNewStoreAisle(long storeId);
        void onStoreAisleSelected(String storeAisle, int index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == EditStoreGroceryAislesActivity.REQUEST_UPATE_AISLE) {
            updateUI();
        }

    }
    public static EditStoreGroceryAislesFragment newInstance(Long storeId, String aisle)
    {
        Bundle args = new Bundle();

        args.putLong(ARG_STORE_ID, storeId);
        args.putString(ARG_STORE_AISLE, aisle);

        EditStoreGroceryAislesFragment fragment = new EditStoreGroceryAislesFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_edit_store_aisle_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.new_store_aisle:
                try {
                    mEditStoreAislesCallbacks.onCreateNewStoreAisle(mStoreId);
                }
                catch (Exception ex) {
                    DialogUtils.showErrorMessage(getContext(), "Error adding store aisle.");
                }
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(ARG_STORE_ID, mStoreId);
        savedInstanceState.putString(ARG_STORE_AISLE, mAisle);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        List<String> storeAisles = Pantry.get(getActivity()).getStoreAisles(mStoreId);
        mStoreId = getArguments().getLong(ARG_STORE_ID);
        mAisle = getArguments().getString(ARG_STORE_AISLE);
        mSelectedAislePosition = isValidAisle(mAisle) ? storeAisles.indexOf(mAisle) : 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pantry pantry = Pantry.get(getActivity());
        List<String> storeAisles = pantry.getStoreAisles(mStoreId);

        mStoreId = getArguments().getLong(ARG_STORE_ID);
        mAisle = getArguments().getString(ARG_STORE_AISLE);
        mSelectedAislePosition = isValidAisle(mAisle) ? storeAisles.indexOf(mAisle) : 0;
        setRetainInstance(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_store_aisles_list, container, false);

        mStoreAislesRecyclerView = v.findViewById(R.id.store_aisles_recycler_view);
        mStoreAislesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setHasOptionsMenu(true);
        updateUI(Pantry.get(getActivity()).getStoreAisles(mStoreId));

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private boolean isValidAisle(String aisle) {
        return aisle != null && !aisle.equals("") && !aisle.equals("<None>");
    }
    public void updateUI()
    {
        updateUI(Pantry.get(getActivity()).getStoreAisles(mStoreId));
    }
    public void updateUI(List<String> storeAisles)
    {
        if (mAdapter == null) {
            mAdapter = new EditStoreGroceryAislesFragment.StoreAislesAdapter(storeAisles, mSelectedAislePosition);
            mStoreAislesRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setStoreAisles(storeAisles, mSelectedAislePosition);
            mAdapter.notifyDataSetChanged();
        }
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        //return super.onCreateView(inflater, container, savedInstanceState);
//        View v = inflater.inflate(R.layout.fragment_store_grocery_aisle, container, false);
//
//        mAisleField = v.findViewById(R.id.aisle);
//
//        mAisleField.setText(mAisle);
//        mAisleField.addTextChangedListener(new TextValidator(mAisleField) {
//            @Override public void validate(TextView textView, String text) {
//                /* Validation code here */
//                if (text.length() == 0)
//                    textView.setError("The name of the aisle is required.");
//                else {
//                    mAisle = text;
//                    try {
//                        updateStoreAisle();
//                    }
//                    catch(Exception ex) {
//                        textView.setError(String.format("Store %s already exists.", text));
//                    }
//                }
//            }
//        });
//
//        return v;
//    }

//    private void updateStoreAisle()
//    {
//        Pantry pantry = Pantry.get(getActivity());
//
//
//        if (mStore == null) {
//            throw new InvalidParameterException("mStore should not be null");
//        } else {
//            pantry.addStoreAisle(mStore.getId(), mAisle);
////            pantry.updateStoreLocation(mStore.getId(), mItemLocation.getId(), mItemLocation.getAisle(), mItemLocation.getName());
//        }
//
//        mCallbacks.onStoreAisleUpdated(mStore, mAisle);
//    }

    @Override
    public void  onAttach(Context context) {
        super.onAttach(context);
        mEditStoreAislesCallbacks = (EditStoreGroceryAislesFragment.EditStoreAislesCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEditStoreAislesCallbacks = null;
    }

    public void setSelectedAislePosition(int position) {
        mSelectedAislePosition = position;
    }

    private class StoreAisleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private String mAisle;
        private TextView mName;
        private int mIndex;

        public StoreAisleHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_aisle, parent, false));

            mName = itemView.findViewById(R.id.store_aisle);
        }

        public void bind(String storeAisle, int index, boolean selected)
        {;
            mIndex = index;
            mAisle = storeAisle;
            mName.setText(storeAisle);

            if (selected) {
                itemView.setBackgroundColor(CYAN);
            } else {
                itemView.setBackgroundColor(WHITE);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            mEditStoreAislesCallbacks.onStoreAisleSelected(mAisle, mIndex);
        }

    }

    private class StoreAislesAdapter extends RecyclerView.Adapter<StoreAisleHolder> {

        private List<String> mStoreAisles;
        private int mSelectedAisleIndex = -1;

        public StoreAislesAdapter(List<String> storeAisles, int selectedStoreAisleIndex)
        {
            mSelectedAisleIndex = selectedStoreAisleIndex;
            setStoreAisles(storeAisles, selectedStoreAisleIndex);
        }

        @Override
        public EditStoreGroceryAislesFragment.StoreAisleHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new EditStoreGroceryAislesFragment.StoreAisleHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(EditStoreGroceryAislesFragment.StoreAisleHolder holder, int position) {
            String storeAisle = getStoreAisles().get(position);
            holder.bind(storeAisle, position, position == mSelectedAisleIndex);
        }

        @Override
        public int getItemCount() {
            return getStoreAisles().size();
        }

        public List<String> getStoreAisles() {
            return mStoreAisles;
        }

        public void setStoreAisles(List<String> storeAisles, int selectedAisleIndex) {
            mStoreAisles = storeAisles;
            mSelectedAisleIndex = selectedAisleIndex;
        }
    }
}
