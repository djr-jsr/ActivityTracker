package iitkgp.btp.activitytracker;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by sayan on 5/16/2016.
 */
public class DataWriter {
    private static File toBeUploadedDir;
    private static File archiveDir;
    private static File newFolder;
    private static File activityfile;
    public static String device;

    public DataWriter(String deviceId) {

        try {
            newFolder = new File(Environment.getExternalStorageDirectory(), "ActivityTracker");
            if (!newFolder.exists()) {
                newFolder.mkdir();
            }
            toBeUploadedDir = new File(newFolder, "Pending");
            if (!toBeUploadedDir.exists()) {
                toBeUploadedDir.mkdir();
            }
            archiveDir = new File(newFolder, "Uploaded");
            if (!archiveDir.exists()) {
                archiveDir.mkdir();
            }
        } catch (Exception e) {
            System.out.println("creating file error" + e.toString());
        }

        device = deviceId;

        long ts = (new Date()).getTime() / 1000 / 100;
        activityfile = new File(toBeUploadedDir, device + "_training_" + ts + "_Activity.txt");

        try {
            if (!activityfile.exists()) {
                if (activityfile.createNewFile()) {
                    FileWriter fw = new FileWriter(activityfile, true);
                    String data = String.format("%20s\t%10s\t%30s\t%20s\t%20s\t%20s\n", "Activities", "Confidence", "Date", "Acceleration X", "Acceleration Y", "Acceleration Z");
                    fw.write(data);
                    fw.flush();
                    fw.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Fail file");
        }

    }

    public ArrayList<UUID> WriteData(Cursor cs) throws IOException {
        try {
            FileWriter fw = new FileWriter(activityfile, true);
            ArrayList<UUID> uid = new ArrayList<>();

            cs.moveToPosition(-1);
            while (cs.moveToNext()) {

                UUID id = UUID.fromString(cs.getString(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_UUID)));
                String activity = cs.getString(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_ACTIVITY));
                int confidence = cs.getInt(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_CONFIDENCE));
                String date = cs.getString(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_TIME));
                float accx = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_ACCX));
                float accy = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_ACCY));
                float accz = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_ACCZ));

                String data = String.format(Locale.US, "%20s\t%10d\t%30s\t%20f\t%20f\t%20f\n", activity, confidence, date, accx, accy, accz);
                fw.write(data);

                uid.add(id);
            }
            fw.flush();
            fw.close();
            cs.close();
            return uid;
        } catch (IOException e) {
            cs.close();
            e.printStackTrace();
            throw e;
        }
    }

//    public static void TestWrite(String str, String fileName) throws IOException {
//        newFolder = new File(Environment.getExternalStorageDirectory(), "ActivityTracker");
//        if (!newFolder.exists()) {
//            newFolder.mkdir();
//        }
//        toBeUploadedDir = new File(newFolder, "Pending");
//        if (!toBeUploadedDir.exists()) {
//            toBeUploadedDir.mkdir();
//        }
//        File file = new File(toBeUploadedDir, fileName);
//        if(!file.exists())
//            file.createNewFile();
//        FileWriter fw = new FileWriter(file, true);
//        fw.write(str + "\n");
//        fw.flush();
//        fw.close();
//    }
}
