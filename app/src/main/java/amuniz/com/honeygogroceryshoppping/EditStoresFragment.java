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
import android.widget.TextView;

import java.util.List;

import amuniz.com.honeygogroceryshoppping.model.Store;

import static android.graphics.Color.CYAN;
import static android.graphics.Color.WHITE;

public class EditStoresFragment extends Fragment {

    private static final String ARG_STORE_LIST = "store_list";

    private RecyclerView mStoreRecyclerView;
    private EditStoresFragment.StoreAdapter mAdapter;
    private EditStoresCallbacks mEditStoresCallbacks;
    private int mSelectedStorePosition;

    public interface EditStoresCallbacks {
        void onStoreSelected(Store store, int position);
        void onCreateNewStore();
    }

    public static EditStoresFragment newInstance() {

        EditStoresFragment fragment = new EditStoresFragment();
        return fragment;
    }

    public void setSelectedStorePosition(int position) {
        mSelectedStorePosition = position;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_pantry_list, container, false);

        mStoreRecyclerView = v.findViewById(R.id.pantry_item_recycler_view);
        mStoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setHasOptionsMenu(true);
        updateUI();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mSelectedStorePosition = getArguments().getInt(ARG_SELECTED_STORE_POSITION);
            mSelectedStorePosition = -1;
    }
    @Override
    public void onResume()
    {
        super.onResume();

        updateUI();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mEditStoresCallbacks = (EditStoresFragment.EditStoresCallbacks)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEditStoresCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_edit_store_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.new_store:
                try {
                    mEditStoresCallbacks.onCreateNewStore();
                }
                catch (Exception ex) {
                    DialogUtils.showErrorMessage(getContext(), "Error adding store.");
                }
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void updateUI()
    {
        Pantry pantry = Pantry.get(getActivity());
        List<Store> stores = pantry.getStores();

        if (mAdapter == null) {
            mAdapter = new EditStoresFragment.StoreAdapter(stores, mSelectedStorePosition);
            mStoreRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setStores(stores, mSelectedStorePosition);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class StoreHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Store mStore;
        private TextView mName;
        private int mIndex;

        public StoreHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_store, parent, false));

            mName = itemView.findViewById(R.id.store_name);
        }

        public void bind(Store store, int index, boolean selected)
        {
            mStore = store;
            mIndex = index;
            mName.setText(store.getName());

            if (selected) {
                itemView.setBackgroundColor(CYAN);
            } else {
                itemView.setBackgroundColor(WHITE);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            mEditStoresCallbacks.onStoreSelected(mStore, mIndex);
        }

    }


    private class StoreAdapter extends RecyclerView.Adapter<StoreHolder> {

        private List<Store> mStores;
        private int mSelectedStoreIndex = -1;

        public StoreAdapter(List<Store> stores, int selectedStoreIndex)
        {
            mSelectedStoreIndex = selectedStoreIndex;
            setStores(stores, selectedStoreIndex);
        }

        @Override
        public StoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new StoreHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(StoreHolder holder, int position) {
            Store store = getStores().get(position);
            holder.bind(store, position, position == mSelectedStoreIndex);
        }

        @Override
        public int getItemCount() {
            return getStores().size();
        }

        public List<Store> getStores() {
            return mStores;
        }

        public void setStores(List<Store> stores, int selectedStoreIndex) {
            mStores = stores;
            mSelectedStoreIndex = selectedStoreIndex;
        }
    }
}
