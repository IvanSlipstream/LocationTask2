package ua.itstep.android11.kharlamov.locationtask.activities;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.db.DbHelper;
import ua.itstep.android11.kharlamov.locationtask.fragments.AddAreaMapFragment;
import ua.itstep.android11.kharlamov.locationtask.fragments.AreaListFragment;
import ua.itstep.android11.kharlamov.locationtask.fragments.ImageSaveDialogFragment;
import ua.itstep.android11.kharlamov.locationtask.fragments.SelectImageFragment;
import ua.itstep.android11.kharlamov.locationtask.models.AreaMap;
import ua.itstep.android11.kharlamov.locationtask.provider.LocationTaskContentProvider;
import ua.itstep.android11.kharlamov.locationtask.services.LocationTaskIntentService;

public class MainActivity extends AppCompatActivity implements
        AreaListFragment.OnFragmentInteractionListener,
        AddAreaMapFragment.OnFragmentInteractionListener,
        SelectImageFragment.OnFragmentInteractionListener,
        ImageSaveDialogFragment.OnFragmentInteractionListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        FragmentManager.OnBackStackChangedListener {

    // TODO: handle screen rotation
    // TODO: clean up internal storage files
    private static final int LOADER_ID_IMAGE_LIST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String KEY_FILE_NAME = "file_name";
    public static final String KEY_CLICKED_AREA_MAP_ID = "clicked_area_map";
    public static final String TAG_ADD_AREA_MAP = "add_area_map";
    public static final String TAG_DIALOG = "dialog";
    private static final String TAG_BACK_STACK_AREA_MAP_LIST = "back_stack_area_map_list";
    private static final String TAG_IMAGE_LIST = "image_list";
    private static final String LOG_TAG = "test";


    private Drawable mDialogDrawable;
    private String mFileName;
    private ResultReceiver mReceiver;
    private Toolbar mToolbar;
    private FloatingActionButton fab;
    private boolean mImageExternal = false;
    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            restartLoaderAreaMaps();
        }
    };;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportLoaderManager().initLoader(LOADER_ID_IMAGE_LIST, null, this);
        mReceiver = new ResultReceiver(new Handler()){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                switch (resultCode) {
                    case LocationTaskIntentService.RESULT_CODE_INSERTED_AREA_MAP:
                        break;
                    case LocationTaskIntentService.RESULT_CODE_FAILURE:
                        if (resultData != null){
                            showErrorToast(resultData.getString(LocationTaskIntentService.RESULT_ERROR_CAUSE_KEY));
                        }
                }
            }
        };
