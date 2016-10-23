package ua.itstep.android11.kharlamov.locationtask.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.models.Location;
import ua.itstep.android11.kharlamov.locationtask.services.LocationTaskIntentService;

/**
 * Created by Slipstream on 21.10.2016 in LocationTask.
 */
public class LocationSaveDialogFragment extends DialogFragment {

    private static final String KEY_LOCATION_TO_SAVE = "location";
    private static final String KEY_RESULT_RECEIVER = "receiver";
    Location mLocation;
    ResultReceiver mReceiver;
    OnFragmentInteractionListener mListener;

    public LocationSaveDialogFragment() {
        super();
    }

    public static LocationSaveDialogFragment newInstance(Location location, ResultReceiver receiver) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_LOCATION_TO_SAVE, location);
        args.putParcelable(KEY_RESULT_RECEIVER, receiver);
        LocationSaveDialogFragment fragment = new LocationSaveDialogFragment();
        fragment.setArguments(args);
        return fragment;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mLocation = getArguments().getParcelable(KEY_LOCATION_TO_SAVE);
            mReceiver = getArguments().getParcelable(KEY_RESULT_RECEIVER);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
        editText.setHint(R.string.brief_description_hint);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        builder.setView(editText)
                .setTitle(R.string.confirm_create_new_location)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDismiss();
                        dismiss();
                    }
                })
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mLocation.setDescription(String.valueOf(editText.getText()));
                        LocationTaskIntentService.startActionCreateLocation(getContext(), mLocation, mReceiver);
                    }
                });
        return builder.create();
    }

    public interface OnFragmentInteractionListener {
        void onDismiss();
    }
}
