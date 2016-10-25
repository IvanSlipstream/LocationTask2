package ua.itstep.android11.kharlamov.locationtask.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import ua.itstep.android11.kharlamov.locationtask.R;
import ua.itstep.android11.kharlamov.locationtask.db.DbHelper;
import ua.itstep.android11.kharlamov.locationtask.models.AreaMap;
import ua.itstep.android11.kharlamov.locationtask.models.Location;
import ua.itstep.android11.kharlamov.locationtask.models.Task;
import ua.itstep.android11.kharlamov.locationtask.models.TaskLocationRelation;
import ua.itstep.android11.kharlamov.locationtask.provider.LocationTaskContentProvider;

public class LocationTaskIntentService extends IntentService {

    private static final String ACTION_CREATE_AREA_MAP = "ua.itstep.android11.kharlamov.locationtask.services.action.CREATE_AREA_MAP";
    private static final String ACTION_CREATE_LOCATION = "ua.itstep.android11.kharlamov.locationtask.services.action.CREATE_LOCATION";
    private static final String ACTION_CREATE_TASK = "ua.itstep.android11.kharlamov.locationtask.services.action.CREATE_TASK";
    private static final String ACTION_LOAD_BITMAPS = "ua.itstep.android11.kharlamov.locationtask.services.action.LOAD_BITMAPS";
    private static final String ACTION_LOAD_ALL_LOCATIONS_IN_AREA = "ua.itstep.android11.kharlamov.locationtask.services.action.LOAD_ALL_LOCATIONS_IN_AREA";
    private static final String ACTION_LOAD_BITMAP_BY_FILE_NAME = "ua.itstep.android11.kharlamov.locationtask.services.action.LOAD_BITMAP_BY_FILE_NAME";
    private static final String ACTION_UPDATE_TASK = "ua.itstep.android11.kharlamov.locationtask.services.action.UPDATE_TASK";

    private static final String EXTRA_AREA_MAP = "ua.itstep.android11.kharlamov.locationtask.services.extra.AREA_MAP";
    private static final String EXTRA_LOCATION = "ua.itstep.android11.kharlamov.locationtask.services.extra.LOCATION";
    private static final String EXTRA_TASK = "ua.itstep.android11.kharlamov.locationtask.services.extra.TASK";
    private static final String EXTRA_RESULT_RECEIVER = "ua.itstep.android11.kharlamov.locationtask.services.extra.RESULT_RECEIVER";
    private static final String EXTRA_FILE_NAME = "ua.itstep.android11.kharlamov.locationtask.services.extra.FILE_NAME";
    private static final String EXTRA_IS_EXTERNAL = "ua.itstep.android11.kharlamov.locationtask.services.extra.IS_EXTERNAL";

    public static final String EXTERNAL_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .toString() + File.separator;

    public LocationTaskIntentService() {
        super("LocationTaskIntentService");
    }

    public static final String RESULT_ID_KEY = "id";

    public static final String RESULT_BITMAP_KEY = "bitmap";

    public static final String RESULT_COUNT_KEY = "count";
    public static final String RESULT_FILE_NAME_KEY = "file_name";
    public static final String RESULT_LOCATION_KEY = "location";
    private static final String RESULT_RELATION_KEY = "relation";
    public static final String RESULT_ERROR_CAUSE_KEY = "error_cause";
    public static final int RESULT_CODE_FAILURE = -1;

    public static final int RESULT_CODE_INSERTED_AREA_MAP = 1;
    public static final int RESULT_CODE_EXTERNAL_BITMAP = 2;
    public static final int RESULT_CODE_LOCATION_IN_AREA = 3;
    public static final int RESULT_CODE_INTERNAL_BITMAP = 4;
    public static final int RESULT_CODE_INSERTED_LOCATION = 5;
    public static final int RESULT_CODE_INSERTED_TASK = 6;

    public static final int MAX_BITMAP_SIZE = 128;

