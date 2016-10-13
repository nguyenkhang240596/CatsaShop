package com.kalis.dialog;

import android.app.Activity;


/**
 * Created by Kalis on 12/28/2015.
 */
public class AlertDialog  {

    private Activity activity;

    public AlertDialog(Activity activity) {
        this.activity = activity;
    }

    public void dialogOnlyRead(String title,String body)
    {
        android.app.AlertDialog.Builder builder = new  android.app.AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(body);

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
       // activity.finish();
    }


}
