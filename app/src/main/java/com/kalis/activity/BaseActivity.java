package com.kalis.activity;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kalis.R;

/**
 * Created by Kalis on 1/6/2016.
 */
public class BaseActivity extends AppCompatActivity {
    private Toolbar toolbar;

    void addToolbar(Activity activity, int icon) {
        toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(icon);
//        actionBar.setHideOnContentScrollEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);

    }


}
