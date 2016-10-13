package com.kalis.thread;

import android.app.Activity;

/**
 * Created by Kalis on 12/28/2015.
 */
public class CloseActivityThread implements Runnable {
    Thread thread;
    long second;
    Activity o;

    public void waitSecondToCloseActivity(int mili,Activity o)
    {
        this.o = o;
        this.second = mili*1000;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        synchronized(this){
            try {
                thread.sleep(second);
                o.finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
