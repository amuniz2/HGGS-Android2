package amuniz.com.honeygogroceryshoppping;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashSet;

import amuniz.com.honeygogroceryshoppping.model.Store;

public class EditStoreFragment extends Fragment {

    private static final String ARG_STORE_ID = "store_id";
    private static final String ARG_STORES = "stores";
    private static final String ARG_STORE_POSITION = "store_position";
    private Store mStore;
    private HashSet<String> mExistingStoreNames;
//    private String mStoreName;
//    private String mOriginalStoreName;
    private EditText mStoreNameField;
    private Button mOkButton;
    private Button mDeleteButton;
    private Button mCancelButton;
    private EditStoreCallbacks mEditStoreCallbacks;

    public interface EditStoreCallbacks {
        void onStoreUpdated(Store store);
        void onStoreDeleted(Store store);
        void onEditStoreCancelled(Store store);
    }

    public static EditStoreFragment newInstance(Long storeId, HashSet<String> stores)
    {
        Bundle args = new Bundle();

        args.putLong(ARG_STORE_ID, storeId);
        args.putSerializable(ARG_STORES, stores);
        EditStoreFragment fragment = new EditStoreFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Long storeId = getArguments().getLong(ARG_STORE_ID);
        mExistingStoreNames = (HashSet<String>)getArguments().getSerializable(ARG_STORES);
        mStore = Pantry.get(getActivity()).getStore(storeId);
        mExistingStoreNames.remove(mStore.getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_store, container, false);

        mStoreNameField = v.findViewById(R.id.store_name);

        mStoreNameField.setText(mStore.getName());

        mStoreNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().equals(mStore.getName())) {
                    mOkButton.setEnabled(true);
                    mCancelButton.setEnabled(true);
                } else {
                    mOkButton.setEnabled(false);
                    mCancelButton.setEnabled(false);
                }
            }
        });
        mOkButton = v.findViewById(R.id.ok_button);

        mOkButton.setOnClickListener(v1 -> {
            String storeNameEntered = mStoreNameField.getText().toString().trim();
            if (storeNameEntered.length() == 0)
                mStoreNameField.setError("The name of the store is required.");
            else if (mExistingStoreNames.contains(storeNameEntered)) {
                mStoreNameField.setError(String.format("Store %s already exists.", storeNameEntered));
            }
            else{
                try {
                    mStore.setName(storeNameEntered);
                    updateStore();
                    mOkButton.setEnabled(false);
                    mCancelButton.setEnabled(false);
                }
                catch(Exception ex) {
                    mStoreNameField.setError(String.format("Error updating store %s.", storeNameEntered));
                }
            }
        });
        mOkButton.setEnabled(false);


        mDeleteButton = v.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(v1 -> {
            String storeNameEntered = mStoreNameField.getText().toString().trim();
                try {
                    deleteStore();
                }
                catch(Exception ex) {
                    mStoreNameField.setError(String.format("Error deleting store %s.", storeNameEntered));
                }
        });
        mDeleteButton.setEnabled(true);

        mCancelButton = v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(v1 -> {
            String storeNameEntered = mStoreNameField.getText().toString().trim();
            try {
                cancelEdit();
//                deleteStore();
            }
            catch(Exception ex) {
                mStoreNameField.setError(String.format("Error deleting store %s.", storeNameEntered));
            }
        });
        mCancelButton.setEnabled(false);

        return v;

    }

    private void cancelEdit() {
//        mStoreNameField.setText(mStore.getName());
//        mCancelButton.setEnabled(false);
//        mOkButton.setEnabled(false);
        mEditStoreCallbacks.onEditStoreCancelled(mStore);
    }
    private void updateStore()
    {
        Pantry.get(getActivity()).updateStore(mStore);
        mEditStoreCallbacks.onStoreUpdated(mStore);
    }
    private void deleteStore()
    {
        Pantry pantry = Pantry.get(getActivity());
        if (pantry.storeInUse(mStore.getId())) {
            showStoreInUseError();
        } else {
            // for now, do not allow store to be deleted - may prompt for a cascade delete later
            confirmDelete();
        }
    }

    @Override
    public void  onAttach(Context context) {
        super.onAttach(context);
        mEditStoreCallbacks = (EditStoreFragment.EditStoreCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEditStoreCallbacks = null;
    }

    private void confirmDelete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //PantryItem pantryItem = Pantry.get(this).getPantryItem(mId);
        builder.setMessage(getString(R.string.conform_delete_store, mStore.getName()));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Pantry.get(getActivity()).deleteStore(mStore.getId());
                mEditStoreCallbacks.onStoreDeleted(mStore);
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

    private void showStoreInUseError()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //PantryItem pantryItem = Pantry.get(this).getPantryItem(mId);
        builder.setMessage(getString(R.string.store_in_use, mStore.getName()));
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }
}
