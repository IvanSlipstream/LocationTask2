package ua.itstep.android11.kharlamov.locationtask.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.db.DbHelper;
import ua.itstep.android11.kharlamov.locationtask.fragments.LocationSaveDialogFragment;
import ua.itstep.android11.kharlamov.locationtask.models.AreaMap;
import ua.itstep.android11.kharlamov.locationtask.models.Location;
import ua.itstep.android11.kharlamov.locationtask.provider.LocationTaskContentProvider;
import ua.itstep.android11.kharlamov.locationtask.services.LocationTaskIntentService;
import ua.itstep.android11.kharlamov.locationtask.views.LocationView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AreaMapFullScreenActivity extends AppCompatActivity
        implements View.OnTouchListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        LocationSaveDialogFragment.OnFragmentInteractionListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final String KEY_AREA_MAP_ID = "area_map_id";
    private static final int BACKGROUND_LOADER_ID = 1;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final String TAG_DIALOG = "dialog";
    private final Handler mHideHandler = new Handler();
    private ImageView mContentView;
    private TextView mTvOverlay;
    private AreaMap mAreaMap;
    private ArrayList<LocationView> mLocationViewList;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private boolean mAddLocationIssued = false;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
            hide();
            mAddLocationIssued = true;
            mTvOverlay.setVisibility(View.VISIBLE);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_area_map_full_screen);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mTvOverlay = (TextView) findViewById(R.id.tv_highlight_fullscreen);
        mContentView = (ImageView) findViewById(R.id.fullscreen_content);
        mContentView.setOnTouchListener(this);
        long mAreaMapId = getIntent().getLongExtra(MainActivity.KEY_CLICKED_AREA_MAP_ID, -1);
        Log.d("test", String.format("Fullscreen for AreaMap #%d", mAreaMapId));
        mLocationViewList = new ArrayList<>();
        Bundle loaderBundle = new Bundle();
        loaderBundle.putLong(KEY_AREA_MAP_ID, mAreaMapId);
        getSupportLoaderManager().initLoader(BACKGROUND_LOADER_ID, loaderBundle, this);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.btn_add_location_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    // setup location view and add it to the content view
    private LocationView addLocationView(final Location location, int count) {
        LocationView locationView = new LocationView(this, null);
        addContentView(locationView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (mLocationViewList == null) {
            mLocationViewList = new ArrayList<>();
        }
        mLocationViewList.add(locationView);
        locationView.setX(location.getLocalX());
        locationView.setY(location.getLocalY());
        locationView.setModelId(location.getId());
        locationView.drawText(String.valueOf(count), getResources().getDimensionPixelSize(R.dimen.location_view_text_size));
        locationView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("test", "clicked " + String.valueOf(((LocationView) view).getModelId()));
                    return true;
                } else {
                    return false;
                }
            }
        });
        return locationView;
    }

    private void showErrorToast(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private View findViewByLocationId(long id){
        for (LocationView locationView: mLocationViewList){
            if (locationView.getModelId() == id) {
                return locationView;
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    // touch content view to add a new
    @Override
    public boolean onTouch(View view, final MotionEvent motionEvent) {
        if (view.getId() == R.id.fullscreen_content && mAddLocationIssued && motionEvent.getAction() == MotionEvent.ACTION_UP) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            mAddLocationIssued = false;
            Location location = new Location((int) mAreaMap.getId(), x, y, getString(R.string.location_default_name));
            Log.d("test", String.format("x=%f, y=%f", x-mContentView.getX(), y-mContentView.getY()));
            final LocationView locationView = addLocationView(location, 0);
            final ResultReceiver receiver = new ResultReceiver(new Handler()){
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == LocationTaskIntentService.RESULT_CODE_INSERTED_LOCATION && resultData != null) {
                        long id = resultData.getLong(LocationTaskIntentService.RESULT_ID_KEY, -1);
                        Log.d("test", String.format("Saved Location #%d", id));
                        locationView.setModelId(id);
                    }
                }
            };
            mTvOverlay.setVisibility(View.GONE);
            LocationSaveDialogFragment.newInstance(location, receiver).show(getSupportFragmentManager(), TAG_DIALOG);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long areaMapId = args.getLong(KEY_AREA_MAP_ID, -1);
        if (areaMapId != -1) {
            return new CursorLoader(this, LocationTaskContentProvider.URI_ALL_AREA_MAPS, null,
                    String.format(Locale.getDefault(), "%s = %d", DbHelper._ID, areaMapId), null, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c != null) {
            c.moveToFirst();
            mAreaMap = new AreaMap(c);
            c.close();
            mContentView.setImageBitmap(BitmapFactory.decodeFile(getFilesDir() + File.separator + mAreaMap.getPath()));
        }
        if (mAreaMap != null) {
            ResultReceiver receiver = new ResultReceiver(new Handler()){
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == LocationTaskIntentService.RESULT_CODE_LOCATION_IN_AREA && resultData != null) {
                        Location location = resultData.getParcelable(LocationTaskIntentService.RESULT_LOCATION_KEY);
                        int count = resultData.getInt(LocationTaskIntentService.RESULT_COUNT_KEY, 0);
                        addLocationView(location, count);
                    }
                    if (resultCode == LocationTaskIntentService.RESULT_CODE_FAILURE && resultData != null) {
                        showErrorToast(resultData.getString(LocationTaskIntentService.RESULT_ERROR_CAUSE_KEY));
                    }
                }
            };
            LocationTaskIntentService.startActionLoadAllLocationsInArea(this, mAreaMap, receiver);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("test", String.format("Loader #%d is reset", loader.getId()));
    }

    @Override
    public void onDismiss() {
        LocationView view = (LocationView) findViewByLocationId(-1);
        if (view != null) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            mLocationViewList.remove(view);
            assert viewGroup != null;
            viewGroup.removeView(view);
        }
    }

    @Override
    public void onBackPressed() {
        if (mAddLocationIssued && mTvOverlay != null) {
            mTvOverlay.setVisibility(View.GONE);
            mAddLocationIssued = false;
        } else {
            super.onBackPressed();
        }
    }
}
