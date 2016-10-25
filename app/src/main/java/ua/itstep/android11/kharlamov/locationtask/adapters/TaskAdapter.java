package ua.itstep.android11.kharlamov.locationtask.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.util.ArrayList;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.fragments.AreaListFragment;
import ua.itstep.android11.kharlamov.locationtask.fragments.TaskListLocationFragment;
import ua.itstep.android11.kharlamov.locationtask.models.Task;
import ua.itstep.android11.kharlamov.locationtask.models.TaskLocationRelation;
import ua.itstep.android11.kharlamov.locationtask.view_holders.TasksLocationViewHolder;

/**
 * Created by Slipstream on 23.10.2016 in LocationTask.
 */
public class TaskAdapter extends RecyclerView.Adapter<TasksLocationViewHolder> {

    private ArrayList<Task> mTaskList;
    private TaskListLocationFragment.OnFragmentInteractionListener mListener;

    public TaskAdapter(@NonNull TaskListLocationFragment.OnFragmentInteractionListener listener) {
        this.mTaskList = new ArrayList<>();
        this.mListener = listener;
    }

    public TaskAdapter(@NonNull TaskListLocationFragment.OnFragmentInteractionListener listener, ArrayList<Task> taskList) {
        this.mTaskList = taskList;
        this.mListener = listener;
    }

    @Override
    public TasksLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_view_holder, parent, false);
        return new TasksLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksLocationViewHolder holder, int position) {
        final Task task = mTaskList.get(position);
        holder.mTvDescription.setText(task.getDescription());
        holder.mIvCompleted.setImageResource(task.getStatus() == Task.NOT_COMPLETED ?
                R.mipmap.ic_not_completed : R.mipmap.ic_completed);
        holder.mIvCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ImageView) view).setImageResource(task.getStatus() == Task.NOT_COMPLETED ?
                        R.mipmap.ic_completed : R.mipmap.ic_not_completed);
            }
        });
        TaskLocationRelation relation = task.getTaskLocationRelation();
        if (relation != null) {
            holder.mRbRating.setRating((float) relation.getRating()/2);
            holder.mRbRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    TaskLocationRelation relation = task.getTaskLocationRelation();
                    relation.setRating((int) (v*2));
                    task.setTaskLocationRelation(relation);
                    mListener.updateTask(task);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }
}
