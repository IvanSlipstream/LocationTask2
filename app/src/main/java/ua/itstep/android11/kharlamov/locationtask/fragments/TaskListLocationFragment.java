package ua.itstep.android11.kharlamov.locationtask.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.itstep.android11.kharlamov.locationtask.R;

/**
 * Created by Slipstream on 23.10.2016 in LocationTask.
 */
public class TaskListLocationFragment extends Fragment {

    private RecyclerView mRvTaskList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        mRvTaskList = (RecyclerView) view.findViewById(R.id.rv_task_list);
        return view;
    }
}
