package ua.itstep.android11.kharlamov.locationtask.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import ua.itstep.android11.kharlamov.locationtask.db.DbHelper;

public class LocationTaskContentProvider extends ContentProvider {

    private SQLiteDatabase databaseRead;
    private SQLiteDatabase databaseWrite;

    public static final String URI_AUTHORITY = "ua.itstep.android11.kharlamov.locationtask";

    private static final int CODE_ALL_AREA_MAPS = 1;
    private static final int CODE_ALL_LOCATIONS = 2;
    private static final int CODE_ALL_TASKS = 3;
    private static final int CODE_ALL_LOCATIONS_IN_AREA = 4;
    private static final int CODE_ALL_LOCATIONS_WITH_TASK_COUNT = 5;
    private static final int CODE_ALL_TASKS_IN_LOCATION = 6;
    private static final int CODE_ALL_TASKS_LOCATIONS = 7;
    private static final String SUFFIX_ALL_AREA_MAPS = "/all_area_maps";
    private static final String SUFFIX_ALL_LOCATIONS = "/all_locations";
    private static final String SUFFIX_ALL_TASKS = "/all_tasks";
    private static final String SUFFIX_ALL_LOCATIONS_IN_AREA = "/all_locations_in_area";
    private static final String SUFFIX_ALL_LOCATIONS_WITH_TASK_COUNT = "/all_locations_with_task_count";
    private static final String SUFFIX_ALL_TASKS_IN_LOCATION = "/all_tasks_in_location";
    private static final String SUFFIX_ALL_TASKS_LOCATIONS = "/all_tasks_locations";

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final Uri URI_ALL_AREA_MAPS = Uri.parse("content://"+URI_AUTHORITY+SUFFIX_ALL_AREA_MAPS);
    public static final Uri URI_ALL_LOCATIONS = Uri.parse("content://"+URI_AUTHORITY+SUFFIX_ALL_LOCATIONS);
    public static final Uri URI_ALL_TASKS = Uri.parse("content://"+URI_AUTHORITY+SUFFIX_ALL_TASKS);
    public static final Uri URI_ALL_TASKS_LOCATIONS = Uri.parse("content://"+URI_AUTHORITY+SUFFIX_ALL_TASKS_LOCATIONS);
    public static final Uri URI_ALL_LOCATIONS_IN_AREA = Uri.parse("content://"+URI_AUTHORITY+SUFFIX_ALL_LOCATIONS_IN_AREA);
    public static final Uri URI_ALL_LOCATIONS_WITH_TASK_COUNT = Uri.parse("content://"+URI_AUTHORITY+SUFFIX_ALL_LOCATIONS_WITH_TASK_COUNT);
    public static final Uri URI_ALL_TASKS_IN_LOCATION = Uri.parse("content://"+URI_AUTHORITY+SUFFIX_ALL_TASKS_IN_LOCATION);

    static {
        sUriMatcher.addURI(URI_AUTHORITY, SUFFIX_ALL_AREA_MAPS, CODE_ALL_AREA_MAPS);
        sUriMatcher.addURI(URI_AUTHORITY, SUFFIX_ALL_LOCATIONS, CODE_ALL_LOCATIONS);
        sUriMatcher.addURI(URI_AUTHORITY, SUFFIX_ALL_TASKS, CODE_ALL_TASKS);
        sUriMatcher.addURI(URI_AUTHORITY, SUFFIX_ALL_TASKS_LOCATIONS, CODE_ALL_TASKS_LOCATIONS);
        sUriMatcher.addURI(URI_AUTHORITY, SUFFIX_ALL_LOCATIONS_IN_AREA+"/#", CODE_ALL_LOCATIONS_IN_AREA);
        sUriMatcher.addURI(URI_AUTHORITY, SUFFIX_ALL_LOCATIONS_WITH_TASK_COUNT, CODE_ALL_LOCATIONS_WITH_TASK_COUNT);
        sUriMatcher.addURI(URI_AUTHORITY, SUFFIX_ALL_TASKS_IN_LOCATION+"/#", CODE_ALL_TASKS_IN_LOCATION);
        sUriMatcher.addURI(URI_AUTHORITY, SUFFIX_ALL_TASKS+"/#", CODE_ALL_TASKS);
        sUriMatcher.addURI(URI_AUTHORITY, SUFFIX_ALL_TASKS_LOCATIONS+"/#", CODE_ALL_TASKS_LOCATIONS);
    }

    public LocationTaskContentProvider() {
    }