//        LocationView locationView = new LocationView(this, null);
//        addContentView(locationView, new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        locationView.setX(100);
//        locationView.setY(200);
//        locationView.drawText("23", 50);
        registerContentObserverForAreaMaps();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnFabClickListener(fragmentManager));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterContentObserverForAreaMaps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerContentObserverForAreaMaps();
    }

    private void registerContentObserverForAreaMaps() {
        getContentResolver().registerContentObserver(LocationTaskContentProvider.URI_ALL_AREA_MAPS, true, mObserver);
    }

    private void unregisterContentObserverForAreaMaps() {
        getContentResolver().unregisterContentObserver(mObserver);
    }

    private void showErrorToast(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && mFileName != null) {
            ResultReceiver receiver = new ResultReceiver(new Handler()){
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == LocationTaskIntentService.RESULT_CODE_INTERNAL_BITMAP) {
                        Bitmap imageBitmap = resultData.getParcelable(LocationTaskIntentService.RESULT_BITMAP_KEY);
                        if (imageBitmap != null) {
                            mDialogDrawable = new BitmapDrawable(getResources(), imageBitmap);
                        }
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_ADD_AREA_MAP);
                        if (fragment != null && fragment instanceof AddAreaMapFragment) {
                            ((AddAreaMapFragment) fragment).setPreviewDrawable();
                        }
                        mImageExternal = false;
                    }
                }
            };
            LocationTaskIntentService.startActionLoadBitmapByFileName(this, mFileName, false, receiver);
        }
    }

    @Override
    public void setDrawableForDialog(Drawable drawable, String fileName) {
        this.mDialogDrawable = drawable;
        this.mFileName = fileName;
        this.mImageExternal = true;
        ImageSaveDialogFragment.newInstance(fileName).show(getSupportFragmentManager(), TAG_DIALOG);
    }

    @Override
    public Drawable getDialogDrawable() {
        return mDialogDrawable;
    }

    @Override
    public void dismissSelectImageFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_IMAGE_LIST);
        if (fragment != null) {
            getSupportFragmentManager().popBackStack();
        }
        fragment = getSupportFragmentManager().findFragmentByTag(TAG_ADD_AREA_MAP);
        if (fragment != null && fragment instanceof AddAreaMapFragment) {
            ((AddAreaMapFragment) fragment).setPreviewDrawable();
        }
    }

    @Override
    public Drawable getPreviewDrawable() {
        return mDialogDrawable;
    }


    @Override
    public void showSelectImageFragment() {
        float translationY = mToolbar.getHeight();
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .add(R.id.cl_main_root, SelectImageFragment.newInstance(translationY), TAG_IMAGE_LIST)
                .commit();
    }

    @Override
    public String getAreaMapFileName() {
        return mFileName;
    }


    @Override
    public void onAreaMapItemClick(AreaMap areaMap) {
        Intent intent = new Intent(this, AreaMapFullScreenActivity.class);
        intent.putExtra(KEY_CLICKED_AREA_MAP_ID, areaMap.getId());
        startActivityForResult(intent, 0, null);
    }

    @Override
    public void startPictureCaptureActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // TODO: Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ua.itstep.android11.kharlamov.locationtask.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                getFilesDir()      /* directory */
        );
        mFileName = image.getName();
        return image;
    }

    public void restartLoaderAreaMaps() {
        getSupportLoaderManager().restartLoader(LOADER_ID_IMAGE_LIST, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id==LOADER_ID_IMAGE_LIST) {
            return new CursorLoader(this,
                    LocationTaskContentProvider.URI_ALL_AREA_MAPS,
                    null, null, null, DbHelper.AreaMapsFields.DESCRIPTION);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (loader.getId()==LOADER_ID_IMAGE_LIST) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_area_list);
            if (fragment != null && fragment instanceof AreaListFragment) {
                if (c != null) {
                    ArrayList<AreaMap> areaMapList = new ArrayList<>();
                    while (c.moveToNext()) {
                        areaMapList.add(new AreaMap(c));
                    }
                    ((AreaListFragment) fragment).setAreaMapList(areaMapList);
                    c.close();
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, String.format("Loader #%d is reset", loader.getId()));
    }

    @Override
    public void onBackStackChanged() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_ADD_AREA_MAP);
        if (fab == null){
            return;
        }
        if (fragment != null && fragment.isVisible()){
            fab.setImageResource(R.mipmap.ic_save_item);
        } else {
            fab.setImageResource(R.mipmap.ic_add_item);
        }
    }

    private class OnFabClickListener implements View.OnClickListener {

        private FragmentManager mFragmentManager;

        public OnFabClickListener(FragmentManager fragmentManager) {
            this.mFragmentManager = fragmentManager;
        }

        @Override
        public void onClick(View view) {
            Fragment fragment = mFragmentManager.findFragmentByTag(TAG_ADD_AREA_MAP);
            if (fab == null){
                return;
            }
            if (fragment == null || !fragment.isVisible()) {
                fragment = AddAreaMapFragment.newInstance(fab.getHeight());
                mFragmentManager.beginTransaction()
                        .addToBackStack(TAG_BACK_STACK_AREA_MAP_LIST)
                        .replace(R.id.cl_main_root, fragment, TAG_ADD_AREA_MAP)
                        .commit();
            } else {
                if (fragment instanceof AddAreaMapFragment) {
                    AreaMap areaMap = ((AddAreaMapFragment) fragment).getAreaMap();
                    if (areaMap != null) {
                        LocationTaskIntentService.startActionCreateAreaMap(view.getContext(), areaMap, mImageExternal, mReceiver);
                    }
                    mFragmentManager.popBackStack(TAG_BACK_STACK_AREA_MAP_LIST, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }
}