    /**
     * Saves AreaMap instance to the database.
     * @param context invoker context.
     * @param external true if the image file is external, false if internal.
     * @param areaMap an instance to save.
     */
    public static void startActionCreateAreaMap(Context context, AreaMap areaMap, boolean external, ResultReceiver receiver) {
        Intent intent = new Intent(context, LocationTaskIntentService.class);
        intent.setAction(ACTION_CREATE_AREA_MAP);
        intent.putExtra(EXTRA_AREA_MAP, areaMap);
        intent.putExtra(EXTRA_IS_EXTERNAL, external);
        intent.putExtra(EXTRA_RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

    /**
     * Saves Location instance to the database
     * @param context invoker context.
     * @param location an instance to save.
     */
    public static void startActionCreateLocation(Context context, Location location, ResultReceiver receiver) {
        Intent intent = new Intent(context, LocationTaskIntentService.class);
        intent.setAction(ACTION_CREATE_LOCATION);
        intent.putExtra(EXTRA_LOCATION, location);
        intent.putExtra(EXTRA_RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

    /**
     * Saves Task and its relation to Location instance to the database
     * @param context invoker context.
     * @param task an instance to save.
     */
    public static void startActionCreateTask(Context context, Task task, ResultReceiver receiver) {
        Intent intent = new Intent(context, LocationTaskIntentService.class);
        intent.setAction(ACTION_CREATE_TASK);
        intent.putExtra(EXTRA_TASK, task);
        intent.putExtra(EXTRA_RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

    /**
     * Updates Task and its relation to Location instance to the database.
     * Task must be already saved to the database
     * @param context invoker context.
     * @param task an instance to update.
     */
    public static void startActionUpdateTask(Context context, Task task, ResultReceiver receiver) {
        Intent intent = new Intent(context, LocationTaskIntentService.class);
        intent.setAction(ACTION_UPDATE_TASK);
        intent.putExtra(EXTRA_TASK, task);
        intent.putExtra(EXTRA_RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

    /**
     * Loads bitmaps from external storage.
     * @param context invoker context.
     */
    public static void startActionLoadBitmaps(Context context, ResultReceiver receiver) {
        Intent intent = new Intent(context, LocationTaskIntentService.class);
        intent.setAction(ACTION_LOAD_BITMAPS);
        intent.putExtra(EXTRA_RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

    /**
     * Loads all locations belonging to specified AreaMap
     * @param context invoker context.
     * @param areaMap an AreaMap instance that contains locations.
     */
    public static void startActionLoadAllLocationsInArea(Context context, AreaMap areaMap, ResultReceiver receiver) {
        Intent intent = new Intent(context, LocationTaskIntentService.class);
        intent.setAction(ACTION_LOAD_ALL_LOCATIONS_IN_AREA);
        intent.putExtra(EXTRA_AREA_MAP, areaMap);
        intent.putExtra(EXTRA_RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

    /**
     * Loads a bitmap by file name.
     * @param context invoker context
     * @param fileName file name to load
     * @param external true if file is external and false if internal
     */
    public static void startActionLoadBitmapByFileName(Context context, String fileName, boolean external, ResultReceiver receiver) {
        Intent intent = new Intent(context, LocationTaskIntentService.class);
        intent.setAction(ACTION_LOAD_BITMAP_BY_FILE_NAME);
        intent.putExtra(EXTRA_RESULT_RECEIVER, receiver);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        intent.putExtra(EXTRA_IS_EXTERNAL, external);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CREATE_AREA_MAP.equals(action)) {
                final AreaMap areaMap = intent.getParcelableExtra(EXTRA_AREA_MAP);
                final boolean external = intent.getBooleanExtra(EXTRA_IS_EXTERNAL, false);
                final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
                handleActionCreateAreaMap(areaMap, external, receiver);
            }
            if (ACTION_LOAD_BITMAPS.equals(action)){
                final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
                handleActionLoadBitmaps(receiver);
            }
            if (ACTION_LOAD_ALL_LOCATIONS_IN_AREA.equals(action)){
                final AreaMap areaMap = intent.getParcelableExtra(EXTRA_AREA_MAP);
                final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
                handleActionLoadAllLocationsInArea(areaMap, receiver);
            }
            if (ACTION_LOAD_BITMAP_BY_FILE_NAME.equals(action)){
                final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
                final String fileName = intent.getStringExtra(EXTRA_FILE_NAME);
                final boolean external = intent.getBooleanExtra(EXTRA_IS_EXTERNAL, false);
                handleActionLoadBitmapByFileName(fileName, external, receiver);
            }
            if (ACTION_CREATE_LOCATION.equals(action)){
                final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
                final Location location = intent.getParcelableExtra(EXTRA_LOCATION);
                handleActionCreateLocation(location, receiver);
            }
            if (ACTION_CREATE_TASK.equals(action)){
                final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
                final Task task = intent.getParcelableExtra(EXTRA_TASK);
                handleActionCreateTask(task, receiver);
            }
            if (ACTION_UPDATE_TASK.equals(action)){
                final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
                final Task task = intent.getParcelableExtra(EXTRA_TASK);
                handleActionUpdateTask(task, receiver);
            }
        }
    }

    private void handleActionUpdateTask(Task task, ResultReceiver receiver) {
        TaskLocationRelation relation = task.getTaskLocationRelation();
        ContentValues cv;
        Uri uri;
        Bundle bundle = new Bundle();
        if (relation != null) {
            cv = relation.makeContentValues();
            uri = ContentUris.withAppendedId(LocationTaskContentProvider.URI_ALL_TASKS_LOCATIONS, relation.getId());
            int relationId = getContentResolver().update(uri, cv, null, null);
            Log.d("test", String.format("Saved relation #%d", relationId));
            // TODO pass result
        }
        cv = task.makeContentValues();
        uri = ContentUris.withAppendedId(LocationTaskContentProvider.URI_ALL_TASKS, task.getId());
        int taskId = getContentResolver().update(uri,
                cv, null, null);
        Log.d("test", String.format("Saved relation #%d", taskId));
    }

    private void handleActionCreateTask(Task task, ResultReceiver receiver) {
        ContentValues cv = task.makeContentValues();
        Uri uri = getContentResolver().insert(LocationTaskContentProvider.URI_ALL_TASKS, cv);
        long id = ContentUris.parseId(uri);
        task.setId(id);
        Log.d("test", "Saved Task #"+id);
        TaskLocationRelation relation = task.getTaskLocationRelation();
        Bundle bundle = new Bundle();
        bundle.putLong(RESULT_ID_KEY, id);
        if (relation != null) {
            relation.setTaskId(id);
            cv = relation.makeContentValues();
            uri = getContentResolver().insert(LocationTaskContentProvider.URI_ALL_TASKS_LOCATIONS, cv);
            id = ContentUris.parseId(uri);
            relation.setId(id);
            task.setTaskLocationRelation(relation);
            Log.d("test", String.format("Saved relation between task #%d and location #%d",
                    relation.getTaskId(), relation.getLocationId()));
            bundle.putLong(RESULT_RELATION_KEY, id);
        }
        if (receiver != null){
            receiver.send(RESULT_CODE_INSERTED_TASK, bundle);
        }
    }

    private void handleActionCreateLocation(Location location, ResultReceiver receiver) {
        ContentValues cv = location.makeContentValues();
        Uri uri = getContentResolver().insert(LocationTaskContentProvider.URI_ALL_LOCATIONS, cv);
        long id = ContentUris.parseId(uri);
        location.setId(id);
        Log.d("test", "Saved Location #"+id);
        Bundle bundle = new Bundle();
        bundle.putLong(RESULT_ID_KEY, id);
        if (receiver != null){
            receiver.send(RESULT_CODE_INSERTED_LOCATION, bundle);
        }
    }

    private void handleActionLoadBitmapByFileName(String fileName, boolean external, ResultReceiver receiver) {
        String bitmapFileName;
        int resultCode;
        if (external){
            bitmapFileName = EXTERNAL_DIRECTORY + fileName;
            resultCode = RESULT_CODE_EXTERNAL_BITMAP;
        } else {
            bitmapFileName = getFilesDir().getAbsolutePath() + File.separator + fileName;
            resultCode = RESULT_CODE_INTERNAL_BITMAP;
        }
        Bitmap bitmap = getResizedBitmap(MAX_BITMAP_SIZE, bitmapFileName);
        Bundle bundle = new Bundle();
        bundle.putParcelable(RESULT_BITMAP_KEY, bitmap);
        if (receiver != null) {
            receiver.send(resultCode, bundle);
        }
    }

    private void handleActionLoadAllLocationsInArea(AreaMap areaMap, ResultReceiver receiver) {
        Uri uri = ContentUris.withAppendedId(LocationTaskContentProvider.URI_ALL_LOCATIONS_IN_AREA, areaMap.getId());
        ContentResolver resolver = getContentResolver();
        Cursor areaLocationsCursor = resolver.query(uri, null, null, null, null);
        if (areaLocationsCursor != null) {
            while (areaLocationsCursor.moveToNext()){
                int count = 0;
                Location location = new Location(areaLocationsCursor);
                Cursor taskCountCursor = resolver.query(LocationTaskContentProvider.URI_ALL_LOCATIONS_WITH_TASK_COUNT,
                        null, String.format(Locale.getDefault(), "%s=%d", DbHelper.TasksLocationsFields.LOCATION_ID, location.getId()),
                        null, null);
                if (taskCountCursor != null) {
                    if (taskCountCursor.moveToFirst()) {
                        count = taskCountCursor.getInt(taskCountCursor.getColumnIndex(DbHelper.COUNT));
                    }
                    taskCountCursor.close();
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable(RESULT_LOCATION_KEY, location);
                bundle.putInt(RESULT_COUNT_KEY, count);
                if (receiver != null) {
                    receiver.send(RESULT_CODE_LOCATION_IN_AREA, bundle);
                }
            }
            areaLocationsCursor.close();
        }
    }

    private void handleActionLoadBitmaps(ResultReceiver receiver) {
        final int maxSize = MAX_BITMAP_SIZE;
        if (!isExternalStorageReadable()){
            return;
        }
        for (String filename: getListDirPictures()) {
            Bitmap bitmap = getResizedBitmap(maxSize, EXTERNAL_DIRECTORY + filename);
            if (bitmap == null) continue;
            Bundle bundle = new Bundle();
            bundle.putParcelable(RESULT_BITMAP_KEY, bitmap);
            bundle.putString(RESULT_FILE_NAME_KEY, filename);
            if (receiver != null){
                receiver.send(RESULT_CODE_EXTERNAL_BITMAP, bundle);
            }
        }
    }

    private void handleActionCreateAreaMap(AreaMap areaMap, boolean external, ResultReceiver receiver) {
        try {
            if (external) {
                File externalFile = new File(EXTERNAL_DIRECTORY + areaMap.getPath());
                File internalFile = new File(getFilesDir(), areaMap.getPath());
                copyFile(externalFile, internalFile);
            }
            ContentValues cv = areaMap.makeContentValues();
            Uri uri = getContentResolver().insert(LocationTaskContentProvider.URI_ALL_AREA_MAPS, cv);
            long id = ContentUris.parseId(uri);
            areaMap.setId(id);
            Log.d("test", "Saved Area Map #"+id);
            Bundle bundle = new Bundle();
            bundle.putLong(RESULT_ID_KEY, id);
            if (receiver != null){
                receiver.send(RESULT_CODE_INSERTED_AREA_MAP, bundle);
            }
        } catch (IOException | NullPointerException e) {
            if (receiver != null){
                Bundle bundle = new Bundle();
                bundle.putString(RESULT_ERROR_CAUSE_KEY, getString(R.string.saving_area_map_failure));
                receiver.send(RESULT_CODE_FAILURE, bundle);
            }
        }
    }

    /**
     * @return if we can access external storage
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }


    /**
     * Get a bitmap with adjusted width or height to the max size.
     * Proportion is kept.
     * Returns null if file contains no bitmap.
     * @param maxSize maximum width or height in pixels
     * @param filename file to decode
     * @return resized bitmap
     */
    @Nullable
    private Bitmap getResizedBitmap(int maxSize, String filename) {
        float scaledHeight = maxSize;
        float scaledWidth = maxSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filename);
        if (bitmap == null){
            return null;
        }
        if (bitmap.getHeight() > bitmap.getWidth()) {
            scaledWidth = (float) bitmap.getWidth() / (float) bitmap.getHeight() * maxSize;
        } else {
            scaledHeight = (float) bitmap.getHeight() / (float) bitmap.getWidth() * maxSize;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) scaledWidth, (int) scaledHeight, false);
        bitmap.prepareToDraw();
        return bitmap;
    }

    /**
     * @return list picture storage
     */
    public ArrayList<String> getListDirPictures() {
        ArrayList<String> fileList = new ArrayList<>();
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (file != null && file.isDirectory()){
            Collections.addAll(fileList, file.list());
            return fileList;
        } else {
            return null;
        }
    }

    public void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
