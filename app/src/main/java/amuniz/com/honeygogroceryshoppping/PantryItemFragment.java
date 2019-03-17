package amuniz.com.honeygogroceryshoppping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import amuniz.com.honeygogroceryshoppping.model.StoreLocation;
import amuniz.com.honeygogroceryshoppping.model.PantryItem;
import amuniz.com.honeygogroceryshoppping.model.PantryItemLocation;

/**
 * Created by amuni on 4/1/2018.
 */

public class PantryItemFragment extends Fragment {

    private static final String ARG_PANTRY_ITEM_ID = "pantry_item_id";
    private static final String FILE_PROVIDER_AUTHORITY = "amuniz.com.honeygogroceryshoppping.fileprovider";

    private static class PantryItemActions {
        public static int REQUEST_PHOTO = 0;
        public static int EDIT_PANTRY_ITEM_LOCATION = 2;
        public static int ADD_NEW_PANTRY_ITEM_LOCATION = 1;
    }
//    private static final int REQUEST_PHOTO = 0;
//    private static final int EDIT_PANTRY_ITEM_LOCATION = 2;
//    private static final int ADD_NEW_PANTRY_ITEM_LOCATION = 1;

    private PantryItem mPantryItem;
    private EditText mNameField;
    private CheckBox mSelectByDefaultCheckbox;
    private EditText mDescriptionField;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private EditPantryItemCallbacks mEditItemCallbacks;
    private EditText mQuantityField;
    private EditText mQuantityUnitField;
    private RecyclerView mLocationRecyclerView;
    private PantryItemLocationAdapter mAdapter;
    private ImageButton mAddLocation;
    private List<StoreLocation> mPantryItemLocations;
    private Button mOkButton;
    private Button mCancelButton;
    private Pantry mPantry;

    public interface EditPantryItemCallbacks {
        void onPantryItemUpdated(PantryItem pantryItem);
    }

    public static PantryItemFragment newInstance(Long pantryItemId)
    {
        Bundle args = new Bundle();

        args.putSerializable(ARG_PANTRY_ITEM_ID, pantryItemId);

        PantryItemFragment fragment = new PantryItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updatePantryItem()
    {
        Pantry.get(getActivity()).updatePantryItem(mPantryItem);
        mEditItemCallbacks.onPantryItemUpdated(mPantryItem);
    }

    public void updateUI()
    {
        mPantryItemLocations.clear();
        mPantryItemLocations.addAll(Pantry.get(getActivity()).getPantryItemStoreLocations(mPantryItem.getId()));
//        List<StoreLocation> pantryItemLocations = new ArrayList<>();
//        pantryItemLocations.addAll(mPantryItem.getLocations());

        if (mAdapter == null) {
            mAdapter = new PantryItemLocationAdapter(mPantryItemLocations);
            mLocationRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setPantryItemLocations(mPantryItemLocations);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPantry = Pantry.get(getActivity());
        mPantryItemLocations = new ArrayList<>();
        try {
            Long pantryItemId = getArguments().getLong(ARG_PANTRY_ITEM_ID);

            mPantryItem = mPantry.getPantryItem(pantryItemId);
            mPhotoFile = mPantry.getPhotoFile(mPantryItem);
        }
        catch (Exception ex) {
            DialogUtils.showErrorMessage(getActivity(), String.format("Error retrieving information for pantry item: %s.", ex.getMessage()));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.fragment_pantry_item, container, false);

        mSelectByDefaultCheckbox = v.findViewById(R.id.pantry_item_selectbydefault);
        mSelectByDefaultCheckbox.setChecked(mPantryItem.isSelectByDefault());
        mSelectByDefaultCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPantryItem.setSelectByDefault(isChecked);
                updatePantryItem();
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

                startActivityForResult(captureImage, PantryItemActions.REQUEST_PHOTO);
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

                    PantryItemPhotoDialogFragment.newInstance(mPantryItem.getName(), mPantryItem.getDescription(), mPhotoFile).show(fragmentManager, "PantryItemPictureDialog");
                }

            }
        });

        mNameField = v.findViewById(R.id.pantry_item_name);
        mNameField.setText(mPantryItem.getName());
        mNameField.addTextChangedListener(new TextValidator(mNameField) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                if (text.length() == 0)
                    textView.setError("The name of the pantry item is required.");
                else {
                    mPantryItem.setName(text);
                    try {
                        updatePantryItem();
                    }
                    catch(Exception ex) {
                        textView.setError(String.format("Item %s already exists.", text));
                    }
                }
            }
        });
        mNameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        mAddLocation = v.findViewById(R.id.add_location_button);
        mAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addItemLocation();
//                PantryItemLocation newLocation = new PantryItemLocation();
//                newLocation.setPantryItem(mPantryItem);
//
//                // todo: create activity for adding/selecting a location
//                Intent addNewPantryItemLocationIntent = PantryItemLocationActivity.newIntent(getActivity(), null, mPantryItem.getId());
//                try {
//                    startActivityForResult(addNewPantryItemLocationIntent, ADD_NEW_PANTRY_ITEM_LOCATION);
//                }
//                catch (Exception ex) {
//                    DialogUtils.showErrorMessage(getActivity(), String.format("Error adding location: %s", ex.getMessage()));
//                }
            }
        });
