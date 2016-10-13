package com.kalis.thread;

import android.app.Activity;
import android.content.Intent;

import com.kalis.activity.MainActivity;

/**
 * Created by Kalis on 12/28/2015.
 */
public class ChangeActivityThread implements Runnable {
    Thread thread;
    long second;
    Activity o;

    public void secToGoMainAct(long mili, Activity o) {
        this.o = o;
        this.second = (mili * 1000);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                thread.sleep(second);
                    Intent intent = new Intent(o, MainActivity.class);
                    o.startActivity(intent);
                o.finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
