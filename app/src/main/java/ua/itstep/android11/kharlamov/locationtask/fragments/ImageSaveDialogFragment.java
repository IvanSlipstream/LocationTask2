package ua.itstep.android11.kharlamov.locationtask.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import ua.itstep.android11.kharlamov.locationtask.R;

/**
 * Created by Slipstream on 20.09.2016 in LocationTask.
 */

public class ImageSaveDialogFragment extends DialogFragment {

    private static final String KEY_FILE_NAME = "file_name";
    private String mFileToSave;
    private OnFragmentInteractionListener mListener;

    public ImageSaveDialogFragment() {
        super();
    }

    public static ImageSaveDialogFragment newInstance(String fileName) {

        Bundle args = new Bundle();
        args.putString(KEY_FILE_NAME, fileName);

        ImageSaveDialogFragment fragment = new ImageSaveDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFileToSave = getArguments().getString(KEY_FILE_NAME);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Drawable drawable = mListener.getDialogDrawable();
        if (drawable != null) {
            TextView textView = new TextView(getActivity());
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
            textView.setText(mFileToSave);
            textView.setGravity(Gravity.CENTER);
            builder.setView(textView);
        };
        return builder.setTitle(R.string.confirm_image_for_area_map)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        mListener.dismissSelectImageFragment();
                    }
                })
                .create();
    }

    public interface OnFragmentInteractionListener{
        Drawable getDialogDrawable();
        void dismissSelectImageFragment();
    }
}
