package iitkgp.btp.activitytracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals("android.intent.action.BOOT_COMPLETED")) {
                    Toast.makeText(context, "Boot Completed", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(context, BootService.class);
                    context.startService(i);
                } else if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    Toast.makeText(context, "Network Changed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
