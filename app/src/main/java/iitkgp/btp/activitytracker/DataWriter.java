package iitkgp.btp.activitytracker;

import android.database.Cursor;
import android.os.Environment;

import com.splunk.mint.Mint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
    private static final String heading_format = "%20s\t%10s\t%30s\t%20s\t%20s\t%20s\t%20s\t%20s\t%20s\t%20s\t%20s\t%20s\t%20s\t%20s\t%20s\n";
    private static final String data_format = "%20s\t%10d\t%30s\t%20f\t%20f\t%20f\t%20f\t%20f\t%20f\t%20f\t%20f\t%20f\t%20f\t%20f\t%20f\n";

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
            Mint.logException(e);
        }

        device = deviceId;

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        activityfile = new File(toBeUploadedDir, device + "_training_" + date + "_Activity.txt");

        try {
            if (!activityfile.exists()) {
                if (activityfile.createNewFile()) {
                    FileWriter fw = new FileWriter(activityfile, true);
                    String data = String.format(heading_format, "Activities", "Confidence", "Date", "Acc X", "Acc Y", "Acc Z", "SD Acc X", "SD Acc Y", "SD Acc Z", "Lacc X", "Lacc Y", "Lacc Z", "SD Lacc X", "SD Lacc Y", "SD Lacc Z");
                    fw.write(data);
                    fw.flush();
                    fw.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Fail file");
            Mint.logException(ex);
        }

    }

    public ArrayList<UUID> WriteData(Cursor cs) throws IOException {
        try {
            Mint.logEvent("DataWriter: WriteData");
            FileWriter fw = new FileWriter(activityfile, true);
            ArrayList<UUID> uid = new ArrayList<>();

            cs.moveToPosition(-1);
            while (cs.moveToNext()) {

                UUID id = UUID.fromString(cs.getString(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_UUID)));
                String activity = cs.getString(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_ACTIVITY));
                int confidence = cs.getInt(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_CONFIDENCE));
                String date = cs.getString(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_TIME));
                float accx = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_M_ACCX));
                float accy = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_M_ACCY));
                float accz = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_M_ACCZ));
                float sd_accx = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_SD_ACCX));
                float sd_accy = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_SD_ACCY));
                float sd_accz = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_SD_ACCZ));
                float laccx = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_M_LACCX));
                float laccy = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_M_LACCY));
                float laccz = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_M_LACCZ));
                float sd_laccx = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_SD_LACCX));
                float sd_laccy = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_SD_LACCY));
                float sd_laccz = cs.getFloat(cs.getColumnIndex(DBHelper.ACTIVITY_COLUMN_SD_LACCZ));

                String data = String.format(Locale.US, data_format, activity, confidence, date, accx, accy, accz, sd_accx, sd_accy, sd_accz, laccx, laccy, laccz, sd_laccx, sd_laccy, sd_laccz);
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
            Mint.logException(e);
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
