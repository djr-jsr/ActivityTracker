/**
 * Created by sayan on 5/16/2016.
 */
package iitkgp.btp.activitytracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.splunk.mint.Mint;

import java.util.Calendar;

public class BootService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mApiClient;
    private DevicePolicyManager mDPM;
    private ComponentName mDA;

    public BootService() {
    }

    private boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDA);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Mint.logEvent("BootService: onCreate");

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDA = new ComponentName(this, DeviceOwnerReceiver.class);

        if (isActiveAdmin()) {

            // Set the alarm to start at approximately 2:00 p.m.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 10);

            Intent i = new Intent(this, DatabaseToFileUploadService.class);
            PendingIntent mAlarmSender = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, 60000, mAlarmSender);

            Intent ii = new Intent(this, FileUploadService.class);
            PendingIntent pi = PendingIntent.getService(this, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pi);

            mApiClient = new GoogleApiClient.Builder(this)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mApiClient.connect();

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 60000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
