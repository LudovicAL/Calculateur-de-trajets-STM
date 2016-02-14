package com.ludovical.tp1stm;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
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
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int DATABASE_INITIAL_SCHEMA = 1;
    private Spinner spinner;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Locate and fill the spinner
        locateAndFillSpinner();
        //Locate and set the date picker
        locateAndSetDatePicker();
        //Locate the time picker
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        //Create database
        //SQLiteDatabase db = openOrCreateDatabase("stm_gtfs", MODE_PRIVATE, null);
        //Prepare Schema
        //prepareSchema(db);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        Intent newIntent = new Intent(getApplicationContext(), SelectorActivity.class);
        newIntent.putExtra("destination", spinner.getSelectedItem().toString());
        newIntent.putExtra("year", datePicker.getYear());
        newIntent.putExtra("month", datePicker.getMonth());
        newIntent.putExtra("day", datePicker.getDayOfMonth());
        newIntent.putExtra("hour", timePicker.getHour());
        newIntent.putExtra("minute", timePicker.getMinute());
        startActivity(newIntent);
    }

    private void locateAndSetDatePicker() {
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.setMinDate(new Date().getTime());
    }

    private void locateAndFillSpinner() {
        spinner = (Spinner) findViewById(R.id.spinnerDestinations);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.destinations_values, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    private void prepareSchema(SQLiteDatabase db) {
        if (db.getVersion() < DATABASE_INITIAL_SCHEMA) {
            try {
                String schema = IOUtils.toString(getResources().openRawResource(R.raw.schema));
                db.beginTransactionWithListener(new SQLiteTransactionListener() {
                    @Override
                    public void onBegin() {

                    }

                    @Override
                    public void onCommit() {
                        Intent fillDatabaseIntent = new Intent(MainActivity.this, FillDataBaseService.class);
                        String[] tables = {"stops", "routes", "shapes", "trips", "stop_times"};
                        fillDatabaseIntent.putExtra("tables", tables);
                        startService(fillDatabaseIntent);
                    }

                    @Override
                    public void onRollback() {
                        Log.e("", "The SQL schema creation failed");
                    }
                });
                for (String statement : schema.split(";")) {
                    db.execSQL(statement);
                }
                db.setVersion(DATABASE_INITIAL_SCHEMA);
                db.endTransaction();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Cursor cursor = (SQLiteCursor) db.rawQuery("select * from trips limit 10", new String[] {});
        while (cursor.moveToNext()) {
            cursor.getInt(cursor.getColumnIndex("trip_id"));
        }
    }
}