    @Override
    public boolean onCreate() {
        DbHelper dbHelper = new DbHelper(getContext());
        databaseWrite = dbHelper.getWritableDatabase();
        databaseRead = dbHelper.getReadableDatabase();
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id;
        Uri insertUri = null;
        switch (sUriMatcher.match(uri)){
            case CODE_ALL_AREA_MAPS:
                id = databaseWrite.insert(DbHelper.AreaMapsFields.TABLE_NAME, null, values);
                insertUri = ContentUris.withAppendedId(uri, id);
                break;
            case CODE_ALL_LOCATIONS:
                id = databaseWrite.insert(DbHelper.LocationFields.TABLE_NAME, null, values);
                insertUri = ContentUris.withAppendedId(uri, id);
                break;
            case CODE_ALL_TASKS:
                id = databaseWrite.insert(DbHelper.TaskFields.TABLE_NAME, null, values);
                insertUri = ContentUris.withAppendedId(uri, id);
                break;
            case CODE_ALL_TASKS_LOCATIONS:
                id = databaseWrite.insert(DbHelper.TasksLocationsFields.TABLE_NAME, null, values);
                insertUri = ContentUris.withAppendedId(uri, id);
                break;
            case CODE_ALL_LOCATIONS_IN_AREA:
            case CODE_ALL_TASKS_IN_LOCATION:
                throw new UnsupportedOperationException("This is a read-only URI!");
        }
        if (getContext() != null && insertUri != null){
            getContext().getContentResolver().notifyChange(insertUri, null);
        }
        return insertUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        long id;
        switch (sUriMatcher.match(uri)){
            case CODE_ALL_AREA_MAPS:
                return databaseRead.query(DbHelper.AreaMapsFields.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
            case CODE_ALL_LOCATIONS:
                return databaseRead.query(DbHelper.LocationFields.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
            case CODE_ALL_TASKS:
                return databaseRead.query(DbHelper.TaskFields.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
            case CODE_ALL_LOCATIONS_IN_AREA:
                id = ContentUris.parseId(uri);
                if (id != -1) {
                    return databaseRead.query(DbHelper.LocationFields.TABLE_NAME, projection,
                            statementConjunction(selection, String.format(Locale.getDefault(), "%s=%d", DbHelper.LocationFields.AREA_MAP_ID, id)),
                            selectionArgs, null, null, sortOrder);
                } else {
                    return null;
                }
            case CODE_ALL_LOCATIONS_WITH_TASK_COUNT:
                return databaseRead.query(DbHelper.TasksLocationsFields.TABLE_NAME,
                        new String[]{"count(*) as "+DbHelper.COUNT, DbHelper.TasksLocationsFields.LOCATION_ID},
                        selection, selectionArgs, DbHelper.TasksLocationsFields.LOCATION_ID, null, sortOrder);
            case CODE_ALL_TASKS_IN_LOCATION:
                id = ContentUris.parseId(uri);
                if (id != -1) {
                    return databaseRead.query(DbHelper.TasksLocationsFields.TABLE_NAME, projection,
                            statementConjunction(selection, String.format(Locale.getDefault(), "%s=%d", DbHelper.TasksLocationsFields.LOCATION_ID, id)),
                            selectionArgs, null, null, sortOrder);
                }
            default:
                return null;
        }

    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        long id;
        long result = -1;
        switch (sUriMatcher.match(uri)){
            case CODE_ALL_TASKS:
                id = ContentUris.parseId(uri);
                if (id != -1) {
                    result = databaseWrite.update(DbHelper.TaskFields.TABLE_NAME, values,
                            statementConjunction(selection, String.format(Locale.getDefault(), "%s=%d", DbHelper._ID, id)),
                            null);
                }
            case CODE_ALL_TASKS_LOCATIONS:
                id = ContentUris.parseId(uri);
                if (id != -1) {
                    result = databaseWrite.update(DbHelper.TasksLocationsFields.TABLE_NAME, values,
                            statementConjunction(selection, String.format(Locale.getDefault(), "%s=%d", DbHelper._ID, id)),
                            null);
                }
        }
        if (getContext() != null){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return (int) result;
    }

    protected String statementConjunction(String... statement){
        ArrayList<String> statementNonEmpty = new ArrayList<>(Arrays.asList(statement));
        int i = 0;
        while (statementNonEmpty.size() > i){
            if (statementNonEmpty.get(i) == null || statementNonEmpty.get(i).trim().equals("")){
                statementNonEmpty.remove(i);
            } else {
                i++;
            }
        }
        String result = "";
        if (statementNonEmpty.size()>0){
            result = result.concat(statementNonEmpty.get(0));
        }
        for (int j=1; j<statementNonEmpty.size(); j++){
                result = result.concat(" and "+statementNonEmpty.get(j));
        }
        return result;
    }
}
