package iitkgp.btp.activitytracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.splunk.mint.Mint;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SensorService extends Service implements SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senGyroscope;
    private Sensor senMagnetometer;
    private Sensor senLinear;
    private Sensor senGravity;
    DBHelper mydb;

    private double acc_x[] = new double[30];
    private double acc_y[] = new double[30];
    private double acc_z[] = new double[30];

    private double lacc_x[] = new double[30];
    private double lacc_y[] = new double[30];
    private double lacc_z[] = new double[30];

    private int acc_count = 30;
    private int lacc_count = 30;

    private float[] gravityValues = null;
    private float[] magneticValues = null;
    private boolean acc_stop = false;
    private boolean lacc_stop = false;

    public SensorService() {
    }

    @Override
    public void onCreate() {
//        acc_x = 0.0f;
//        acc_y = 0.0f;
//        acc_z = 0.0f;
//        count =
        acc_count = 30;
        lacc_count = 30;
        acc_stop = false;
        lacc_stop = false;
        mydb = new DBHelper(this);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // senGyroscope = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        senGravity = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        senMagnetometer = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        senLinear = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if (senAccelerometer != null) {
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
//        if (senGyroscope != null) {
//            senSensorManager.registerListener(this, senGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
//        }
        if (senGravity != null) {
            senSensorManager.registerListener(this, senGravity, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (senMagnetometer != null) {
            senSensorManager.registerListener(this, senMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (senLinear != null) {
            senSensorManager.registerListener(this, senLinear, SensorManager.SENSOR_DELAY_NORMAL);
        }
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

        if(acc_stop && lacc_stop){
            stopSelf();
        }

        if ((gravityValues != null) && (magneticValues != null)
                && (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)) {
            acc_count--;
            float[] earthAcc = transform(sensorEvent);
            final float x = earthAcc[0];
            final float y = earthAcc[1];
            final float z = earthAcc[2];

            if (acc_count >= 0 && acc_count < 30) {
                acc_x[acc_count] = x;
                acc_y[acc_count] = y;
                acc_z[acc_count] = z;
            }

            if (acc_count <= 0) {

                StandardDeviation sd = new StandardDeviation();
                Mean m = new Mean();

                double m_acc_x = m.evaluate(acc_x);
                double m_acc_y = m.evaluate(acc_y);
                double m_acc_z = m.evaluate(acc_z);

                double sd_acc_x = sd.evaluate(acc_x);
                double sd_acc_y = sd.evaluate(acc_y);
                double sd_acc_z = sd.evaluate(acc_z);

                Mint.logEvent("SensorService: onSensorChanged: Accelerometer");
                mydb.insertAccelerometer(m_acc_x, m_acc_y, m_acc_z, sd_acc_x, sd_acc_y, sd_acc_z);

                Handler h = new Handler(SensorService.this.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(SensorService.this, "Acc: " + x + ", " + y + ", " + z, Toast.LENGTH_SHORT).show();
                    }
                });
                acc_stop = true;
            }
        }
        if ((gravityValues != null) && (magneticValues != null)
                && (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)) {
            lacc_count--;
            float[] earthAcc = transform(sensorEvent);
            final float x = earthAcc[0];
            final float y = earthAcc[1];
            final float z = earthAcc[2];

            if (lacc_count >= 0 && lacc_count < 30) {
                lacc_x[lacc_count] = x;
                lacc_y[lacc_count] = y;
                lacc_z[lacc_count] = z;
            }

            if (lacc_count <= 0) {

                StandardDeviation sd = new StandardDeviation();
                Mean m = new Mean();

                double m_acc_x = m.evaluate(lacc_x);
                double m_acc_y = m.evaluate(lacc_y);
                double m_acc_z = m.evaluate(lacc_z);

                double sd_acc_x = sd.evaluate(lacc_x);
                double sd_acc_y = sd.evaluate(lacc_y);
                double sd_acc_z = sd.evaluate(lacc_z);

                Mint.logEvent("SensorService: onSensorChanged: Linear Accelerometer");
                mydb.insertLinearAccelerometer(m_acc_x, m_acc_y, m_acc_z, sd_acc_x, sd_acc_y, sd_acc_z);

                Handler h = new Handler(SensorService.this.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(SensorService.this, "Acc: " + x + ", " + y + ", " + z, Toast.LENGTH_SHORT).show();
                    }
                });
                lacc_stop = true;
            }
        }
        if (mySensor.getType() == Sensor.TYPE_GRAVITY) {
            gravityValues = sensorEvent.values;
        }
        if (mySensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticValues = sensorEvent.values;
        }
    }

    private float[] transform(SensorEvent sensorEvent) {

        float[] deviceRelativeAcceleration = new float[4];
        deviceRelativeAcceleration[0] = sensorEvent.values[0];
        deviceRelativeAcceleration[1] = sensorEvent.values[1];
        deviceRelativeAcceleration[2] = sensorEvent.values[2];
        deviceRelativeAcceleration[3] = 0;

        // Change the device relative acceleration values to earth relative values
        // X axis -> East
        // Y axis -> North Pole
        // Z axis -> Sky

        float[] R = new float[16], I = new float[16], earthAcc = new float[16];

        SensorManager.getRotationMatrix(R, I, gravityValues, magneticValues);

        float[] inv = new float[16];

        Matrix.invertM(inv, 0, R, 0);
        Matrix.multiplyMV(earthAcc, 0, inv, 0, deviceRelativeAcceleration, 0);
        //Log.d("Acceleration", "Values: (" + earthAcc[0] + ", " + earthAcc[1] + ", " + earthAcc[2] + ")");
        return earthAcc;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
