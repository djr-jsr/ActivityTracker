
/**
 * Created by sayan on 5/16/2016.
 */
package iitkgp.btp.activitytracker;

import android.app.IntentService;
import android.content.Intent;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DatabaseToFileUploadService extends IntentService {

    public DatabaseToFileUploadService() {
        super("DatabaseToFileUploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DBHelper mydb = new DBHelper(this);
        mydb.toFile();
        mydb.close();
    }
}
