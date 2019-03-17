package amuniz.com.honeygogroceryshoppping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashSet;

public class AddStoreAisleFragment extends Fragment {
    private static final String ARG_STORE_ID = "store_id";
    private static final String ARG_STORE_AISLES = "store_aisles";
    private static final String EXTRA_ITEM_ADDED_STORE_AISLE = "com.amuniz.addstoreintent.added_store_aisle";

    private String mStoreAisle;
    private long mStoreId;
    private EditText mAisleField;
    private AddStoreAisleCallbacks mAddStoreAisleCallbacks;
    private HashSet<String> mExistingStoreAisles;
    private Button mOkButton;
    private Button mCancelButton;
    public interface AddStoreAisleCallbacks {
        void onStoreAisleAdded();
        void onCancelAdd();
    }

    public static AddStoreAisleFragment newInstance(long storeId, HashSet<String> storeAisles)
    {
        Bundle args = new Bundle();

        args.putLong(ARG_STORE_ID, storeId);
        args.putSerializable(ARG_STORE_AISLES, storeAisles);

        AddStoreAisleFragment fragment = new AddStoreAisleFragment();
        fragment.setArguments(args);

        return fragment;
    }
    public static String getAddedStoreAisle(Intent data) {
        return data.getStringExtra(AddStoreAisleFragment.EXTRA_ITEM_ADDED_STORE_AISLE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStoreId = getArguments().getLong(ARG_STORE_ID);
        mExistingStoreAisles = (HashSet<String>) getArguments().getSerializable(ARG_STORE_AISLES);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_store_grocery_aisle, container, false);

        mAisleField = v.findViewById(R.id.aisle);
        mOkButton = v.findViewById(R.id.ok_button);

//        mStoreNameField.setText("Enter store name");

        mOkButton.setOnClickListener(v1 -> {
            /* Validation code here */
            String aisleNameEntered = mAisleField.getText().toString().trim();
            if (aisleNameEntered.length() == 0)
                mAisleField.setError("The name of the aisle is required.");
            else if (mExistingStoreAisles.contains(aisleNameEntered)) {
                mAisleField.setError(String.format("Store aisle %s already exists.", aisleNameEntered));
            }
            else{
                try {
                    addStoreAisle(aisleNameEntered);
                }
                catch(Exception ex) {
                    mAisleField.setError(String.format("Error adding store aisle %s.", aisleNameEntered));
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
        mAddStoreAisleCallbacks.onCancelAdd();
//        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void addStoreAisle(String aisle)
    {
        Pantry pantry = Pantry.get(getActivity());
        pantry.addStoreAisle(mStoreId, aisle);
        mStoreAisle = aisle;

        sendResult(aisle);
        mAddStoreAisleCallbacks.onStoreAisleAdded();
    }

    private void sendResult(String aisle) {
        Intent intent = new Intent();

        if ((aisle == null) || aisle.trim().length() == 0) {
            getActivity().setResult(Activity.RESULT_CANCELED, intent);
            return;
        }
        intent.putExtra(EXTRA_ITEM_ADDED_STORE_AISLE, aisle);
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void  onAttach(Context context) {
        super.onAttach(context);
        mAddStoreAisleCallbacks = (AddStoreAisleFragment.AddStoreAisleCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddStoreAisleCallbacks = null;
    }

}
