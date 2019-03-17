package amuniz.com.honeygogroceryshoppping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import amuniz.com.honeygogroceryshoppping.model.PantryItem;
import amuniz.com.honeygogroceryshoppping.model.PantryItemLocation;
import amuniz.com.honeygogroceryshoppping.model.PantryItemQuantity;
import amuniz.com.honeygogroceryshoppping.model.ShoppingItem;
import amuniz.com.honeygogroceryshoppping.model.StoreLocation;

/**
 * Created by amuni on 4/1/2018.
 */

public class AddPantryItemFragment extends Fragment {

    private static final String ARG_PANTRY_TEMS = "existing_pantry_items";

    private static final String FILE_PROVIDER_AUTHORITY = "amuniz.com.honeygogroceryshoppping.fileprovider";

    private static final int REQUEST_PHOTO = 0;
    private static final int ADD_NEW_PANTRY_ITEM_LOCATION = 1;

    private boolean mSelelectByDefault;
    private String mName;
    private String mDescription;
    private List<StoreLocation> mStoreLocations;
    private PantryItemQuantity mDefaultQuantity;

//    private PantryItem mPantryItem;
    private EditText mNameField;
    private CheckBox mSelectByDefaultCheckbox;
    private EditText mDescriptionField;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private AddItemCallbacks mAddItemCallbacks;
    private EditText mQuantityField;
    private EditText mQuantityUnitField;
    private RecyclerView mLocationRecyclerView;
    private PantryItemLocationAdapter mAdapter;
    private ImageButton mAddLocation;
    private Button mOkButton;
    private Button mCancelButton;
    private LinearLayout mLocationGroup;
    private HashSet<String> mExistingPantryItemNames;
    private HashSet<Location> mAssignedLocations;


//    public interface Callbacks {
//        void onPantryItemUpdated(PantryItem pantryItem);
//    }

    public interface AddItemCallbacks {
        void onPantryItemAdded(PantryItem pantryItem, HashSet<Location> assignedLocations);
        void onCancelAdd();
    }

