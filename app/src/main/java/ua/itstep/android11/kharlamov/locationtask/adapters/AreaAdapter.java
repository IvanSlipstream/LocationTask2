package ua.itstep.android11.kharlamov.locationtask.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.fragments.AreaListFragment;
import ua.itstep.android11.kharlamov.locationtask.models.AreaMap;
import ua.itstep.android11.kharlamov.locationtask.view_holders.AreaViewHolder;

/**
 * Created by Slipstream on 07.09.2016 in Location Task.
 */
public class AreaAdapter extends RecyclerView.Adapter<AreaViewHolder> {

    private ArrayList<AreaMap> mAreaMaps;
    private Context mContext;
    private AreaListFragment.OnFragmentInteractionListener mListener;

    public AreaAdapter(Context context, ArrayList<AreaMap> initialMapList) {
        this.mAreaMaps = initialMapList;
        this.mContext = context;
    }

    public void addAreaMap(AreaMap areaMap, int position) {
        mAreaMaps.add(position, areaMap);
    }

    @Override
    public AreaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.area_map_view_holder, parent, false);
        return new AreaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AreaViewHolder holder, int position) {
        final AreaMap areaMap = mAreaMaps.get(position);
        String properties;
        if (areaMap.getFloor() != 0) {
            properties = mContext.getResources().getString(R.string.properties_area_map,
                    (int) areaMap.getRealWidth(), (int) areaMap.getRealHeight(), areaMap.getFloor());
        } else {
            properties = mContext.getResources().getString(R.string.properties_area_map_no_floor,
                    (int) areaMap.getRealWidth(), (int) areaMap.getRealHeight());
        }
        holder.mTvAreaDescription.setText(areaMap.getDescription());
        Bitmap bitmap = getResizedBitmap(areaMap);
        holder.mAreaThumbnail.setImageBitmap(bitmap);
        holder.mTvAreaProperties.setText(properties);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onAreaMapItemClick(areaMap);
                }
            }
        });
    }

    public void setOnRvItemClickListener(AreaListFragment.OnFragmentInteractionListener listener) {
        this.mListener = listener;
    }

    public Bitmap getResizedBitmap(AreaMap areaMap) {
        final int maxSize = 128;
        float scaledWidth = maxSize;
        float scaledHeight = maxSize;
        Bitmap bitmap = BitmapFactory.decodeFile(mContext.getFilesDir()+ File.separator+areaMap.getPath());
        if (bitmap==null){
            return null;
        }
        if (bitmap.getHeight() > bitmap.getWidth()) {
            scaledWidth = (float) bitmap.getWidth() / (float) bitmap.getHeight() * maxSize;
        } else {
            scaledHeight = (float) bitmap.getHeight() / (float) bitmap.getWidth() * maxSize;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) scaledWidth, (int) scaledHeight, false);
        bitmap.prepareToDraw();
        return bitmap;
    }

    @Override
    public int getItemCount() {
        return mAreaMaps.size();
    }
}
