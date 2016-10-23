package ua.itstep.android11.kharlamov.locationtask.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import ua.itstep.android11.kharlamov.locationtask.db.DbHelper;

/**
 * Created by Slipstream on 23.10.2016 in LocationTask.
 */
public class Task implements Parcelable {

    private int mStatus;
    private String mDescription;
    private long mId = -1;

    private TaskLocationRelation mTaskLocationRelation;

    public Task(int status, String description) {
        this.mStatus = status;
        this.mDescription = description;
    }

    public Task(Cursor c){
        this.mDescription = c.getString(c.getColumnIndex(DbHelper.TaskFields.DESCRIPTION));
        this.mStatus = c.getInt(c.getColumnIndex(DbHelper.TaskFields.STATUS));
        this.mId = c.getLong(c.getColumnIndex(DbHelper.TaskFields.STATUS));
    }

    public ContentValues makeContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.TaskFields.STATUS, mStatus);
        cv.put(DbHelper.TaskFields.DESCRIPTION, mDescription);
        return cv;
    }

    protected Task(Parcel in) {
        this.mStatus = in.readInt();
        this.mDescription = in.readString();
        this.mId = in.readLong();
        this.mTaskLocationRelation = in.readParcelable(TaskLocationRelation.class.getClassLoader());
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mStatus);
        parcel.writeString(mDescription);
        parcel.writeLong(mId);
        parcel.writeParcelable(mTaskLocationRelation, PARCELABLE_WRITE_RETURN_VALUE);
    }

    public TaskLocationRelation getTaskLocationRelation() {
        return mTaskLocationRelation;
    }

    public void setTaskLocationRelation(TaskLocationRelation taskLocationRelation) {
        this.mTaskLocationRelation = taskLocationRelation;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getDescription() {
        return mDescription;
    }

    public long getId() {
        return mId;
    }
}
