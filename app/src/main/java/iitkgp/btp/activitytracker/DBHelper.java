package iitkgp.btp.activitytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by sayan on 31/7/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ActivityTracker.db";
    public static final String ACTIVITY_TABLE_NAME = "activities";
    public static final String ACTIVITY_COLUMN_UUID = "uuid";
    public static final String ACTIVITY_COLUMN_IMEI = "imei";
    public static final String ACTIVITY_COLUMN_ACTIVITY = "activity";
    public static final String ACTIVITY_COLUMN_CONFIDENCE = "confidence";
    public static final String ACTIVITY_COLUMN_M_ACCX = "m_acc_x";
    public static final String ACTIVITY_COLUMN_M_ACCY = "m_acc_y";
    public static final String ACTIVITY_COLUMN_M_ACCZ = "m_acc_z";
    public static final String ACTIVITY_COLUMN_SD_ACCX = "sd_acc_x";
    public static final String ACTIVITY_COLUMN_SD_ACCY = "sd_acc_y";
    public static final String ACTIVITY_COLUMN_SD_ACCZ = "sd_acc_z";

    public static final String ACTIVITY_COLUMN_M_LACCX = "m_lacc_x";
    public static final String ACTIVITY_COLUMN_M_LACCY = "m_lacc_y";
    public static final String ACTIVITY_COLUMN_M_LACCZ = "m_lacc_z";
    public static final String ACTIVITY_COLUMN_SD_LACCX = "sd_lacc_x";
    public static final String ACTIVITY_COLUMN_SD_LACCY = "sd_lacc_y";
    public static final String ACTIVITY_COLUMN_SD_LACCZ = "sd_lacc_z";
    public static final String ACTIVITY_COLUMN_TIME = "timestamp";
    public static final String ACTIVITY_COLUMN_UPLOADED = "uploaded";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + ACTIVITY_TABLE_NAME + " ( " +
                ACTIVITY_COLUMN_UUID + " VARCHAR PRIMARY KEY, " +
                ACTIVITY_COLUMN_IMEI + " VARCHAR," +
                ACTIVITY_COLUMN_ACTIVITY + " VARCHAR, " +
                ACTIVITY_COLUMN_CONFIDENCE + " INTEGER, " +
                ACTIVITY_COLUMN_M_ACCX + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_M_ACCY + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_M_ACCZ + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_SD_ACCX + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_SD_ACCY + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_SD_ACCZ + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_M_LACCX + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_M_LACCY + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_M_LACCZ + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_SD_LACCX + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_SD_LACCY + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_SD_LACCZ + " DOUBLE PRECISION, " +
                ACTIVITY_COLUMN_TIME + " VARCHAR, " +
                ACTIVITY_COLUMN_UPLOADED + " BOOLEAN);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ACTIVITY_TABLE_NAME + ";");
        onCreate(sqLiteDatabase);
    }

    public boolean insertActivity(String imei, String activity, int confidence, String date) {
        try {

//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
//            String date = df.format(Calendar.getInstance().getTime());

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ACTIVITY_COLUMN_UUID, UUID.randomUUID().toString());
            contentValues.put(ACTIVITY_COLUMN_IMEI, imei);
            contentValues.put(ACTIVITY_COLUMN_ACTIVITY, activity);
            contentValues.put(ACTIVITY_COLUMN_CONFIDENCE, confidence);
            contentValues.put(ACTIVITY_COLUMN_TIME, date);
            contentValues.put(ACTIVITY_COLUMN_UPLOADED, false);
            db.insert(ACTIVITY_TABLE_NAME, null, contentValues);
            db.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertAccelerometer(double mx, double my, double mz, double sdx, double sdy, double sdz) {
        try {

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            String date = df.format(Calendar.getInstance().getTime());

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ACTIVITY_COLUMN_M_ACCX, mx);
            contentValues.put(ACTIVITY_COLUMN_M_ACCY, my);
            contentValues.put(ACTIVITY_COLUMN_M_ACCZ, mz);
            contentValues.put(ACTIVITY_COLUMN_SD_ACCX, sdx);
            contentValues.put(ACTIVITY_COLUMN_SD_ACCY, sdy);
            contentValues.put(ACTIVITY_COLUMN_SD_ACCZ, sdz);
            db.update(ACTIVITY_TABLE_NAME, contentValues, ACTIVITY_COLUMN_M_ACCX + " ISNULL ", null);
            db.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertLinearAccelerometer(double mx, double my, double mz, double sdx, double sdy, double sdz) {
        try {

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            String date = df.format(Calendar.getInstance().getTime());

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ACTIVITY_COLUMN_M_LACCX, mx);
            contentValues.put(ACTIVITY_COLUMN_M_LACCY, my);
            contentValues.put(ACTIVITY_COLUMN_M_LACCZ, mz);
            contentValues.put(ACTIVITY_COLUMN_SD_LACCX, sdx);
            contentValues.put(ACTIVITY_COLUMN_SD_LACCY, sdy);
            contentValues.put(ACTIVITY_COLUMN_SD_LACCZ, sdz);
            db.update(ACTIVITY_TABLE_NAME, contentValues, ACTIVITY_COLUMN_M_LACCX + " ISNULL ", null);
            db.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void toFile() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<UUID> arr = new ArrayList<>();
        try (Cursor cs = db.query(true, ACTIVITY_TABLE_NAME, null, ACTIVITY_COLUMN_UPLOADED + " = 0 AND " + ACTIVITY_COLUMN_M_ACCX + " NOTNULL AND " + ACTIVITY_COLUMN_M_LACCX + " NOTNULL ", null, null, null, ACTIVITY_COLUMN_TIME, null)) {
            if (cs.getCount() > 0) {
                cs.moveToFirst();
                String IMEI = cs.getString(cs.getColumnIndex(ACTIVITY_COLUMN_IMEI));
                DataWriter dw = new DataWriter(IMEI);
                arr = dw.WriteData(cs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        db = this.getWritableDatabase();
        try {
            for (UUID id : arr) {
                db.delete(ACTIVITY_TABLE_NAME, ACTIVITY_COLUMN_UUID + " = ? ", new String[]{id.toString()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

}
