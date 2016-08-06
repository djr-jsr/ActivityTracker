package iitkgp.btp.activitytracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by SnigdhaNUP on 5/16/2016.
 */
public class FileUploadService extends Service {
    private MyThread myythread;
    public boolean isRunning = false;
    long interval;
    //String upload_interval;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate(){
        //System.out.println("[FileUpload] Service getting started...");
        //upload_interval = getResources().getString(R.string.upload_interval);
        interval = 1000 * 60 * 1;
        //Integer.parseInt(upload_interval);
        super.onCreate();
        myythread = new MyThread(interval);
    }

    @Override
    public synchronized void onDestroy(){
        //System.out.println("[FileUpload] Service getting stopped...");
        isRunning = false;
        super.onDestroy();
        if(!isRunning){
            myythread.interrupt();
            myythread = null;
        }
    }

    @Override
    public synchronized int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        if(!isRunning){
            myythread.start();
            isRunning = true;
        }
        //System.out.println("in onstartcommand");
        return START_STICKY;
    }

    class MyThread extends Thread{
        long interval;
        public MyThread(long interval){
            this.interval = interval;
            //System.out.println("MyThread started");
        }
        @Override
        public void run(){
            while(isRunning){
                //System.out.println("[FileUpload] Service running");
                try{
                    dataUpload();
                    Thread.sleep(interval);
                }
                catch(InterruptedException e){
                    isRunning = false;
                    e.printStackTrace();
                }
            }
        }

        public void dataUpload(){
            if(checkInternetConnection()) {
                boolean upload_status = new UploadtoFileServer(getApplicationContext()).folderUpload();
                //System.out.println("upload_status:" + upload_status);
            }
        }
    }

    private boolean checkInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getActiveNetworkInfo();
        //getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if((mWifi != null) && (mWifi.getType()== ConnectivityManager.TYPE_WIFI)){
            //System.out.println("connection ok");
            return true;
        }
        else{
            return false;
        }
    }
}
