package ua.itstep.android11.kharlamov.locationtask.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.services.LocationTaskIntentService;

/**
 * Created by Slipstream on 18.09.2016 in Location Task.
 */
public class SelectImageFragment extends Fragment {

    public static final int IMAGE_PADDING = 8;
    private static final String KEY_TRANSLATION_Y = "translation_y";
    private GridLayout mGvImageList;
    private ArrayList<String> mLinkList;
    private SelectImageFragment.OnFragmentInteractionListener mListener;
    private float mTranslationY;

    public static SelectImageFragment newInstance(float translationY) {

        Bundle args = new Bundle();
        args.putFloat(KEY_TRANSLATION_Y, translationY);
        SelectImageFragment fragment = new SelectImageFragment();
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
        if (getArguments() != null) {
            mTranslationY = getArguments().getFloat(KEY_TRANSLATION_Y, 0);
        }
        mLinkList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_list, container, false);
        view.setPadding(0, (int) mTranslationY, 0, 0);
        mGvImageList = (GridLayout) view.findViewById(R.id.gl_picture_list);
        LocationTaskIntentService.startActionLoadBitmaps(getActivity(), new ResultReceiver(new Handler()){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == LocationTaskIntentService.RESULT_CODE_EXTERNAL_BITMAP) {
                    ImageView imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setPadding(IMAGE_PADDING, IMAGE_PADDING, IMAGE_PADDING, IMAGE_PADDING);
                    Bitmap bitmap = resultData.getParcelable(LocationTaskIntentService.RESULT_BITMAP_KEY);
                    imageView.setImageBitmap(bitmap);
                    String fileName = resultData.getString(LocationTaskIntentService.RESULT_FILE_NAME_KEY);
                    mGvImageList.addView(imageView, mLinkList.size());
                    mLinkList.add(fileName);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Drawable drawable = ((ImageView) view).getDrawable();
                            int index = mGvImageList.indexOfChild(view);
                            if (index != -1) {
                                mListener.setDrawableForDialog(drawable, mLinkList.get(index));
                            }
                        }
                    });
                }
            }
        });
        return view;
    }

    public interface OnFragmentInteractionListener {
        void setDrawableForDialog(Drawable drawable, String fileName);
    }

}