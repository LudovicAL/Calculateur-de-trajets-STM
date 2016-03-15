package com.ludovical.tp1stm;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    public ArrayList<Route> routeList;
    private Context context;

    public ListViewAdapter(Context context, ArrayList<Route> routeList) {
        this.context = context;
        this.routeList = routeList;
    }

    @Override
    public int getCount() {
        return routeList.size();
    }

    @Override
    public Object getItem(int position) {
        return routeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View elementLayout = convertView;
        if (elementLayout == null) {
            LayoutInflater li;
            li = LayoutInflater.from(context);
            elementLayout = li.inflate(R.layout.listview_item, null);
        }
        TextView tv = (TextView)elementLayout.findViewById(R.id.genericTextView);
        tv.setText(routeList.get(position).toString());
        final int pos = position;
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDetails(pos);
            }
        });

        return elementLayout;
    }

    //Called when user clicks an item of the listview
    public void displayDetails(int position) {
        Intent newIntent = new Intent(context, DisplayActivity.class);
        newIntent.putExtra("route", routeList.get(position));
        context.startActivity(newIntent);
    }
}