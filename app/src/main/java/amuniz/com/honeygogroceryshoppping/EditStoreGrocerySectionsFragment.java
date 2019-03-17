package amuniz.com.honeygogroceryshoppping;

import android.content.Context;
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

public class EditStoreGrocerySectionsFragment extends Fragment {

    private static final String ARG_STORE_ID = "store_id";
    private static final String ARG_STORE_SECTION = "store_section";

    private String mSection;
    private EditText mSectionField;
    private long mStoreId;
    private int mSelectedSectionPosition;

    private RecyclerView mStoreSectionsRecyclerView;
    private EditStoreGrocerySectionsFragment.StoreSectionsAdapter mAdapter;
    private EditStoreSectionsCallbacks mEditStoreSectionsCallbacks;

    public interface EditStoreSectionsCallbacks {
        void onCreateNewStoreSection(long storeId);
        void onStoreSectionSelected(String storeSection, int index);
    }

    public static EditStoreGrocerySectionsFragment newInstance(Long storeId, String section)
    {
        Bundle args = new Bundle();

        args.putLong(ARG_STORE_ID, storeId);
        args.putString(ARG_STORE_SECTION, section);

        EditStoreGrocerySectionsFragment fragment = new EditStoreGrocerySectionsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_edit_store_section_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.new_store_section:
                try {
                    mEditStoreSectionsCallbacks.onCreateNewStoreSection(mStoreId);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStoreId = getArguments().getLong(ARG_STORE_ID);
        mSection = getArguments().getString(ARG_STORE_SECTION);
        mSelectedSectionPosition = -1;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_store_grocery_sections_list, container, false);

        mStoreSectionsRecyclerView = v.findViewById(R.id.store_sections_recycler_view);
        mStoreSectionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setHasOptionsMenu(true);
        updateUI();

        return v;
    }

    public void updateUI()
    {
        Pantry pantry = Pantry.get(getActivity());
        List<String> storeSections = pantry.getStoreGrocerySections(mStoreId);

        if (mAdapter == null) {
            mAdapter = new EditStoreGrocerySectionsFragment.StoreSectionsAdapter(storeSections, mSelectedSectionPosition);
            mStoreSectionsRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setStoreSections(storeSections, mSelectedSectionPosition);
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
        mEditStoreSectionsCallbacks = (EditStoreGrocerySectionsFragment.EditStoreSectionsCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEditStoreSectionsCallbacks = null;
    }

    public void setSelectedSectionPosition(int position) {
        mSelectedSectionPosition = position;
    }

    private class StoreSectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private String mSection;
        private TextView mName;
        private int mIndex;

        public StoreSectionHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_section, parent, false));

            mName = itemView.findViewById(R.id.store_section);
        }

        public void bind(String storeSection, int index, boolean selected)
        {;
            mIndex = index;
            mSection = storeSection;
            mName.setText(storeSection);

            if (selected) {
                itemView.setBackgroundColor(CYAN);
            } else {
                itemView.setBackgroundColor(WHITE);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            mEditStoreSectionsCallbacks.onStoreSectionSelected(mSection, mIndex);
        }

    }

    private class StoreSectionsAdapter extends RecyclerView.Adapter<StoreSectionHolder> {

        private List<String> mStoreSections;
        private int mSelectedSectionIndex = -1;

        public StoreSectionsAdapter(List<String> storeSections, int selectedStoreAisleIndex)
        {
            mSelectedSectionIndex = selectedStoreAisleIndex;
            setStoreSections(storeSections, selectedStoreAisleIndex);
        }

        @Override
        public EditStoreGrocerySectionsFragment.StoreSectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new EditStoreGrocerySectionsFragment.StoreSectionHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(EditStoreGrocerySectionsFragment.StoreSectionHolder holder, int position) {
            String storeSection = getStoreSections().get(position);
            holder.bind(storeSection, position, position == mSelectedSectionPosition);
        }

        @Override
        public int getItemCount() {
            return getStoreSections().size();
        }

        public List<String> getStoreSections() {
            return mStoreSections;
        }

        public void setStoreSections(List<String> storeSections, int selectedSectionIndex) {
            mStoreSections = storeSections;
            mSelectedSectionIndex = selectedSectionIndex;
        }
    }
}
