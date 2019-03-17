package amuniz.com.honeygogroceryshoppping;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class EditStoreAisleFragment extends Fragment {

    private static final String ARG_STORE_ID = "store_id";
    private static final String ARG_STORE_AISLES = "store_aisles";
    private static final String ARG_AISLE= "store_aisle";
    private long mStoreId;
    private String mAisle;
    private HashSet<String> mExistingStoreAisles;
    private EditText mAisleField;
    private Button mOkButton;
    private Button mDeleteButton;
    private Button mCancelButton;
    private EditStoreAisleCallbacks mEditStoreAisleCallbacks;

    public interface EditStoreAisleCallbacks {
        void onStoreAisleUpdated();
        void onStoreAisleDeleted();
        void onEditStoreAisleCancelled();
    }

    public static EditStoreAisleFragment newInstance(Long storeId,  String aisle, HashSet<String> aisles)
    {
        Bundle args = new Bundle();

        args.putLong(ARG_STORE_ID, storeId);
        args.putString(ARG_AISLE, aisle);
        args.putSerializable(ARG_STORE_AISLES, aisles);
        EditStoreAisleFragment fragment = new EditStoreAisleFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Long storeId = getArguments().getLong(ARG_STORE_ID);
        mExistingStoreAisles = (HashSet<String>)getArguments().getSerializable(ARG_STORE_AISLES);
        mStoreId = getArguments().getLong(ARG_STORE_ID);
        mAisle = getArguments().getString(ARG_AISLE);
        mExistingStoreAisles.remove(mAisle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_store_grocery_aisle, container, false);

        mAisleField = v.findViewById(R.id.aisle);

        mAisleField.setText(mAisle);

        mAisleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().equals(mAisle)) {
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
            String aisleEntered = mAisleField.getText().toString().trim();
            if (aisleEntered.length() == 0)
                mAisleField.setError("The name of the store is required.");
            else if (mExistingStoreAisles.contains(aisleEntered)) {
                mAisleField.setError(String.format("Aisle %s already exists.", aisleEntered));
            }
            else{
                try {
                    updateStoreAisle(aisleEntered);
                }
                catch(Exception ex) {
                    mAisleField.setError(String.format("Error updating aisle %s.", aisleEntered));
                }
            }
        });
        mOkButton.setEnabled(false);


        mDeleteButton = v.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(v1 -> {
            String aisleEntered = mAisleField.getText().toString().trim();
                try {
                    deleteStoreAisle();
                }
                catch(Exception ex) {
                    mAisleField.setError(String.format("Error deleting store aisle %s.", aisleEntered));
                }
        });
        mDeleteButton.setEnabled(true);

        mCancelButton = v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(v1 -> {
            String aisleEntered = mAisleField.getText().toString().trim();
            try {
                cancelEdit();
            }
            catch(Exception ex) {
                mAisleField.setError(String.format("Error deleting store aisle %s.", aisleEntered));
            }
        });
        mCancelButton.setEnabled(true);

        return v;

    }

    private void cancelEdit() {
        mEditStoreAisleCallbacks.onEditStoreAisleCancelled();
    }
    private void updateStoreAisle(String newAisleName)
    {
        Pantry.get(getActivity()).updateStoreAisle(mStoreId, mAisle, newAisleName);
        mEditStoreAisleCallbacks.onStoreAisleUpdated();
    }
    private void deleteStoreAisle()
    {
        Pantry pantry = Pantry.get(getActivity());
        if (pantry.storeAisleInUse(mStoreId, mAisle)) {
            showStoreAisleInUseError();
        } else {
            confirmDelete();
        }
    }

    @Override
    public void  onAttach(Context context) {
        super.onAttach(context);
        mEditStoreAisleCallbacks = (EditStoreAisleFragment.EditStoreAisleCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEditStoreAisleCallbacks = null;
    }

    private void confirmDelete()
    {
        String storeName = Pantry.get(getActivity()).getStore(mStoreId).getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //PantryItem pantryItem = Pantry.get(this).getPantryItem(mId);
        builder.setMessage(getString(R.string.conform_delete_store_aisle, mAisle, storeName));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Pantry.get(getActivity()).deleteStoreAisle(mStoreId, mAisle);
                mEditStoreAisleCallbacks.onStoreAisleDeleted();
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

    private void showStoreAisleInUseError()
    {
        String storeName = Pantry.get(getActivity()).getStore(mStoreId).getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.store_aisle_in_use, mAisle, storeName));
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }
}
