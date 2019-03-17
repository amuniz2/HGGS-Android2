package amuniz.com.honeygogroceryshoppping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashSet;

import amuniz.com.honeygogroceryshoppping.model.Store;

public class AddStoreFragment extends Fragment {
    private static final String ARG_STORES = "stores";
    private static final String EXTRA_ITEM_ADDED_STORE_ID = "com.amuniz.addstoreintent.added_store_id";

    private Store mStore;
    private EditText mStoreNameField;
    private AddStoreCallbacks mAddStoreCallbacks;
    private HashSet<String> mExistingStoreNames;
    private Button mOkButton;
    private Button mCancelButton;
    public interface AddStoreCallbacks {
        void onStoreAdded(Store store);
        void onCancelAdd();
    }

    public static AddStoreFragment newInstance(HashSet<String> stores)
    {
        Bundle args = new Bundle();

        args.putSerializable(ARG_STORES, stores);

        AddStoreFragment fragment = new AddStoreFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static Long getAddedStoreId(Intent data) {
        return data.getLongExtra(AddStoreFragment.EXTRA_ITEM_ADDED_STORE_ID, -1);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExistingStoreNames = (HashSet<String>) getArguments().getSerializable(ARG_STORES);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_store, container, false);
        mOkButton = v.findViewById(R.id.ok_button);
        mOkButton.setEnabled(false);
        mStoreNameField = v.findViewById(R.id.store_name);
        mStoreNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {
                    mOkButton.setEnabled(true);
                    mCancelButton.setEnabled(true);
                } else {
                    mOkButton.setEnabled(false);
                    mCancelButton.setEnabled(false);
                }
            }
        });


//        mStoreNameField.setText("Enter store name");

        mOkButton.setOnClickListener(v1 -> {
            /* Validation code here */
            String storeNameEntered = mStoreNameField.getText().toString().trim();
            if (storeNameEntered.length() == 0)
                mStoreNameField.setError("The name of the store is required.");
            else if (mExistingStoreNames.contains(storeNameEntered)) {
                mStoreNameField.setError(String.format("Store %s already exists.", storeNameEntered));
            }
            else{
                try {
                    addStore(storeNameEntered);
                }
                catch(Exception ex) {
                    mStoreNameField.setError(String.format("Error addingStore %s.", storeNameEntered));
                }
            }
        });
        v.findViewById(R.id.delete_button).setVisibility(View.GONE);
        mCancelButton = v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(v1 -> {
            cancelAdd();
        });
        mCancelButton.setEnabled(true);

        return v;
    }
    private void cancelAdd() {
        mAddStoreCallbacks.onCancelAdd();
    }

    private void sendResult(Long storeId, String storeName) {
        Intent intent = new Intent();

        if (storeId == null) {
            // no valid store was added - cancel
            getActivity().setResult(Activity.RESULT_CANCELED, intent);
            return;
        }

        intent.putExtra(EXTRA_ITEM_ADDED_STORE_ID, storeId);
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    private void addStore(String storeName)
    {
        Pantry pantry = Pantry.get(getActivity());
        long storeId = pantry.addStore(storeName);
        // mStore = new Store(storeId, storeName);

        sendResult(storeId, storeName);
        mAddStoreCallbacks.onStoreAdded(mStore);
    }

    @Override
    public void  onAttach(Context context) {
        super.onAttach(context);
        mAddStoreCallbacks = (AddStoreFragment.AddStoreCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddStoreCallbacks = null;
    }

}
