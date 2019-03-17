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

public class AddStoreSectionFragment extends Fragment {
    private static final String ARG_STORE_ID = "store_id";
    private static final String ARG_STORE_SECTIONS = "store_sections";
    private static final String EXTRA_ITEM_ADDED_STORE_SECTION = "com.amuniz.addstoreintent.added_store_section";

    private String mStoreSection;
    private long mStoreId;
    private EditText mSectionField;
    private AddStoreSectionCallbacks mAddStoreSectionCallbacks;
    private HashSet<String> mExistingStoreSections;
    private Button mOkButton;
    private Button mCancelButton;
    public interface AddStoreSectionCallbacks {
        void onStoreSectionAdded();
        void onCancelAdd();
    }

    public static AddStoreSectionFragment newInstance(long storeId, HashSet<String> storeSections)
    {
        Bundle args = new Bundle();

        args.putLong(ARG_STORE_ID, storeId);
        args.putSerializable(ARG_STORE_SECTIONS, storeSections);

        AddStoreSectionFragment fragment = new AddStoreSectionFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static String getAddedStoreSection(Intent data) {
        return data.getStringExtra(AddStoreSectionFragment.EXTRA_ITEM_ADDED_STORE_SECTION);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStoreId = getArguments().getLong(ARG_STORE_ID);
        mExistingStoreSections = (HashSet<String>) getArguments().getSerializable(ARG_STORE_SECTIONS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_store_grocery_section, container, false);

        mSectionField = v.findViewById(R.id.section);
        mOkButton = v.findViewById(R.id.ok_button);

        mOkButton.setOnClickListener(v1 -> {
            /* Validation code here */
            String sectionNameEntered = mSectionField.getText().toString().trim();
            if (sectionNameEntered.length() == 0)
                mSectionField.setError("The name of the aisle is required.");
            else if (mExistingStoreSections.contains(sectionNameEntered)) {
                mSectionField.setError(String.format("Store section %s already exists.", sectionNameEntered));
            }
            else{
                try {
                    addStoreSection(sectionNameEntered);
                }
                catch(Exception ex) {
                    mSectionField.setError(String.format("Error adding store section %s.", sectionNameEntered));
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
        mAddStoreSectionCallbacks.onCancelAdd();
//        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void addStoreSection(String section)
    {
        Pantry pantry = Pantry.get(getActivity());
        pantry.addStoreSection(mStoreId, section);
        mStoreSection = section;

        sendResult(section);
        mAddStoreSectionCallbacks.onStoreSectionAdded();
    }

    private void sendResult(String storeSection) {
        Intent intent = new Intent();

        if ((storeSection == null) || storeSection.trim().length() == 0) {
            getActivity().setResult(Activity.RESULT_CANCELED, intent);
            return;
        }
        intent.putExtra(EXTRA_ITEM_ADDED_STORE_SECTION, storeSection);
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void  onAttach(Context context) {
        super.onAttach(context);
        mAddStoreSectionCallbacks = (AddStoreSectionFragment.AddStoreSectionCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddStoreSectionCallbacks = null;
    }

}
