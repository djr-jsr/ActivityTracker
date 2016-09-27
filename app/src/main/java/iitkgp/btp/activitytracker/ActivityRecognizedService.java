/**
 * Created by sayan on 5/16/2016.
 */
package iitkgp.btp.activitytracker;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.splunk.mint.Mint;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ActivityRecognizedService extends IntentService {

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Mint.flush();
        Log.e("ActivityRecognizedSrvc", "onHandleIntent");
        Mint.logEvent("ActivityRecognizedService: onHandleIntent");

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        //DataWriter obj = new DataWriter(imei);
        Handler h = new Handler(ActivityRecognizedService.this.getMainLooper());
        DBHelper mydb = new DBHelper(ActivityRecognizedService.this);
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

//            if (result.getActivityConfidence(DetectedActivity.UNKNOWN) > 50 ||
//                    result.getMostProbableActivity().getType() == DetectedActivity.UNKNOWN ||
//                    result.getMostProbableActivity().getConfidence() < 50) {
//
//                Intent i = new Intent(this, ChooseActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
//            } else {
            try {
                handleDetectedActivities(result.getProbableActivities(), h, mydb, imei);
                Intent sensorIntent = new Intent(this, SensorService.class);
                startService(sensorIntent);
            } catch (IOException e) {
                e.printStackTrace();
                Mint.logException(e);
            }
//            }
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities, Handler h, DBHelper obj, String imei) throws IOException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        String date = df.format(java.util.Calendar.getInstance().getTime());
        for (final DetectedActivity activity : probableActivities) {
            if (activity.getConfidence() < 30)
                continue;
            switch (activity.getType()) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e("ActivityRecognition", "In Vehicle: " + activity.getConfidence());

                    h.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(ActivityRecognizedService.this, "In Vehicle: " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
                        }
                    });

//                        DataWriter.TestWrite("In Vehicle: " + activity.getConfidence() + " " + date, "Activity.txt");
                    obj.insertActivity(imei, "In Vehicle", activity.getConfidence(), date);
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e("ActivityRecognition", "On Bicycle: " + activity.getConfidence());

                    h.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(ActivityRecognizedService.this, "On Bicycle: " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
                        }
                    });

//                        DataWriter.TestWrite("On Bicycle: " + activity.getConfidence() + " " + date, "Activity.txt");
                    obj.insertActivity(imei, "On Bicycle", activity.getConfidence(), date);
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e("ActivityRecognition", "On Foot: " + activity.getConfidence());

                    h.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(ActivityRecognizedService.this, "On Foot: " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
                        }
                    });

//                        DataWriter.TestWrite("On Foot: " + activity.getConfidence() + " " + date, "Activity.txt");
                    obj.insertActivity(imei, "On Foot", activity.getConfidence(), date);
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e("ActivityRecognition", "Running: " + activity.getConfidence());

                    h.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(ActivityRecognizedService.this, "Running: " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
                        }
                    });

//                        DataWriter.TestWrite("Running: " + activity.getConfidence() + " " + date, "Activity.txt");
                    obj.insertActivity(imei, "Running", activity.getConfidence(), date);
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e("ActivityRecognition", "Still: " + activity.getConfidence());

                    h.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(ActivityRecognizedService.this, "Still: " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
                        }
                    });

//                        DataWriter.TestWrite("Still: " + activity.getConfidence() + " " + date, "Activity.txt");
                    obj.insertActivity(imei, "Still", activity.getConfidence(), date);
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e("ActivityRecognition", "Tilting: " + activity.getConfidence());

                    h.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(ActivityRecognizedService.this, "Tilting: " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
                        }
                    });

//                        DataWriter.TestWrite("Tilting: " + activity.getConfidence() + " " + date, "Activity.txt");
                    obj.insertActivity(imei, "Tilting", activity.getConfidence(), date);
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e("ActivityRecognition", "Walking: " + activity.getConfidence());

                    h.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(ActivityRecognizedService.this, "Walking: " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
                        }
                    });

//                        DataWriter.TestWrite("Walking: " + activity.getConfidence() + " " + date, "Activity.txt");
                    obj.insertActivity(imei, "Walking", activity.getConfidence(), date);
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e("ActivityRecognition", "Unknown: " + activity.getConfidence());

                    h.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(ActivityRecognizedService.this, "Unknown: " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
                        }
                    });

//                        DataWriter.TestWrite("Unknown: " + activity.getConfidence() + " " + date, "Activity.txt");
                    obj.insertActivity(imei, "Unknown", activity.getConfidence(), date);
                    break;
                }
            }
        }
    }
}
