package ua.itstep.android11.kharlamov.locationtask.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Slipstream on 04.09.2016 in Location Task.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String _ID = "_id";
    private static final String DB_NAME = "location_task";
    public static final String COUNT = "cnt";

    public static final class AreaMapsFields {
        public static final String TABLE_NAME = "area_maps";
        // path to image file
        public static final String PATH = "path";
        // actual width and height of area
        public static final String REAL_WIDTH = "width";
        public static final String REAL_HEIGHT = "height";
        // id from buildings table
        public static final String BUILDING_ID = "building_id";
        // brief description of area
        public static final String DESCRIPTION = "desc";
        // floor number: 1 for ground, 0 for none
        public static final String FLOOR = "floor";
        // measure units, e. g. meters, feet
        public static final String UNIT = "unit";
    }

    public static final class TaskFields {
        public static final String TABLE_NAME = "tasks";
        // is accomplished?
        public static final String STATUS = "status";
        // brief description of task
        public static final String DESCRIPTION = "desc";
        // id from locations  table
        public static final String LOCATION_ID = "location_id";
    }

    public static final class LocationFields {
        public static final String TABLE_NAME = "locations";
        // id from area map table
        public static final String AREA_MAP_ID = "area_map_id";
        // x coordinate in the area map
        public static final String LOCAL_X = "local_x";
        // y coordinate in the area map
        public static final String LOCAL_Y = "local_y";
        // brief description
        public static final String DESCRIPTION = "desc";
    }

    /*
     M2M relation between task and location
     */
    public static final class TasksLocationsFields {
        public static final String TABLE_NAME = "tasks_locations";
        // task and location ids
        public static final String TASK_ID = "task_id";
        public static final String LOCATION_ID = "location_id";
        // user's assessment
        public static final String RATING = "rating";
    }

    private static String CREATE_TABLE_TASKS =
            String.format("create table %s(" +
                    "%s integer primary key autoincrement, " +
                    "%s integer," +
                    "%s integer," +
                    "%s text)",
                    TaskFields.TABLE_NAME,
                    _ID,
                    TaskFields.LOCATION_ID,
                    TaskFields.STATUS,
                    TaskFields.DESCRIPTION);

    private static String CREATE_TABLE_AREA_MAPS =
            String.format("create table %s (" +
                    "%s integer primary key autoincrement, " +
                    "%s text, " +
                    "%s real, " +
                    "%s real, " +
                    "%s integer, " +
                    "%s text, " +
                    "%s integer," +
                    "%s text)",
                    AreaMapsFields.TABLE_NAME,
                    _ID,
                    AreaMapsFields.PATH,
                    AreaMapsFields.REAL_WIDTH,
                    AreaMapsFields.REAL_HEIGHT,
                    AreaMapsFields.BUILDING_ID,
                    AreaMapsFields.DESCRIPTION,
                    AreaMapsFields.FLOOR,
                    AreaMapsFields.UNIT
                    );

    private static String CREATE_TABLE_LOCATIONS =
            String.format("create table %s(" +
                    "%s integer primary key autoincrement, " +
                    "%s integer, " +
                    "%s real," +
                    "%s real," +
                    "%s text)",
                    LocationFields.TABLE_NAME,
                    _ID,
                    LocationFields.AREA_MAP_ID,
                    LocationFields.LOCAL_X,
                    LocationFields.LOCAL_Y,
                    LocationFields.DESCRIPTION);

    private static String CREATE_TABLE_TASKS_LOCATIONS =
            String.format("create table %s(" +
                    "%s integer primary key autoincrement, " +
                    "%s integer, " +
                    "%s integer, " +
                    "%s integer)",
                    TasksLocationsFields.TABLE_NAME,
                    _ID,
                    TasksLocationsFields.LOCATION_ID,
                    TasksLocationsFields.TASK_ID,
                    TasksLocationsFields.RATING);

    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (String statement:
             new String[]{
                     CREATE_TABLE_AREA_MAPS,
                     CREATE_TABLE_LOCATIONS,
                     CREATE_TABLE_TASKS,
                     CREATE_TABLE_TASKS_LOCATIONS
             }) {
            sqLiteDatabase.execSQL(statement);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
