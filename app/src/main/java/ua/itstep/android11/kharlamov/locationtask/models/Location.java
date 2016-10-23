package ua.itstep.android11.kharlamov.locationtask.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;

import ua.itstep.android11.kharlamov.locationtask.db.DbHelper;

/**
 * Created by Slipstream on 04.10.2016 in LocationTask.
 */
public class Location implements Parcelable {

    private int mAreaMapId;
    private float mLocalX;
    private float mLocalY;
    private String mDescription;
    private long mId = -1;

    public Location(int areaMapId, float localX, float localY, String description) {
        this.mAreaMapId = areaMapId;
        this.mLocalX = localX;
        this.mLocalY = localY;
        this.mDescription = description;
    }

    public Location(Cursor c){
        this.mAreaMapId = c.getInt(c.getColumnIndex(DbHelper.LocationFields.AREA_MAP_ID));
        this.mLocalX = c.getFloat(c.getColumnIndex(DbHelper.LocationFields.LOCAL_X));
        this.mLocalY = c.getFloat(c.getColumnIndex(DbHelper.LocationFields.LOCAL_Y));
        this.mDescription = c.getString(c.getColumnIndex(DbHelper.LocationFields.DESCRIPTION));
        this.mId = c.getLong(c.getColumnIndex(DbHelper._ID));
    }

    public ContentValues makeContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.LocationFields.AREA_MAP_ID, mAreaMapId);
        cv.put(DbHelper.LocationFields.LOCAL_X, mLocalX);
        cv.put(DbHelper.LocationFields.LOCAL_Y, mLocalY);
        cv.put(DbHelper.LocationFields.DESCRIPTION, mDescription);
        return cv;
    }

    protected Location(Parcel in) {
        this.mAreaMapId = in.readInt();
        this.mLocalX = in.readFloat();
        this.mLocalY = in.readFloat();
        this.mDescription = in.readString();
        this.mId = in.readLong();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mAreaMapId);
        parcel.writeFloat(mLocalX);
        parcel.writeFloat(mLocalY);
        parcel.writeString(mDescription);
        parcel.writeLong(mId);
    }

    public int getAreaMapId() {
        return mAreaMapId;
    }

    public float getLocalX() {
        return mLocalX;
    }

    public float getLocalY() {
        return mLocalY;
    }

    public String getDescription() {
        return mDescription;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public void setDescription(String  description) {
        this.mDescription = description;
    }
}
