package com.kalis.dialog;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Kalis on 12/29/2015.
 */
public class MyToast {

    public static void toastLong(Activity activity,String msg)
    {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }


    public static void toastShort(Activity activity,String msg)
    {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

}
