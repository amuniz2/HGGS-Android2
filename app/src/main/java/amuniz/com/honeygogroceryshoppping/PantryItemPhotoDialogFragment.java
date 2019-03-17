package amuniz.com.honeygogroceryshoppping;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.UUID;

import amuniz.com.honeygogroceryshoppping.model.PantryItem;

public class PantryItemPhotoDialogFragment extends AppCompatDialogFragment {

    private static final String ARG_PANTRY_ITEM_IMAGE = "com.amuniz.pantryitemintent.pantry_item_image";
    private static final String ARG_PANTRY_ITEM_IMAGE_TITLE = "com.amuniz.pantryitemintent.pantry_item_image_title";
    private static final String ARG_PANTRY_ITEM_IMAGE_DESCRIPTION = "com.amuniz.pantryitemintent.pantry_item_image_description";

    //private UUID mPantryItemId;
    private ImageView mPhotoView;


    static PantryItemPhotoDialogFragment newInstance(String title, String description, File photoFile) {

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(ARG_PANTRY_ITEM_IMAGE, photoFile);
        args.putString(ARG_PANTRY_ITEM_IMAGE_TITLE, title);
        args.putString(ARG_PANTRY_ITEM_IMAGE_DESCRIPTION, description);

        PantryItemPhotoDialogFragment f = new PantryItemPhotoDialogFragment();
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_dialog, container, false);

        File photoFile = (File) getArguments().getSerializable(ARG_PANTRY_ITEM_IMAGE);
        String title = getArguments().getString(ARG_PANTRY_ITEM_IMAGE_TITLE);
        String itemDescription = getArguments().getString(ARG_PANTRY_ITEM_IMAGE_DESCRIPTION);

        Bitmap image = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());

        ImageView imageView = (ImageView) rootView.findViewById(R.id.pantry_item_photo);
        imageView.setImageBitmap(image);

        getDialog().setTitle(title);
        TextView descView = (TextView) rootView.findViewById(R.id.description);
        descView.setText(itemDescription);

        Button dismiss = (Button) rootView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState)
//    {
////        Dialog dlg = super.onCreateDialog(savedInstanceState);
//
//        File photoFile = (File) getArguments().getSerializable(ARG_PANTRY_ITEM_IMAGE);
//
//        Bitmap image = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
//
//        View v =  LayoutInflater.from(getActivity()).inflate(R.layout.fragment_photo_dialog, null);
//
//        ImageView imageView = (ImageView) v.findViewById(R.id.pantry_item_photo);
//        imageView.setImageBitmap(image);
//
//
//        Dialog dialog = new AlertDialog.Builder(getActivity()).create;
//        return .setView(imageView);
//    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setStyle(DialogFragment.STYLE_NORMAL, 0);
//
//    }

//    private ImageView addPhotoViewToDialog()
//    {
//        mPhotoView = new ImageView(getActivity());
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//
//        // Add rule to layout parameters
//        // Add the ImageView below to Button
//        lp.addRule(RelativeLayout.BELOW, btn.getId());
//
//        // Add layout parameters to ImageView
//        mPhotoView.setLayoutParams(lp);
//
//        // Finally, add the ImageView to layout
//        rl.addView(mPhotoView);
//
//
//        photoView.setImageBitmap();
//
//    }
//    #Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                           Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_photo_dialog, container, false);
//
//
//        PantryItem pantryItem = Pantry.get(getActivity()).getPantryItem(mPantryItemId);
//        mPhotoView = (ImageView)v.findViewById(R.id.pantry_item_photo);
//
//        updatePhotoView();
//
//        // Watch for button clicks.
//        Button okButton = (Button)v.findViewById(R.id.ok_button);
//        okButton.setOnClickListener(new DialogInterface.OnClickListener() {
//            public void onClick(View v) {
//                // When button is clicked, call up to owning activity.
//                (getActivity()).showDialog();
//            }
//        });
//
//        return v;
//    }

//    private void updatePhotoView() {
//        if (mPhotoView == null || !mPhotoFile.exists()) {
//            mPhotoView.setImageDrawable(null);
//        }
//        else {
//            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
//            mPhotoView.setImageBitmap(bitmap);
//        }
//    }

//Pantry.get(getActivity()).getPhotoFile(mPantryItem);
}
