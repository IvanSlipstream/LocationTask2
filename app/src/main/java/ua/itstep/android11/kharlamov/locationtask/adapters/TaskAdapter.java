package ua.itstep.android11.kharlamov.locationtask.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.models.Task;
import ua.itstep.android11.kharlamov.locationtask.models.TaskLocationRelation;
import ua.itstep.android11.kharlamov.locationtask.view_holders.TasksLocationViewHolder;

/**
 * Created by Slipstream on 23.10.2016 in LocationTask.
 */
public class TaskAdapter extends RecyclerView.Adapter<TasksLocationViewHolder> {

    private ArrayList<Task> mTaskList;

    public TaskAdapter() {
        mTaskList = new ArrayList<>();
    }

    public TaskAdapter(ArrayList<Task> taskList) {
        this.mTaskList = taskList;
    }

    @Override
    public TasksLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_view_holder, parent, false);
        return new TasksLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksLocationViewHolder holder, int position) {
        Task task = mTaskList.get(position);
        holder.mTvDescription.setText(task.getDescription());
        holder.mIvCompleted.setImageResource(task.getStatus() == Task.NOT_COMPLETED ?
                R.mipmap.ic_not_completed : R.mipmap.ic_completed);
        TaskLocationRelation relation = task.getTaskLocationRelation();
        if (relation != null) {
            holder.mRbRating.setRating(relation.getRating());
        }
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }
}
