package com.kalis.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kalis.R;


/**
 * Created by Kalis on 1/6/2016.
 */
public class ListViewAdapter extends ArrayAdapter<String> {
    Activity activity;
    int resource;
    LinearLayout ll;
    String[] obStrings;
    Integer[] imgSource = {
            R.drawable.ic_home_white_24dp,
            R.drawable.ic_favorite_border_white_24dp,
            };
    public ListViewAdapter(Activity activity, int resource,String[] obStrings) {
        super(activity, resource);
        this.activity = activity;
        this.resource = resource;
        this.obStrings = obStrings;

    }

    @Override
    public int getCount() {
        return obStrings.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        convertView = inflater.inflate(resource,null);
        TextView txtListview = (TextView) convertView.findViewById(R.id.txtListView);
        ImageView imgListView = (ImageView) convertView.findViewById(R.id.imgListView);
        txtListview.setText(obStrings[position]);
        imgListView.setImageResource(imgSource[position]);


        return convertView;
    }

    public LinearLayout getLinearLayout() {
        View convertView = activity.getLayoutInflater().inflate(resource, null);
        if (ll == null)
            ll = (LinearLayout) convertView.findViewById(R.id.linear_layout_custom_popup);
        return ll;
    }
}
