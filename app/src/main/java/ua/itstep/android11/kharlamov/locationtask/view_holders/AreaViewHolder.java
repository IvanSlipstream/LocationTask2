package ua.itstep.android11.kharlamov.locationtask.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ua.itstep.android11.kharlamov.locationtask.R;

/**
 * Created by Slipstream on 04.09.2016 in Location Task.
 */
public class AreaViewHolder extends RecyclerView.ViewHolder {

    public TextView mTvAreaDescription;
    public TextView mTvAreaProperties;
    public ImageView mAreaThumbnail;

    public AreaViewHolder(View itemView) {
        super(itemView);
        mTvAreaDescription = (TextView) itemView.findViewById(R.id.tv_area_description);
        mAreaThumbnail = (ImageView) itemView.findViewById(R.id.iv_area_thumbnail);
        mTvAreaProperties = (TextView) itemView.findViewById(R.id.tv_area_properties);
    }
}