    public static AddPantryItemFragment newInstance(HashSet<String> sxistingPantryItemNames)
    {
        Bundle args = new Bundle();

        args.putSerializable(ARG_PANTRY_TEMS, sxistingPantryItemNames);

        AddPantryItemFragment fragment = new AddPantryItemFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public void updateUI()
    {
        List<StoreLocation> pantryItemLocations = new ArrayList<StoreLocation>();
        pantryItemLocations.addAll(mStoreLocations);

        if (mAdapter == null) {
            mAdapter = new PantryItemLocationAdapter(pantryItemLocations);
            mLocationRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setPantryItemLocations(pantryItemLocations);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelelectByDefault = false;
        mName = "";
        mDescription = "";
        mStoreLocations = new ArrayList<StoreLocation>();
        mDefaultQuantity = new PantryItemQuantity(1, "");

        mExistingPantryItemNames = (HashSet<String>)getArguments().getSerializable(ARG_PANTRY_TEMS);
        mAssignedLocations = new HashSet<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.fragment_pantry_item, container, false);

        mSelectByDefaultCheckbox = v.findViewById(R.id.pantry_item_selectbydefault);
        mSelectByDefaultCheckbox.setChecked(mSelelectByDefault);
        mSelectByDefaultCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               mSelelectByDefault = isChecked;
            }
        });

        mPhotoButton = (ImageButton)v.findViewById(R.id.pantry_item_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager packageManager = getActivity().getPackageManager();
        boolean canTakePhoto = mPhotoFile != null &&
            captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        FILE_PROVIDER_AUTHORITY, mPhotoFile);

                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.pantry_item_photo);
        updatePhotoView();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPhotoFile!= null && mPhotoFile.exists())
                {
                    FragmentManager fragmentManager = getFragmentManager();

                    PantryItemPhotoDialogFragment.newInstance(mName, mDescription, mPhotoFile).show(fragmentManager, "PantryItemPictureDialog");
                }

            }
        });

        mNameField = v.findViewById(R.id.pantry_item_name);
        mNameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        mOkButton = v.findViewById(R.id.ok_button);

        mOkButton.setOnClickListener(v1 -> {
            /* Validation code here */
            String itemNameEntered = mNameField.getText().toString().trim();
            if (itemNameEntered.length() == 0)
                mNameField.setError("The name of the item is required.");
            else if (mExistingPantryItemNames.contains(itemNameEntered)) {
                mNameField.setError(String.format("Store %s already exists.", itemNameEntered));
            }
            else{
                try {
                    addPantryItem(itemNameEntered, mDescriptionField.getText().toString(), mSelectByDefaultCheckbox.isChecked(),
                            Double.parseDouble(mQuantityField.getText().toString()),mQuantityUnitField.getText().toString());
                    // return to parent?
                }
                catch(Exception ex) {
                    mNameField.setError(String.format("Error adding pantry item %s.", itemNameEntered));
                }
            }
        });

        mCancelButton = v.findViewById(R.id.cancel_button);

        mCancelButton.setOnClickListener(v1 -> {
            /* Validation code here */
            String itemNameEntered = mNameField.getText().toString().trim();
            cancelAdd();
        });

        mLocationGroup = v.findViewById(R.id.location_group);
        mLocationGroup.setVisibility(View.GONE);

        mQuantityField = v.findViewById(R.id.pantry_item_quantity);
        mQuantityField.setText(String.format("%.2f", mDefaultQuantity.getQuantity()));
        mQuantityField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDefaultQuantity.setQuantity(Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mQuantityUnitField = v.findViewById(R.id.pantry_item_quantity_unit);
        mQuantityUnitField.setText(mDefaultQuantity.getUnit());
        mQuantityUnitField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDefaultQuantity.setUnit(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDescriptionField = v.findViewById(R.id.pantry_item_description);
        mDescriptionField.setText(mDescription);
        mDescriptionField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDescription = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDescriptionField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        return v;
    }

    private void cancelAdd() {
        mAddItemCallbacks.onCancelAdd();

    }
    private void addPantryItem(String name, String description, boolean isSelectByDefault, double defaultQuantity, String unit)
    {
        long pantryItemId = Pantry.get(getActivity()).addPantryItem(name, description, isSelectByDefault, defaultQuantity, unit);
        PantryItem pantryItem = new PantryItem(pantryItemId, name, description, unit, defaultQuantity, isSelectByDefault);
        mAddItemCallbacks.onPantryItemAdded(pantryItem, mAssignedLocations);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAddItemCallbacks = (AddItemCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddItemCallbacks = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    FILE_PROVIDER_AUTHORITY, mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        } else if (requestCode == ADD_NEW_PANTRY_ITEM_LOCATION) {
            mAssignedLocations.add((Location)data.getSerializableExtra(PantryItemLocationFragment.EXTRA_ITEM_LOCATION));
            // update UI With additional assigned location
 // update mExistingLocations?
            //
        }

    }
    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        }
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private StoreLocation mPantryItemLocation;
        private int mIndex;
        private TextView mLocationDescription;
        private ImageButton mRemoveLocation;

        public LocationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_location, parent, false));

            mLocationDescription = itemView.findViewById(R.id.location_description);
            mRemoveLocation = itemView.findViewById(R.id.remove_location);
        }

        @Override
        public void onClick(View v) {

        }

        public void bind(StoreLocation pantryItemLocation, int index)
        {
            mPantryItemLocation = pantryItemLocation;
            mIndex = index;
            mLocationDescription.setText(pantryItemLocation.toString());
            //itemView.setOnClickListener(this);
        }

    }

    private class PantryItemLocationAdapter extends RecyclerView.Adapter<AddPantryItemFragment.LocationHolder> {

        private List<StoreLocation> mPantryItemLocations;

        public PantryItemLocationAdapter(List<StoreLocation> pantryItemLocations) {
            setPantryItemLocations(pantryItemLocations);
        }

        @Override
        public AddPantryItemFragment.LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new AddPantryItemFragment.LocationHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(AddPantryItemFragment.LocationHolder holder, int position) {
            StoreLocation pantryItemLocation = getPantryItemLocations().get(position);
            holder.bind(pantryItemLocation, position);
        }

        @Override
        public int getItemCount() {
            return getPantryItemLocations().size();
        }

        public List<StoreLocation> getPantryItemLocations() {
            return mPantryItemLocations;
        }

        public void setPantryItemLocations(List<StoreLocation> storeLocations) {
            mPantryItemLocations = storeLocations;
        }
    }
}
