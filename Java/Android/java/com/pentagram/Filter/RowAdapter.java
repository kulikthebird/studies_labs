package com.pentagram.Filter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

import com.pentagram.R;

/**
 * Created by Tomek on 2015-03-30.
 */
public class RowAdapter extends ArrayAdapter<RowBean>{

    Context context;
    int layoutResourceId;
    ArrayList<RowBean> data = null;

    public RowAdapter(Context context, int layoutResourceId, ArrayList<RowBean> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();

        View row=inflater.inflate(R.layout.list_row, parent, false);
        RowBean object;
        object = (position != -1)?data.get(position):new RowBean("Select Filter");

        TextView label=(TextView)row.findViewById(R.id.textView);
        label.setText(object.text);

        //ImageView icon=(ImageView)row.findViewById(R.id.imageView);
        //icon.setImageResource(object.icon);

        return row;
    }
}