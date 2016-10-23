package ua.itstep.android11.kharlamov.locationtask.fragments;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.adapters.AreaAdapter;
import ua.itstep.android11.kharlamov.locationtask.models.AreaMap;

public class AreaListFragment extends Fragment {

    private RecyclerView mRvAreaList;
    private OnFragmentInteractionListener mListener;
    private AreaAdapter mAdapter;

    public AreaListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAdapter = new AreaAdapter(getActivity(), new ArrayList<AreaMap>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_area_list, container, false);
        mRvAreaList = (RecyclerView) view.findViewById(R.id.rv_area_list);
        mRvAreaList.setAdapter(mAdapter);
        mRvAreaList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
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

    public void addAreaMap (AreaMap areaMap) {
        mAdapter.addAreaMap(areaMap, 0);
        if (mRvAreaList != null) {
            mRvAreaList.swapAdapter(mAdapter, false);
        }
    }

    public void setAreaMapList(ArrayList<AreaMap> areaMapList) {
        this.mAdapter = new AreaAdapter(getActivity(), areaMapList);
        // TODO remove new object allocation
        if (mListener != null) {
            mAdapter.setOnRvItemClickListener(mListener);
        }
        this.mRvAreaList.swapAdapter(mAdapter, true);
    }

    public interface OnFragmentInteractionListener {
        void onAreaMapItemClick(AreaMap areaMap);
    }

}
