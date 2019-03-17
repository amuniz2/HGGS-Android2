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

public class EditStoreSectionFragment extends Fragment {

    private static final String ARG_STORE_ID = "store_id";
    private static final String ARG_STORE_SECTIONS = "store_sections";
    private static final String ARG_SECTION = "store_section";
    private long mStoreId;
    private String mSection;
    private HashSet<String> mExistingStoreSecions;
    private EditText mSectionField;
    private Button mOkButton;
    private Button mDeleteButton;
    private Button mCancelButton;
    private EditStoreSectionCallbacks mEditStoreSectionCallbacks;

    public interface EditStoreSectionCallbacks {
        void onStoreSectionUpdated();
        void onStoreSectionDeleted();
        void onEditStoreSectionCancelled();
    }

    public static EditStoreSectionFragment newInstance(Long storeId, String aisle, HashSet<String> aisles)
    {
        Bundle args = new Bundle();

        args.putLong(ARG_STORE_ID, storeId);
        args.putString(ARG_SECTION, aisle);
        args.putSerializable(ARG_STORE_SECTIONS, aisles);
        EditStoreSectionFragment fragment = new EditStoreSectionFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExistingStoreSecions = (HashSet<String>)getArguments().getSerializable(ARG_STORE_SECTIONS);
        mStoreId = getArguments().getLong(ARG_STORE_ID);
        mSection = getArguments().getString(ARG_SECTION);
        mExistingStoreSecions.remove(mSection);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_store_grocery_section, container, false);

        mSectionField = v.findViewById(R.id.section);

        mSectionField.setText(mSection);

        mSectionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().equals(mSection)) {
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
            String sectionEntered = mSectionField.getText().toString().trim();
            if (sectionEntered.length() == 0)
                mSectionField.setError("The name of the section is required.");
            else if (mExistingStoreSecions.contains(sectionEntered)) {
                mSectionField.setError(String.format("Section %s already exists.", sectionEntered));
            }
            else{
                try {
                    updateStoreSection(sectionEntered);
                }
                catch(Exception ex) {
                    mSectionField.setError(String.format("Error updating section %s.", sectionEntered));
                }
            }
        });
        mOkButton.setEnabled(false);


        mDeleteButton = v.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(v1 -> {
            String sectionEntered = mSectionField.getText().toString().trim();
                try {
                    deleteStoreSection();
                }
                catch(Exception ex) {
                    mSectionField.setError(String.format("Error deleting store section %s.", sectionEntered));
                }
        });
        mDeleteButton.setEnabled(true);

        mCancelButton = v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(v1 -> {
            String sectionEntered = mSectionField.getText().toString().trim();
            try {
                cancelEdit();
            }
            catch(Exception ex) {
                mSectionField.setError(String.format("Error deleting store section %s.", sectionEntered));
            }
        });
        mCancelButton.setEnabled(false);

        return v;

    }

    private void cancelEdit() {
        mEditStoreSectionCallbacks.onEditStoreSectionCancelled();
    }
    private void updateStoreSection(String newSectionName)
    {
        Pantry.get(getActivity()).updateStoreSection(mStoreId, mSection, newSectionName);
        mEditStoreSectionCallbacks.onStoreSectionUpdated();
    }
    private void deleteStoreSection()
    {
        Pantry pantry = Pantry.get(getActivity());
        if (pantry.storeSectionInUse(mStoreId, mSection)) {
            showStoreSectionInUseError();
        } else {
            confirmDelete();
        }
    }

    @Override
    public void  onAttach(Context context) {
        super.onAttach(context);
        mEditStoreSectionCallbacks = (EditStoreSectionFragment.EditStoreSectionCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEditStoreSectionCallbacks = null;
    }

    private void confirmDelete()
    {
        String storeName = Pantry.get(getActivity()).getStore(mStoreId).getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.conform_delete_store_section, mSection, storeName));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Pantry.get(getActivity()).deleteStoreGrocerySection(mStoreId, mSection);
                mEditStoreSectionCallbacks.onStoreSectionDeleted();
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

    private void showStoreSectionInUseError()
    {
        String storeName = Pantry.get(getActivity()).getStore(mStoreId).getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.store_section_in_use, mSection, storeName));
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }
}
