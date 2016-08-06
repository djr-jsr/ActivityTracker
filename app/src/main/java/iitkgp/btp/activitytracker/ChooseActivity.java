package iitkgp.btp.activitytracker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cocosw.bottomsheet.BottomSheet;

public class ChooseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_choose);
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String imei = tm.getDeviceId();
        final DBHelper mydb = new DBHelper(ChooseActivity.this);

        final int confidence = 100;

        new BottomSheet.Builder(this).title("Choose Your Current Activity").sheet(R.menu.menu).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.vehicle:
                        mydb.insertActivity(imei, "In Vehicle", confidence);
                        break;
                    case R.id.bicycle:
                        mydb.insertActivity(imei, "On Bicycle", confidence);
                        break;
                    case R.id.foot:
                        mydb.insertActivity(imei, "On Foot", confidence);
                        break;
                    case R.id.walking:
                        mydb.insertActivity(imei, "Walking", confidence);
                        break;
                    case R.id.running:
                        mydb.insertActivity(imei, "Running", confidence);
                        break;
                    case R.id.tilting:
                        mydb.insertActivity(imei, "Tilting", confidence);
                        break;
                    case R.id.still:
                        mydb.insertActivity(imei, "Still", confidence);
                        break;
                    case R.id.others:
                        mydb.insertActivity(imei, "Others", confidence);
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
