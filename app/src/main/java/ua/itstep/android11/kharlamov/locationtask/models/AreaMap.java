package ua.itstep.android11.kharlamov.locationtask.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import ua.itstep.android11.kharlamov.locationtask.db.DbHelper;

/**
 * Created by Slipstream on 04.09.2016 in Location Task.
 */
public class AreaMap implements Parcelable {

    private String mPath;
    private float mRealWidth;
    private float mRealHeight;
    private long mBuildingId;
    private String mDescription;
    private int mFloor;
    private long mId = -1;

    public AreaMap(String path, float realWidth, float realHeight, long buildingId, String description, int floor) {
        this.mPath = path;
        this.mRealWidth = realWidth;
        this.mRealHeight = realHeight;
        this.mBuildingId = buildingId;
        this.mDescription = description;
        this.mFloor = floor;
    }

    public AreaMap(Cursor c) {
        this.mPath = c.getString(c.getColumnIndex(DbHelper.AreaMapsFields.PATH));
        this.mRealWidth = c.getInt(c.getColumnIndex(DbHelper.AreaMapsFields.REAL_WIDTH));
        this.mRealHeight = c.getInt(c.getColumnIndex(DbHelper.AreaMapsFields.REAL_HEIGHT));
        this.mBuildingId = c.getInt(c.getColumnIndex(DbHelper.AreaMapsFields.BUILDING_ID));
        this.mDescription = c.getString(c.getColumnIndex(DbHelper.AreaMapsFields.DESCRIPTION));
        this.mFloor = c.getInt(c.getColumnIndex(DbHelper.AreaMapsFields.FLOOR));
        this.mId = c.getLong(c.getColumnIndex(DbHelper._ID));
    }

    public ContentValues makeContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.AreaMapsFields.PATH, mPath);
        cv.put(DbHelper.AreaMapsFields.REAL_WIDTH, mRealWidth);
        cv.put(DbHelper.AreaMapsFields.REAL_HEIGHT, mRealHeight);
        cv.put(DbHelper.AreaMapsFields.BUILDING_ID, mBuildingId);
        cv.put(DbHelper.AreaMapsFields.DESCRIPTION, mDescription);
        cv.put(DbHelper.AreaMapsFields.FLOOR, mFloor);
        return cv;
    }

    protected AreaMap(Parcel in) {
        this.mPath = in.readString();
        this.mRealWidth = in.readFloat();
        this.mRealHeight = in.readFloat();
        this.mBuildingId = in.readLong();
        this.mDescription = in.readString();
        this.mFloor = in.readInt();
        this.mId = in.readLong();
    }

    public static final Creator<AreaMap> CREATOR = new Creator<AreaMap>() {
        @Override
        public AreaMap createFromParcel(Parcel in) {
            return new AreaMap(in);
        }

        @Override
        public AreaMap[] newArray(int size) {
            return new AreaMap[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mPath);
        parcel.writeFloat(mRealWidth);
        parcel.writeFloat(mRealHeight);
        parcel.writeLong(mBuildingId);
        parcel.writeString(mDescription);
        parcel.writeInt(mFloor);
        parcel.writeLong(mId);
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getId() {
        return mId;
    }

    public String getPath() {
        return mPath;
    }

    public float getRealWidth() {
        return mRealWidth;
    }

    public float getRealHeight() {
        return mRealHeight;
    }

    public long getBuildingId() {
        return mBuildingId;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getFloor() {
        return mFloor;
    }

    @Override
    public String toString() {
        return "AreaMap{" +
                "mPath='" + mPath + '\'' +
                ", mRealWidth=" + mRealWidth +
                ", mRealHeight=" + mRealHeight +
                ", mBuildingId=" + mBuildingId +
                ", mDescription='" + mDescription + '\'' +
                ", mFloor=" + mFloor +
                '}';
    }
}
