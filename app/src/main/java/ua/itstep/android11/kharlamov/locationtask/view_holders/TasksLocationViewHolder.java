package ua.itstep.android11.kharlamov.locationtask.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import ua.itstep.android11.kharlamov.locationtask.R;

/**
 * Created by Slipstream on 22.10.2016 in LocationTask.
 */
public class TasksLocationViewHolder extends RecyclerView.ViewHolder {

    public ImageView mIvCompleted;
    public TextView mTvDescription;
    public RatingBar mRbRating;

    public TasksLocationViewHolder(View itemView) {
        super(itemView);
        mIvCompleted = (ImageView) itemView.findViewById(R.id.iv_task_completed);
        mTvDescription = (TextView) itemView.findViewById(R.id.tv_task_description);
        mRbRating = (RatingBar) itemView.findViewById(R.id.rb_task_location_rating);
    }
}
