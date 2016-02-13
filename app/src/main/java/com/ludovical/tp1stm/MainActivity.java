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
import org.apache.commons.io.IOUtils;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int DATABASE_INITIAL_SCHEMA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Create database
        SQLiteDatabase db = openOrCreateDatabase("stm_gtfs", MODE_PRIVATE, null);
        //Prepare Schema
        prepareSchema(db);

        /*
        startService(new Intent(this, FillDataBaseService.class));

        new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... params) {

                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {

            }

            @Override
            protected void onPostExecute (Void result) {

            }
        }.execute ("route", "stop_times", "stops", "trips");

        private void insertData(String tableName, InputStream data) {

        }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        Log.e("", "La création du schéma SQL a échouée.");
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
