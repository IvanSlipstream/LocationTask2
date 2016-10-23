package ua.itstep.android11.kharlamov.locationtask.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.activities.MainActivity;
import ua.itstep.android11.kharlamov.locationtask.adapters.AreaAdapter;
import ua.itstep.android11.kharlamov.locationtask.models.AreaMap;

/**
 * Created by Slipstream on 09.09.2016 in Location Task.
 */
public class AddAreaMapFragment extends Fragment {

    private static final String KEY_TRANSLATION_Y = "translation_y";

    private ImageButton mIbTakePicture;
    private ImageButton mIbSelectPicture;
    private EditText mEtWidth;
    private EditText mEtHeight;
    private EditText mEtFloor;
    private EditText mEtDescription;
    private ImageView mIvPreview;
    private OnFragmentInteractionListener mListener;
    private float mTranslationY;

    public AddAreaMapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_area_map, container, false);
        view.setPadding(0, (int) mTranslationY, 0, (int) mTranslationY);
        mIbTakePicture = (ImageButton) view.findViewById(R.id.ib_take_picture);
        mIbSelectPicture = (ImageButton) view.findViewById(R.id.ib_select_picture);
        mEtWidth = (EditText) view.findViewById(R.id.et_area_map_width);
        mEtHeight = (EditText) view.findViewById(R.id.et_area_map_height);
        mEtFloor = (EditText) view.findViewById(R.id.et_area_map_floor);
        mEtDescription = (EditText) view.findViewById(R.id.et_area_map_description);
        mIvPreview = (ImageView) view.findViewById(R.id.iv_area_load_preview);
        mIbSelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.showSelectImageFragment();
            }
        });
        mIbTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.startPictureCaptureActivity();
            }
        });
        setPreviewDrawable();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mTranslationY = getArguments().getFloat(KEY_TRANSLATION_Y, 0);
        }
    }

    public void setPreviewDrawable() {
        if (mIvPreview != null ) {
            Drawable drawable = mListener.getPreviewDrawable();
            if (drawable != null) {
                mIvPreview.setImageDrawable(drawable);
            } else {
                mIvPreview.setImageResource(R.mipmap.ic_image_placeholder);
            }
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

    public static AddAreaMapFragment newInstance(float translationY) {
        AddAreaMapFragment fragment = new AddAreaMapFragment();
        Bundle args = new Bundle();
        args.putFloat(KEY_TRANSLATION_Y, translationY);
        fragment.setArguments(args);
        return fragment;
    }

    public AreaMap getAreaMap () {
        try {
            String path = mListener.getAreaMapFileName();
            int realWidth = Integer.parseInt(String.valueOf(mEtWidth.getText()));
            int realHeight = Integer.parseInt(String.valueOf(mEtHeight.getText()));
            int buildingId = 0;
            String description = String.valueOf(mEtDescription.getText());
            int floor = Integer.parseInt(String.valueOf(mEtFloor.getText()));
            return new AreaMap(path, realWidth, realHeight, buildingId, description, floor);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    public interface OnFragmentInteractionListener {
        Drawable getPreviewDrawable();
        void showSelectImageFragment();
        String getAreaMapFileName();
        void startPictureCaptureActivity();
    }

}
