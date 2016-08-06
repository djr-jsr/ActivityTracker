package iitkgp.btp.activitytracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SensorService extends Service implements SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senGyroscope;
    private Sensor senMagnetometer;
    DBHelper mydb;

    public SensorService() {
    }

    @Override
    public void onCreate() {
        mydb = new DBHelper(this);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        senGyroscope = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//        senMagnetometer = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (senAccelerometer != null) {
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
//        if (senGyroscope != null){
//            senSensorManager.registerListener(this, senGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
//        }
//        if(senMagnetometer != null){
//            senSensorManager.registerListener(this, senMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
//        }
        Log.e("onCreate: ", "Registered!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        senSensorManager.unregisterListener(this);
        Log.e("onDestroy: ", "Unregistered");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        String date = df.format(java.util.Calendar.getInstance().getTime());

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float x = sensorEvent.values[0];
            final float y = sensorEvent.values[1];
            final float z = sensorEvent.values[2];

            mydb.insertAccelerometer(x, y, z);

            Handler h = new Handler(SensorService.this.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    // Toast.makeText(SensorService.this, "Acc: " + x + ", " + y + ", " + z, Toast.LENGTH_SHORT).show();
                }
            });
        }
//        if(mySensor.getType() == Sensor.TYPE_GYROSCOPE){
//            final float x = sensorEvent.values[0];
//            final float y = sensorEvent.values[1];
//            final float z = sensorEvent.values[2];
//
//            //mydb.insertAccelerometer(x, y, z);
//            try {
//                DataWriter.TestWrite(x + " " + y + " " + z + " " + date, "Gyroscope.txt");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Handler h = new Handler(SensorService.this.getMainLooper());
//            h.post(new Runnable() {
//                @Override
//                public void run() {
//                    // Toast.makeText(SensorService.this, "Acc: " + x + ", " + y + ", " + z, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//        if(mySensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
//            final float x = sensorEvent.values[0];
//            final float y = sensorEvent.values[1];
//            final float z = sensorEvent.values[2];
//
//            //mydb.insertAccelerometer(x, y, z);
//            try {
//                DataWriter.TestWrite(x + " " + y + " " + z + " " + date, "Magnetometer.txt");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Handler h = new Handler(SensorService.this.getMainLooper());
//            h.post(new Runnable() {
//                @Override
//                public void run() {
//                    // Toast.makeText(SensorService.this, "Acc: " + x + ", " + y + ", " + z, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }

        stopSelf();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
