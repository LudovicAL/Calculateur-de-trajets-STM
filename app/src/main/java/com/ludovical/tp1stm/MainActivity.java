package com.ludovical.tp1stm;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int A_STOP_NAME = 2;
    public static final int A_STOP_LAT = 3;
    public static final int A_STOP_LON = 4;
    public static final int A_ARRIVAL_TIME = 8;
    public static final int TRIP_HEAD_SIGN = 15;
    public static final int B_STOP_NAME = 23;
    public static final int B_STOP_LAT = 24;
    public static final int B_STOP_LON = 25;
    public static final int B_ARRIVAL_TIME = 29;
    public static final int MAX_WALKING_DISTANCE = 1500;
    public static final int NUMBER_OF_RESULTS = 5;
    public static final int DATABASE_INITIAL_SCHEMA = 1;
    public static final int DATABASE_MIGRATION_1_1 = 2;

    private Spinner spinner;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private SQLiteDatabase db;
    private List<Coordinates> spinnerCoordinates;
    private Calendar initialCalendar;
    private Coordinates initialCoordinates;
    private Coordinates objectiveCoordinates;
    private boolean occupied;
    private Button buttonInitialPosition;
    private Button buttonObjectivePosition;
    private Button buttonDate;
    private Button buttonTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize coordinates
        initialCoordinates = new Coordinates("Pavillon André-Aisenstadt", 45.5010115, -73.6179101);
        objectiveCoordinates = new Coordinates("Centre Bell", 45.4960704, -73.571504);
        buttonInitialPosition = (Button)findViewById(R.id.buttonSelectInitialPosition);
        buttonInitialPosition.setText(initialCoordinates.getName());
        buttonObjectivePosition = (Button)findViewById(R.id.buttonSelectObjectivePosition);
        buttonObjectivePosition.setText(objectiveCoordinates.getName());
        //Initialize calendar
        initialCalendar  = Calendar.getInstance();
        buttonDate = (Button)findViewById(R.id.buttonDate);
        buttonDate.setText(CommonTools.calendarToDateString(initialCalendar));
        buttonTime = (Button)findViewById(R.id.buttonTime);
        buttonTime.setText(CommonTools.calendarToTimeString(initialCalendar));
        //Creating the database
        db = openOrCreateDatabase("stm_gtfs", MODE_PRIVATE, null);
        //Preparing the Schema and database
        applyMigration(db, DATABASE_INITIAL_SCHEMA, R.raw.schema);
        applyMigration(db, DATABASE_MIGRATION_1_1, R.raw.migration_1_1);
        Intent fillDatabaseIntent = new Intent(MainActivity.this, FillDataBaseService.class);
        String[] tables = {"calendar_dates", "feed_info", "stops", "routes", "shapes", "trips", "stop_times"};
        fillDatabaseIntent.putExtra("tables", tables);
        startService(fillDatabaseIntent);
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

    //On a click on the select date button
    public void onSelectDateClick(View v) {
        int year = 0;
        int month = 0;
        int day = 0;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }

    //The datePickerDialog listener
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            initialCalendar.set(year, month, day);
            buttonDate.setText(CommonTools.calendarToDateString(initialCalendar));
        }
    };

    //On a click on the select time button
    public void onSelectTimeClick(View v) {
        int hourOfDay = 0;
        int minute = 0;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, myTimeListener, hourOfDay, minute, true);
        timePickerDialog.show();
    }

    //The timePickerDialog listener
    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            initialCalendar.set(initialCalendar.get(Calendar.YEAR), initialCalendar.get(Calendar.MONTH), initialCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
            buttonTime.setText(CommonTools.calendarToTimeString(initialCalendar));
        }
    };

    //On a click on the initial position selection button
    public void onSelectInitialPositionButtonClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra("actualCoordinates", (Serializable) initialCoordinates);
        intent.putExtra("otherCoordinates", (Serializable) objectiveCoordinates);
        intent.putExtra("message", getResources().getString(R.string.instructionInitialPosition));
        startActivityForResult(intent, 1);
    }

    //On a click on the objective position selection button
    public void onSelectObjectivePositionButtonClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra("actualCoordinates", (Serializable) objectiveCoordinates);
        intent.putExtra("otherCoordinates", (Serializable) initialCoordinates);
        intent.putExtra("message", getResources().getString(R.string.instructionObjectivePosition));
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    initialCoordinates = (Coordinates)intent.getSerializableExtra("coordinates");
                    buttonInitialPosition.setText(initialCoordinates.getLatitude() + ", " + initialCoordinates.getLongitude());
                    break;
                case 2:
                    objectiveCoordinates = (Coordinates)intent.getSerializableExtra("coordinates");
                    buttonObjectivePosition.setText(objectiveCoordinates.getLatitude() + ", " + objectiveCoordinates.getLongitude());
                    break;
            }
        }
    }

    //On a click on the search button
    public void onSearchButtonClick(View v) {
        if (!occupied) {
            occupied = true;
            String query = "SELECT *," +
                    " (select group_concat(shape_pt_lat || ',' || shape_pt_lon, ';') from shapes" +
                    " where A_prime_B_prime_trips.shape_id = shapes.shape_id" +
                    " group by shape_id" +
                    " order by shape_pt_sequence)" +
                    " from stops as A_prime" +
                    " join stop_times as A_prime_times on A_prime.stop_id = A_prime_times.stop_id" +
                    " join trips as A_prime_B_prime_trips on A_prime_times.trip_id = A_prime_B_prime_trips.trip_id" +
                    " join stops as B_prime" +
                    " join stop_times as B_prime_times on B_prime.stop_id = B_prime_times.stop_id" +
                    " and A_prime_times.trip_id = B_prime_times.trip_id" +
                    " and A_prime_times.stop_sequence < B_prime_times.stop_sequence" +
                    " WHERE" +
                    " service_id in (select service_id from calendar_dates where calendar_dates.date = strftime('%Y%m%d', date('" + CommonTools.calendarToDateString(initialCalendar) + "', '" + CommonTools.calendarToTimeString(initialCalendar) + "')))" +
                    " and 3600 * substr(A_prime_times.arrival_time, 0, 3) + strftime('%s', '00' || substr(A_prime_times.arrival_time, 3))" +
                    " >= strftime('%s', time('" + CommonTools.calendarToDateString(initialCalendar) + "', '" + CommonTools.calendarToTimeString(initialCalendar) + "')) + 1.37 * 98194.860939613 * (abs(A_prime.stop_lat - " + initialCoordinates.getLatitude() + ") + abs(A_prime.stop_lon - " + initialCoordinates.getLongitude() + "))" +
                    " and 98194.860939613 * (abs(A_prime.stop_lat - " + initialCoordinates.getLatitude() + ") + abs(A_prime.stop_lon - " + initialCoordinates.getLongitude() + ")) <= " + MAX_WALKING_DISTANCE +
                    " and 98194.860939613 * (abs(" + objectiveCoordinates.getLatitude() + " - B_prime.stop_lat) + abs(" + objectiveCoordinates.getLongitude() + " - B_prime.stop_lon)) <= " + MAX_WALKING_DISTANCE +
                    " and 98194.860939613 * (abs(A_prime.stop_lat - " + initialCoordinates.getLatitude() + ") + abs(A_prime.stop_lon - " + initialCoordinates.getLongitude() + ") + abs(" + objectiveCoordinates.getLatitude() + " - B_prime.stop_lat) + abs(" + objectiveCoordinates.getLongitude() + " - B_prime.stop_lon)) <= " + MAX_WALKING_DISTANCE +
                    " group by A_prime_B_prime_trips.trip_id" +
                    " order by" +
                    " 3600 * substr(B_prime_times.arrival_time, 0, 3) + strftime('%s', '00' || substr(B_prime_times.arrival_time, 3))" +
                    " + 1.37 * 98194.860939613 * (abs(" + objectiveCoordinates.getLatitude() + " - B_prime.stop_lat) + abs(" + objectiveCoordinates.getLongitude() + " - B_prime.stop_lon))," +
                    " min(3600 * substr(B_prime_times.arrival_time, 0, 3) + strftime('%s', '00' || substr(B_prime_times.arrival_time, 3))" +
                    " + 1.37 * 98194.860939613 * (abs(" + objectiveCoordinates.getLatitude() + " - B_prime.stop_lat) + abs(" + objectiveCoordinates.getLongitude() + " - B_prime.stop_lon)))" +
                    " limit " + NUMBER_OF_RESULTS + ";";
            Log.d("test", query);
            new ASyncQuery().execute(query);
        } else {
            Toast.makeText(MainActivity.this, R.string.pleaseWait, Toast.LENGTH_LONG).show();
        }
    }

    private class ASyncQuery extends AsyncTask<String, Void, Cursor> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, R.string.querySent, Toast.LENGTH_LONG).show();
        }

        @Override
        protected Cursor doInBackground(String... params) {
            Log.d("test", "Sending Query");
            Cursor cursor = db.rawQuery(StringUtils.normalizeSpace(params[0]), null);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            onQueryEnded(cursor);
        }
    }

    private void onQueryEnded(Cursor cursor) {
        ArrayList<Route> routeList = new ArrayList<Route>() {};
        try {
            Log.d("test", "Cursor rows count size: " + String.valueOf(cursor.getCount()));
            if (cursor.moveToFirst()) {
                Log.d("Test", "Cursor could moveToFirst");
                do {
                    //Line number
                    String tripHeadSign = cursor.getString(TRIP_HEAD_SIGN);
                    //A
                    String aStopName = cursor.getString(A_STOP_NAME);
                    String aArrivalTime = cursor.getString(A_ARRIVAL_TIME);
                    Double aLatitude = cursor.getDouble(A_STOP_LAT);
                    Double aLongitude = cursor.getDouble(A_STOP_LON);
                    //B
                    String bStopName = cursor.getString(B_STOP_NAME);
                    String bArrivalTime = cursor.getString(B_ARRIVAL_TIME);
                    Double bLatitude = cursor.getDouble(B_STOP_LAT);
                    Double bLongitude = cursor.getDouble(B_STOP_LON);
                    //Object construction
                    Route route = new Route(tripHeadSign,
                            //INITIAL POSITION
                            initialCalendar,
                            initialCoordinates.getLatitude(),
                            initialCoordinates.getLongitude(),
                            //A POSITION
                            aStopName,
                            aArrivalTime,
                            aLatitude,
                            aLongitude,
                            //B POSITION
                            bStopName,
                            bArrivalTime,
                            bLatitude,
                            bLongitude,
                            //OBJECTIVE POSITION
                            objectiveCoordinates.getLatitude(),
                            objectiveCoordinates.getLongitude()
                    );
                    routeList.add(route);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        if (!routeList.isEmpty()) {
            //Launching the new Intent
            Log.d("test", "result.size : " + routeList.size());
            Intent newIntent = new Intent(getApplicationContext(), SelectorActivity.class);
            for (int i = 0, max = routeList.size(); i < max; i++) {
                newIntent.putExtra("route" + i, routeList.get(i));
            }
            startActivity(newIntent);
        } else {
            Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            Log.d("test", "The cursor value remained null after the query.");
        }
        occupied = false;
    }

    //Apply migration to schema if applicable
    private void applyMigration(SQLiteDatabase db, int version, int migrationId) {
        if (db.needUpgrade(version)) {
            InputStream migration = getResources().openRawResource(migrationId);
            db.beginTransaction();
            try {
                String schema = IOUtils.toString(migration)
                        .replaceAll("--.*\r?\n", " ") //For commentaries
                        .replaceAll("\r?\n", " ");  //For new lines
                for (String statement : schema.split(";")) {
                    String st = StringUtils.normalizeSpace(statement);
                    Log.d("test", st);
                    if (!st.isEmpty())
                        db.execSQL(st);
                }
                db.setVersion(version);
                db.setTransactionSuccessful();
                Log.d("test", "La migration " + version + " a été appliquée.");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                IOUtils.closeQuietly(migration);
            }
        }
    }
}