/*
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = s.toString();
                if (name.trim().length() == 0) {
                    Toast.makeText(getActivity(), R.string.pantry_item_name_required, Toast.LENGTH_LONG);
                }
                else {
                    mPantryItem.setName(s.toString());
                    updatePantryItem();
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String value = s.toString();
                if (value.trim().length() == 0) {
                    Toast.makeText(getActivity(), R.string.pantry_item_name_required, Toast.LENGTH_LONG);
                }
                else {
                    mPantryItem.setName(s.toString());
                    updatePantryItem();
                }
            }
        });
        */

        mQuantityField = v.findViewById(R.id.pantry_item_quantity);
        mQuantityField.setText(String.format("%.2f", mPantryItem.getDefaultQuantity().getQuantity()));
        mQuantityField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPantryItem.getDefaultQuantity().setQuantity(Double.parseDouble(s.toString()));
                updatePantryItem();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mQuantityUnitField = v.findViewById(R.id.pantry_item_quantity_unit);
        mQuantityUnitField.setText(mPantryItem.getDefaultQuantity().getUnit());
        mQuantityUnitField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPantryItem.getDefaultQuantity().setUnit(s.toString());
                updatePantryItem();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDescriptionField = v.findViewById(R.id.pantry_item_description);
        mDescriptionField.setText(mPantryItem.getDescription());
        mDescriptionField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPantryItem.setDescription(s.toString());
                updatePantryItem();
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

        mOkButton = v.findViewById(R.id.ok_button);
        mOkButton.setVisibility(View.GONE);

//        mOkButton.setOnClickListener(v1 -> {
//            /* Validation code here */
//            String itemNameEntered = mNameField.getText().toString().trim();
//            if (itemNameEntered.length() == 0)
//                mNameField.setError("The name of the item is required.");
//            else if (mExistingPantryItemNames.contains(itemNameEntered)) {
//                mNameField.setError(String.format("Store %s already exists.", itemNameEntered));
//            }
//            else{
//                try {
//                    updatePantryItem(itemNameEntered, mDescriptionField.getText().toString(), mSelectByDefaultCheckbox.isChecked(),
//                            Double.parseDouble(mQuantityField.getText().toString()),mQuantityUnitField.getText().toString());
//                    // return to parent?
//                }
//                catch(Exception ex) {
//                    mNameField.setError(String.format("Error adding pantry item %s.", itemNameEntered));
//                }
//            }
//        });

        mCancelButton = v.findViewById(R.id.cancel_button);
        mCancelButton.setVisibility(View.GONE);
//        mCancelButton.setOnClickListener(v1 -> {
//            /* Validation code here */
//            String itemNameEntered = mNameField.getText().toString().trim();
//            cancelAdd();
//        });

        mLocationRecyclerView = v.findViewById(R.id.location_recycler_view);
        mLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return v;
    }
    private void addItemLocation() {
        Intent intent = PantryItemLocationActivity.newIntent(getContext(), null, mPantryItem.getId());
        startActivityForResult(intent, PantryItemActions.ADD_NEW_PANTRY_ITEM_LOCATION);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mEditItemCallbacks = (EditPantryItemCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEditItemCallbacks = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        Pantry.get(getActivity()).updatePantryItem(mPantryItem);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == PantryItemActions.REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    FILE_PROVIDER_AUTHORITY, mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePantryItem();
            updatePhotoView();
        } else if (requestCode == PantryItemActions.ADD_NEW_PANTRY_ITEM_LOCATION) {
//            StoreLocation newLocation = (StoreLocation) data.getSerializableExtra(PantryItemLocationFragment.EXTRA_ITEM_LOCATION);
            updateUI();
        } else if ( requestCode == PantryItemActions.EDIT_PANTRY_ITEM_LOCATION) {
            updateUI();
        }
    }
    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }

    private void updatePhotoView() {
        if (mPhotoView == null || !mPhotoFile.exists()) {
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
        private Long mPantryItemId;
        private TextView mLocationDescription;
        private ImageButton mRemoveLocation;

        public LocationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_location, parent, false));

            mLocationDescription = itemView.findViewById(R.id.location_description);
            mRemoveLocation = itemView.findViewById(R.id.remove_location);
            mRemoveLocation.setOnClickListener(v1 -> {
                Pantry.get(getActivity()).deletePantryItemLocation(mPantryItemId, mPantryItemLocation.getId());
                updateUI();
                //mAdapter.notifyDataSetChanged();
            });

        }
        @Override
        public void onClick(View v) {
            Intent intent = PantryItemLocationActivity.newIntent(getContext(), mPantryItemLocation.getId(), mPantryItem.getId());
            startActivityForResult(intent, PantryItemActions.EDIT_PANTRY_ITEM_LOCATION);

//            Intent intent = PantryItemPagerActivity.newIntent(getActivity(), mPantryItem.getId());
//            startActivity(intent);
            //startActivityForResult(intent, EDIT_PANTRY_ITEM);
        }


        public void bind(Long pantryItemId, StoreLocation pantryItemLocation, int index)
        {
            mPantryItemLocation = pantryItemLocation;
            mIndex = index;
            mLocationDescription.setText(pantryItemLocation.toString());
            mPantryItemId = pantryItemId;
            itemView.setOnClickListener(this);
            //itemView.setOnClickListener(this);
        }

    }

    private class PantryItemLocationAdapter extends RecyclerView.Adapter<PantryItemFragment.LocationHolder> {

        private List<StoreLocation> mPantryItemLocations;

        public PantryItemLocationAdapter(List<StoreLocation> pantryItemLocations) {
            setPantryItemLocations(pantryItemLocations);
        }

        @Override
        public PantryItemFragment.LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PantryItemFragment.LocationHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PantryItemFragment.LocationHolder holder, int position) {
            StoreLocation pantryItemLocation = getPantryItemLocations().get(position);
            holder.bind(mPantryItem.getId(), pantryItemLocation, position);
        }

        @Override
        public int getItemCount() {
            return getPantryItemLocations().size();
        }

        public List<StoreLocation> getPantryItemLocations() {
            return mPantryItemLocations;
        }

        public void setPantryItemLocations(List<StoreLocation> pantryItemLocations) {
            mPantryItemLocations = pantryItemLocations;
        }
    }
}
