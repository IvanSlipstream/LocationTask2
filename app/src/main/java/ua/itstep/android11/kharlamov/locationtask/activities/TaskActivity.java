package ua.itstep.android11.kharlamov.locationtask.activities;

import android.content.ContentUris;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.db.DbHelper;
import ua.itstep.android11.kharlamov.locationtask.fragments.TaskListLocationFragment;
import ua.itstep.android11.kharlamov.locationtask.models.Task;
import ua.itstep.android11.kharlamov.locationtask.models.TaskLocationRelation;
import ua.itstep.android11.kharlamov.locationtask.provider.LocationTaskContentProvider;
import ua.itstep.android11.kharlamov.locationtask.services.LocationTaskIntentService;

public class TaskActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Task>>,
        TaskListLocationFragment.OnFragmentInteractionListener{

    private static int TASK_LOADER_ID = 1;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TaskListLocationFragment mTaskListLocationFragment;
    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            restartLoaderTasks();
        }
    };

    /**

     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private long mLocationId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLocationId = getIntent().getLongExtra(AreaMapFullScreenActivity.KEY_LOCATION_ID, -1);
        mTaskListLocationFragment = TaskListLocationFragment.newInstance(mLocationId);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final ResultReceiver receiver = new ResultReceiver(new Handler());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskLocationRelation relation = new TaskLocationRelation(3, mLocationId);
                Task task = new Task(0, "new test task");
                task.setTaskLocationRelation(relation);
                LocationTaskIntentService.startActionCreateTask(view.getContext(),
                        task, receiver);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerContentObserverForTasks();
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterContentObserverForTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerContentObserverForTasks() {
        getContentResolver().registerContentObserver(LocationTaskContentProvider.URI_ALL_TASKS, true, mObserver);
    }

    private void unregisterContentObserverForTasks() {
        getContentResolver().unregisterContentObserver(mObserver);
    }

    private void restartLoaderTasks() {
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public Loader<ArrayList<Task>> onCreateLoader(int id, Bundle args) {
        if (id == TASK_LOADER_ID) {
            return new AsyncTaskLoader<ArrayList<Task>>(this) {
                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public ArrayList<Task> loadInBackground() {
                    Uri uri = ContentUris.withAppendedId(LocationTaskContentProvider.URI_ALL_TASKS_IN_LOCATION, mLocationId);
                    ArrayList<Task> taskList = new ArrayList<>();
                    Cursor outerCursor = getContext().getContentResolver().query(uri,
                            null, null, null, null);
                    if (outerCursor != null) {
                        while (outerCursor.moveToNext()){
                            TaskLocationRelation relation = new TaskLocationRelation(outerCursor);
                            long id = relation.getTaskId();
                            Cursor innerCursor = getContext().getContentResolver().query(LocationTaskContentProvider.URI_ALL_TASKS,
                                    null, String.format(Locale.getDefault(), "%s=%d", DbHelper._ID, id),
                                    null, null);
                            if (innerCursor != null) {
                                innerCursor.moveToFirst();
                                Task task = new Task(innerCursor);
                                task.setTaskLocationRelation(relation);
                                innerCursor.close();
                                taskList.add(task);
                            }
                        }
                        outerCursor.close();
                    }
                    return taskList;
                }
            };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Task>> loader, ArrayList<Task> data) {
        if (loader.getId() == TASK_LOADER_ID) {
            mTaskListLocationFragment.setTaskList(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Task>> loader) {
        Log.d("test", String.format("Loader #%d is reset", loader.getId()));
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_task, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mTaskListLocationFragment;
                case 1:
                    return PlaceholderFragment.newInstance(1);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }
}
