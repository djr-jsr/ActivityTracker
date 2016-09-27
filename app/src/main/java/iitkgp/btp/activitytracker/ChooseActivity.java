
/**
 * Created by sayan on 5/16/2016.
 */
package iitkgp.btp.activitytracker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cocosw.bottomsheet.BottomSheet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChooseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_choose);
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String imei = tm.getDeviceId();
        final DBHelper mydb = new DBHelper(ChooseActivity.this);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        final String date = df.format(java.util.Calendar.getInstance().getTime());
        final int confidence = 100;

        new BottomSheet.Builder(this).title("Choose Your Current Activity").sheet(R.menu.menu).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.vehicle:
                        mydb.insertActivity(imei, "In Vehicle", confidence, date);
                        break;
                    case R.id.bicycle:
                        mydb.insertActivity(imei, "On Bicycle", confidence, date);
                        break;
                    case R.id.foot:
                        mydb.insertActivity(imei, "On Foot", confidence, date);
                        break;
                    case R.id.walking:
                        mydb.insertActivity(imei, "Walking", confidence, date);
                        break;
                    case R.id.running:
                        mydb.insertActivity(imei, "Running", confidence, date);
                        break;
                    case R.id.tilting:
                        mydb.insertActivity(imei, "Tilting", confidence, date);
                        break;
                    case R.id.still:
                        mydb.insertActivity(imei, "Still", confidence, date);
                        break;
                    case R.id.others:
                        mydb.insertActivity(imei, "Others", confidence, date);
                        break;
                }
            }
        }).darkTheme().setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.d("On Dismiss:", "Dismissed");
                Intent sensorIntent = new Intent(ChooseActivity.this, SensorService.class);
                startService(sensorIntent);
                finish();
            }
        }).show();
    }
}
