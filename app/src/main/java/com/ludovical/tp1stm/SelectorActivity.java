package com.ludovical.tp1stm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;

public class SelectorActivity extends AppCompatActivity {

    private ListView listView;
    private ListViewAdapter listViewAdapter;
    private ArrayList<Route> routeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        routeList = new ArrayList<>();
        //Retrieve data passed by previous activity
        retrieveIntentInformation();
        //Locate and prepare the ListViews
        locateAndPrepareListView();
    }

    //Locates and prepares the ListView
    private void locateAndPrepareListView() {
        listView = (ListView)findViewById(R.id.listViewResults);
        listViewAdapter = new ListViewAdapter(this, routeList);
        listView.setAdapter(listViewAdapter);
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewAdapter.fonctionQuelquonque(position);
            }
        });
        */
    }

    //Retrieves the information passed with the Intent
    private void retrieveIntentInformation() {
        Intent intent = getIntent();
        for (int i = 0; i < MainActivity.NUMBER_OF_RESULTS; i++) {
            Route route = (Route) intent.getSerializableExtra("route" + i);
            routeList.add(route);
            Log.d("test", route.toString());
        }
    }

    //Fired when user clicks the "Back" button
    public void onSelectorActivityBackButtonClick(View v) {
        finish();
    }

}
