package com.kalis.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gcm.GCMRegistrar;
import com.kalis.R;
import com.kalis.ServerTask;
import com.kalis.keys.KeySource;
import com.kalis.log.LogSystem;
import com.kalis.thread.ChangeActivityThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SplashScreenActivity extends AppCompatActivity {

    private ImageView wave;
    private SQLiteDatabase database;
    private ProgressBar progressBar;

    AsyncTask<Void, Void, Void> gcmRegisterTask;

    final BroadcastReceiver handleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String newMessage = intent.getExtras().getString(ServerTask.EXTRA_MESSAGE);
                    Log.i(ServerTask.TAG, newMessage);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = (ProgressBar) findViewById(R.id.splashscreenLoading);
        processCopy();
        xuLyDangKyGCMServer();
        new ChangeActivityThread().secToGoMainAct(2, this);
//        progressBar.setVisibility(View.GONE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    protected void xuLyDangKyGCMServer() {
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        //msg = (TextView) findViewById(R.id.display);

        registerReceiver(handleMessageReceiver,
                new IntentFilter(ServerTask.DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, ServerTask.SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                //msg.append(getString(R.string.already_registered) + "\n");
                Log.i(ServerTask.TAG, "đã đăng ký :\n" + regId+"\n Thành công");
                final Context context = this;
                gcmRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerTask.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        gcmRegisterTask = null;
                    }

                };
                gcmRegisterTask.execute(null, null, null);
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                gcmRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerTask.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        gcmRegisterTask = null;
                    }

                };
                gcmRegisterTask.execute(null, null, null);
            }
        }
    }

    private void processCopy() {
        //private app
        File dbFile = getDatabasePath("CatsaShop.sqlite");

        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
            } catch (Exception e) {
                LogSystem.E(e.toString());
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        try{
            if(handleMessageReceiver!=null)
                unregisterReceiver(handleMessageReceiver);
        }catch(Exception e)
        {

        }
        super.onDestroy();

    }


    private String getDatabasePath() {
        return getApplicationInfo().dataDir + KeySource.DB_PATH_SUFFIX + KeySource.DATABASE_NAME;
    }

    public void CopyDataBaseFromAsset() {
        try {
            InputStream myInput;
            myInput = getAssets().open(KeySource.DATABASE_NAME);
            // Path to the just created empty db
            String outFileName = getDatabasePath();
            // if the path doesn't exist first, create it
            File f = new File(getApplicationInfo().dataDir + KeySource.DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}