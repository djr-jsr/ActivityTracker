package iitkgp.btp.activitytracker;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by SnigdhaNUP on 5/16/2016.
 */
public class UploadtoFileServer {
    private String upLoadServerUri;
    private String selectedFilePath;

    int serverResponseCode = 0;

    protected Context context;

    public UploadtoFileServer(Context context){
        this.context = context.getApplicationContext();
    }

    public boolean folderUpload()
    {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        upLoadServerUri=context.getApplicationContext().getResources().getString(R.string.server_uri);

        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot,context.getApplicationContext().getResources().getString(R.string.to_be_uploaded_file_path)); //"Wifi"

        for (File f : yourDir.listFiles()) {
            if (f.isFile()) {
                String name = f.getName();
                // Do your stuff
                //System.out.println(name);
                selectedFilePath = sdCardRoot + context.getApplicationContext().getResources().getString(R.string.to_be_uploaded_file_path) + name;
                //System.out.println(selectedFilePath);

                //button.onClick();
                try {

                    File myFile = new File(selectedFilePath);
                    FileInputStream fileInputStream = new FileInputStream(myFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", selectedFilePath);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + selectedFilePath + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    //Log.i("Bytes read:" , "" + bytesRead);
                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    //Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                    /*InputStream is = conn.getInputStream();
                    int ch;

                    StringBuffer b =new StringBuffer();
                    while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
                    String s=b.toString();
                    Log.i("Response",s);
                    dos.close();*/


                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    //Move the file to the Archive folder if the serverresponse code is 200
                    if(serverResponseCode==200) {
                        move_file(name);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public void move_file(String file_name){

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
            if(sourceLocation.exists()) {
                sourceLocation.delete();
            }
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
