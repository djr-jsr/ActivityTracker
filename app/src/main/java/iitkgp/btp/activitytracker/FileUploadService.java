package iitkgp.btp.activitytracker;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.splunk.mint.Mint;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class FileUploadService extends IntentService implements UploadStatusDelegate {

    final static String TAG = ".FileUploadService";

    public FileUploadService() {
        super("FileUploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (isNetworkAvailable(FileUploadService.this)) {
            upload(this);
        }
    }

    public boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    @NonNull
    private UploadNotificationConfig getNotificationConfig() {
        return new UploadNotificationConfig()
                .setIcon(R.drawable.ic_upload)
                .setCompletedIcon(R.drawable.ic_upload_success)
                .setErrorIcon(R.drawable.ic_upload_error)
                .setTitle("Activity Tracker")
                .setInProgressMessage(getString(R.string.uploading))
                .setCompletedMessage(getString(R.string.upload_success))
                .setErrorMessage(getString(R.string.upload_error))
                .setAutoClearOnSuccess(false)
                .setAutoClearOnError(false)
                .setClickIntent(null)
                .setClearOnAction(false)
                .setRingToneEnabled(true);
    }

    public boolean upload(final Context context) {
        try {
            Mint.logEvent("FileUploadService: upload");

            File sdCardRoot = Environment.getExternalStorageDirectory();
            File yourDir = new File(sdCardRoot, this.getApplicationContext().getResources().getString(R.string.to_be_uploaded_file_path));

            MultipartUploadRequest uploadIdReq =
                    new MultipartUploadRequest(context, getString(R.string.server_uri))
                            .setNotificationConfig(getNotificationConfig())
                            .setMaxRetries(2);
            int count_files = 0;

            for (File f : yourDir.listFiles()) {
                if (f.isFile()) {
                    try {
                        String filename = f.getName();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                        Date lastModified = sdf.parse(filename.split("_")[2]);
                        Date today = sdf.parse(sdf.format(new Date()));

                        if (!today.after(lastModified))
                            continue;

                        uploadIdReq.addFileToUpload(f.getPath(), "uploaded_file[]");
                        count_files++;

                    } catch (ParseException | FileNotFoundException e) {
                        e.printStackTrace();
                        Mint.logException(e);
                    }
                }
            }
            try {
                if (count_files > 0)
                    uploadIdReq.setDelegate(this).startUpload();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Mint.logException(e);
            }

        }
        catch (Exception e){
            e.printStackTrace();
            Mint.logException(e);
        }
        return true;
    }

    @Override
    public void onProgress(UploadInfo uploadInfo) {

    }

    @Override
    public void onError(UploadInfo uploadInfo, Exception e) {

    }

    @Override
    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
        for (String i : uploadInfo.getSuccessfullyUploadedFiles()) {
            Log.e(TAG, uploadInfo.getTotalFiles() + " " + i);
            File file = new File(i);
            String strFileName = file.getName();
            move_file(this, strFileName);
        }
    }

    @Override
    public void onCancelled(UploadInfo uploadInfo) {

    }

    public void move_file(Context context, String file_name) {

        File sdCardRoot = Environment.getExternalStorageDirectory();

        File tobeuploadedDir = new File(sdCardRoot, context.getApplicationContext().getResources().getString(R.string.to_be_uploaded_file_path));
        File archiveDir = new File(sdCardRoot, context.getApplicationContext().getResources().getString(R.string.archive_file_path));

        File sourceLocation = new File(tobeuploadedDir, file_name);
        File targetLocation = new File(archiveDir, file_name);

        try {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            //Now delete from ToBeUploaded folder
            if (sourceLocation.exists()) {
                sourceLocation.delete();
            }
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            Mint.logException(e);
        }
    }
}
