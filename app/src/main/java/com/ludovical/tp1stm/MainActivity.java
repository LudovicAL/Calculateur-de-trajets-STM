package com.ludovical.tp1stm;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int MAX_WALKING_DISTANCE = 250;
    public static final int NUMBER_OF_RESULTS = 5;
    public static final int DATABASE_INITIAL_SCHEMA = 1;
    public static final Coordinates A_COORDINATES = new Coordinates("Pavillon André-Aisenstadt", 45.5010115, 73.6179101);

    private Spinner spinner;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private SQLiteDatabase db;
    private List<Coordinates> coordinates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Create a new CoordinatesSet ArrayList
        createCoordinates();
        //Locating and filling the spinner
        locateAndFillSpinner();
        //Locating and setting the date picker
        locateAndSetDatePicker();
        //Locating the time picker
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        //Creating the database
        db = openOrCreateDatabase("stm_gtfs", MODE_PRIVATE, null);
        //Preparing the Schema
        prepareSchema();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflating the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSearchButtonClick(View v) {
        Coordinates bCoordinates = coordinates.get(spinner.getSelectedItemPosition());
        Log.d("test", bCoordinates.toString());
        //Querying the database
        ArrayList<String> result;
        result = dbQuery(bCoordinates);
        if (!result.isEmpty()) {
            //Launching the new Intent
            Log.d("test", "result.size : " + result.size());
            for (String s : result) {
                Log.d("test", "result content: " + s);
            }
            Intent newIntent = new Intent(getApplicationContext(), SelectorActivity.class);
            newIntent.putExtra("destination", spinner.getSelectedItem().toString());
            newIntent.putExtra("year", datePicker.getYear());
            newIntent.putExtra("month", datePicker.getMonth());
            newIntent.putExtra("day", datePicker.getDayOfMonth());
            //newIntent.putExtra("hour", timePicker.getHour());
            //newIntent.putExtra("minute", timePicker.getMinute());
            startActivity(newIntent);
        } else {
            Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            Log.d("test", "The cursor value remained null after the query.");
        }
    }

    //Creates a coordinatesSet array
    private void createCoordinates() {
        this.coordinates = new ArrayList<Coordinates>();
        String[] destinationsArray = this.getResources().getStringArray(R.array.destinations_values);
        for(String s : destinationsArray) {
            String name = s.substring(0, s.indexOf("(") - 1);
            double latitude = CommonTools.stringToDouble(s.substring(s.indexOf("(") + 1, s.indexOf(",")));
            double longitude = CommonTools.stringToDouble(s.substring(s.indexOf(",") + 2, s.indexOf(")")));
            coordinates.add(new Coordinates(name, latitude, longitude));
        }
    }

    //Locates the date picker and sets today as its minimum selectable date
    private void locateAndSetDatePicker() {
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.setMinDate(new Date().getTime());
    }

    //Locates the spinner and fills it with predefined destinations values
    private void locateAndFillSpinner() {
        spinner = (Spinner) findViewById(R.id.spinnerDestinations);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.destinations_values, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    //Sends the Query to the database and returns a Cursor
    private ArrayList<String> dbQuery(Coordinates bCoordinates) {
        Cursor cursor = null;
        String query = "SELECT *"
                + " from stops as A_prime" //arrêts à A'
                + " join stop_times as A_prime_times on A_prime.stop_id = A_prime_times.stop_id" //temps aux arrêtes de A'
                + " join trips as A_prime_B_prime_trips on A_prime_times.trip_id = A_prime_B_prime_trips.trip_id" //chemins de A' à B'
                + " join stops as B_prime" //arrêtes à B'
                + " join stop_times as B_prime_times on B_prime.stop_id = B_prime_times.stop_id" //temps aux arrêtes à B'
                + " and A_prime_times.trip_id = B_prime_times.trip_id" //chemin de A' à B'
                + " and time(A_prime_times.departure_time) < time(B_prime_times.arrival_time)" //d(A') < d(B')
                + " WHERE"
                //marche jusqu'à l'arrêt (5 km/h)
                + " time(A_prime_times.departure_time) >= time(strftime('%s', 'now', 'localtime') + 1.37 * 51883.246273604 * (abs(A_prime.stop_lat - " + A_COORDINATES.getLatitude() + ") + abs(A_prime.stop_lon - " + A_COORDINATES.getLongitude() + ")), 'unixepoch')" //d(A) < d(A')
                //filtre les points A' et B'
                + " and 51883.246273604 * (abs(A_prime.stop_lat - " + A_COORDINATES.getLatitude() + ") + abs(A_prime.stop_lon - " + A_COORDINATES.getLongitude() + ")) <= " + MAX_WALKING_DISTANCE
                + " and 51883.246273604 * (abs(" + bCoordinates.getLatitude() + " - B_prime.stop_lat) + abs(" + bCoordinates.getLongitude() + " - B_prime.stop_lon)) <= " + MAX_WALKING_DISTANCE
                //distance maximale de marche
                + " and 51883.246273604 * (abs(A_prime.stop_lat - " + A_COORDINATES.getLatitude() + ") + abs(A_prime.stop_lon - " + A_COORDINATES.getLongitude() + ") + abs(" + bCoordinates.getLatitude() + " - B_prime.stop_lat) + abs(" + bCoordinates.getLongitude() + " - B_prime.stop_lon)) <= " + MAX_WALKING_DISTANCE //d(A, A') + d(B', B)
                //minimise le temps d'arrivée t(B') + 1.37 * d(B', B)
                + " order by time(B_prime_times.arrival_time) + 1.37 * 51883.246273604 * (abs (" + bCoordinates.getLatitude() + " - B_prime.stop_lat) + abs(" + bCoordinates.getLongitude() + " - B_prime.stop_lon))" //minimise t(B') + t(B)
                + " limit " + NUMBER_OF_RESULTS + ";";
        Log.d("test", query);
        ArrayList<String> result = new ArrayList<String>() {};
        try {
            cursor = db.rawQuery(query, null);
            Log.d("test", "Query sent");
            Log.d("test", "Cursor rows count size: " + String.valueOf(cursor.getCount()));
            if (cursor.moveToFirst()) {
                Log.d("Test", "Cursor could moveToFirst");
                do {
                    Log.d("Test", "Retreiving curcor data");
                    String tempo = cursor.getString(0);
                    result.add(tempo);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    //Builds the SQL schema
    private void prepareSchema() {
        if (db.needUpgrade(DATABASE_INITIAL_SCHEMA)) {
            try {
                InputStream schemaStream = getResources().openRawResource(R.raw.schema);
                String schema = IOUtils.toString(schemaStream);
                IOUtils.closeQuietly(schemaStream);
                db.beginTransactionWithListener(new SQLiteTransactionListener() {
                    @Override
                    public void onBegin() {

                    }

                    @Override
                    public void onCommit() {
                        Log.d("test", "The schema was successfully created.");
                        Intent fillDatabaseIntent = new Intent(MainActivity.this, FillDataBaseService.class);
                        String[] tables = {"stops", "routes", "trips", "stop_times"};
                        fillDatabaseIntent.putExtra("tables", tables);
                        startService(fillDatabaseIntent);
                    }

                    @Override
                    public void onRollback() {
                        Log.d("", "The schema creation failed.");
                    }
                });
                for (String statement : schema.split(";")) {
                    statement = StringUtils.normalizeSpace(statement.replaceAll("[\r\n]", ""));
                    if (!statement.isEmpty()) {
                        db.execSQL(statement);
                    }
                }
                db.setVersion(DATABASE_INITIAL_SCHEMA);
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.d("", db.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("test", "The schema doesn't need an upgrade.");
            Intent fillDataBaseIntent = new Intent(MainActivity.this, FillDataBaseService.class);
            String[] tables = {"stops", "routes", "trips", "stop_times"};
            fillDataBaseIntent.putExtra("tables", tables);
            startService(fillDataBaseIntent);
        }
    }
}
