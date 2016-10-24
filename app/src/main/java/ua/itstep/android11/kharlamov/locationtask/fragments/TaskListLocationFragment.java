package ua.itstep.android11.kharlamov.locationtask.fragments;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Locale;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.adapters.TaskAdapter;
import ua.itstep.android11.kharlamov.locationtask.db.DbHelper;
import ua.itstep.android11.kharlamov.locationtask.models.Task;
import ua.itstep.android11.kharlamov.locationtask.models.TaskLocationRelation;
import ua.itstep.android11.kharlamov.locationtask.provider.LocationTaskContentProvider;
import ua.itstep.android11.kharlamov.locationtask.services.LocationTaskIntentService;

/**
 * Created by Slipstream on 23.10.2016 in LocationTask.
 */
public class TaskListLocationFragment extends Fragment {

    private static final String KEY_LOCATION_ID = "location_id";
    private RecyclerView mRvTaskList;
    private TaskAdapter mAdapter;
    private long mLocationId;
    private OnFragmentInteractionListener mListener;

    public static TaskListLocationFragment newInstance(long locationId) {

        Bundle args = new Bundle();
        args.putLong(KEY_LOCATION_ID, locationId);
        TaskListLocationFragment fragment = new TaskListLocationFragment();
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
            mLocationId = getArguments().getLong(KEY_LOCATION_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        mRvTaskList = (RecyclerView) view.findViewById(R.id.rv_task_list);
        mRvTaskList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvTaskList.setAdapter(mAdapter);
        return view;
    }

    public void setTaskList(ArrayList<Task> taskList) {
        TaskAdapter adapter = new TaskAdapter(taskList);
        mAdapter = adapter;
        if (mRvTaskList != null) {
            mRvTaskList.swapAdapter(adapter, false);
        }
    }

    public interface OnFragmentInteractionListener{

    }
}
