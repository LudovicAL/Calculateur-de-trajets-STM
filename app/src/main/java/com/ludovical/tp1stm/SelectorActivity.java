package com.ludovical.tp1stm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SelectorActivity extends AppCompatActivity {

    private String destination;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private ListView listView;
    private ListViewAdapter listViewAdapter;
    private ArrayList<Route> routeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        //Retrieve data passed by previous activity
        retrieveIntentInformation();
        Toast.makeText(SelectorActivity.this, destination + " : " + year + "/" + month + "/" + day + " || " + hour + ":" + minute, Toast.LENGTH_LONG).show();
        //Locate and prepare the list view
        locateAndPrepareListView();
    }

    private void locateAndPrepareListView() {
        listView = (ListView)findViewById(R.id.listViewResults);
        listViewAdapter = new ListViewAdapter(this, routeList);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //listViewAdapter.fonctionQuelquonque(position);
            }
        });
    }

    private void retrieveIntentInformation() {
        Intent i = getIntent();
        destination = i.getStringExtra("destination");
        year = i.getIntExtra("year", -1);
        month = i.getIntExtra("month", -1);
        day = i.getIntExtra("day", -1);
        hour = i.getIntExtra("hour", -1);
        minute = i.getIntExtra("minute", -1);
    }

    public void onSelectorActivityBackButtonClick(View v) {
        finish();
    }

}
