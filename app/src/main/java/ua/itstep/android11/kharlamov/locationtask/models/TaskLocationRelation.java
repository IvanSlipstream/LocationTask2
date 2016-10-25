package ua.itstep.android11.kharlamov.locationtask.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import ua.itstep.android11.kharlamov.locationtask.db.DbHelper;

/**
 * Created by Slipstream on 23.10.2016 in LocationTask.
 */
public class TaskLocationRelation implements Parcelable {

    private long mLocationId;
    private long mTaskId;
    private int mRating;
    private long mId = -1;

    protected TaskLocationRelation(Parcel in) {
        this.mLocationId = in.readLong();
        this.mTaskId = in.readLong();
        this.mRating = in.readInt();
        this.mId = in.readLong();
    }

    public TaskLocationRelation(int rating, long locationId) {
        this.mRating = rating;
        this.mLocationId = locationId;
    }

    public TaskLocationRelation(Cursor c) {
        this.mId = c.getLong(c.getColumnIndex(DbHelper._ID));
        this.mLocationId = c.getLong(c.getColumnIndex(DbHelper.TasksLocationsFields.LOCATION_ID));
        this.mRating = c.getInt(c.getColumnIndex(DbHelper.TasksLocationsFields.RATING));
        this.mTaskId = c.getLong(c.getColumnIndex(DbHelper.TasksLocationsFields.TASK_ID));
    }

    public ContentValues makeContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.TasksLocationsFields.LOCATION_ID, mLocationId);
        cv.put(DbHelper.TasksLocationsFields.RATING, mRating);
        cv.put(DbHelper.TasksLocationsFields.TASK_ID, mTaskId);
        return cv;
    }

    public static final Creator<TaskLocationRelation> CREATOR = new Creator<TaskLocationRelation>() {
        @Override
        public TaskLocationRelation createFromParcel(Parcel in) {
            return new TaskLocationRelation(in);
        }

        @Override
        public TaskLocationRelation[] newArray(int size) {
            return new TaskLocationRelation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mLocationId);
        parcel.writeLong(mTaskId);
        parcel.writeInt(mRating);
        parcel.writeLong(mId);
    }

    public void setId(long id) {
        this.mId = id;
    }

    public void setTaskId(long taskId) {
        this.mTaskId = taskId;
    }

    public long getLocationId() {
        return mLocationId;
    }

    public long getTaskId() {
        return mTaskId;
    }

    public int getRating() {
        return mRating;
    }

    public long getId() {
        return mId;
    }

    public void setRating(int rating) {
        this.mRating = rating;
    }
}
