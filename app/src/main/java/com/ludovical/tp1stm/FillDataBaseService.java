package com.ludovical.tp1stm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FillDataBaseService extends IntentService {

    public static final int BATCH_SIZE = 100000;

    public FillDataBaseService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Progress
        startForeground(R.id.PROGRESS_NOTIFICATION_ID, new Notification.Builder(FillDataBaseService.this)
                .setContentTitle("Chargement des données")
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setCategory(Notification.CATEGORY_PROGRESS)
                .setProgress(100, 0, true)
                .build());
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //OpenOrCreate Database
        SQLiteDatabase db = openOrCreateDatabase("stm_gtfs", MODE_PRIVATE, null);
        String[] tables = intent.getExtras().getStringArray("tables");
        Log.d("test", "Population des tables: " + StringUtils.join(tables, ", "));
        //Check if database is up to date
        Cursor feedInfoCursor = db.query("feed_info", new String[]{"feed_start_date", "feed_end_date"}, null, new String[]{}, null, null, null);
        OkHttpClient client = new OkHttpClient();
        if (feedInfoCursor.moveToFirst()) {
            String feedStartDate  = feedInfoCursor.getString(feedInfoCursor.getColumnIndex("feed_start_date"));
            String feedEndDate = feedInfoCursor.getString(feedInfoCursor.getColumnIndex("feed_end_date"));
            try {
                String lastModified = client.newCall(new Request.Builder()
                        .head()
                        .url("http://www.stm.info/sites/default/files/gtfs/gtfs_stm.zip")
                        .build()).execute().header("Last-Modified");
                Log.i("test", feedStartDate + " <= " + lastModified + " <= " + feedEndDate);
                // TODO: si feedStartDate <= lastModified <= feedEndDate then return!
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Download files from the STM website
        Request req = new Request.Builder()
                .get()
                .url("http://www.stm.info/sites/default/files/gtfs/gtfs_stm.zip")
                .build();
        File outputFile;
        try {
            outputFile = File.createTempFile("prefix", "extension", getCacheDir());
            FileOutputStream fio = new FileOutputStream(outputFile);
            IOUtils.copyLarge(client.newCall(req).execute().body().byteStream(), fio);
            IOUtils.closeQuietly(fio);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(outputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Prepare database for insertion
        db.setForeignKeyConstraintsEnabled(false);
        db.rawQuery("pragma journal_mode=off", new String[]{});
        db.rawQuery("pragma synchronous=off", new String[]{});
        //Empty tables
        for (String table : tables) {
            db.delete(table, null, new String[]{});
        }
        try {
            ZipEntry currentEntry;
            while ((currentEntry = zis.getNextEntry()) != null) {
                Log.d("test", currentEntry.getName());
                for (int i = 0; i < tables.length; i++) {
                    String tableName = tables[i];
                    if (tableName.equals(currentEntry.getName().substring(0, currentEntry.getName().lastIndexOf(".")))) {
                        long begin = System.currentTimeMillis();
                        Log.i("test", "Chargement de la table " + tableName + "...");
                        nm.notify(R.id.PROGRESS_NOTIFICATION_ID, new Notification.Builder(FillDataBaseService.this)
                                .setContentTitle("Chargement de la table " + tableName + "...")
                                .setSmallIcon(android.R.drawable.ic_popup_sync)
                                .setCategory(Notification.CATEGORY_PROGRESS)
                                .setProgress(100, 0, true)
                                .build());
                        CSVParser parser = new CSVParser(new InputStreamReader(zis), CSVFormat.RFC4180.withHeader());
                        ContentValues values = new ContentValues();
                        DatabaseUtils.InsertHelper insertHelper = new DatabaseUtils.InsertHelper(db, tableName);
                        long cumulativeSizeApproximation = 0;
                        long batchBegin = System.currentTimeMillis();
                        for (CSVRecord row : parser) {
                            if ((row.getRecordNumber() - 1) % BATCH_SIZE == 0) {
                                batchBegin = System.currentTimeMillis();
                            }
                            for (Map.Entry<String, String> e : row.toMap().entrySet()) {
                                if (e.getValue().isEmpty()) { //Manage empty values in CSV
                                    values.putNull(e.getKey());
                                } else {
                                    values.put(e.getKey(), e.getValue());
                                }
                            }
                            insertHelper.insert(values);
                            cumulativeSizeApproximation += row.toString().length();
                            if ((row.getRecordNumber() - 1) % BATCH_SIZE == BATCH_SIZE - 1) {
                                insertHelper.prepareForInsert();
                                insertHelper.execute();
                                Log.i("test", "Inséré la batch #"
                                        + (row.getRecordNumber() / BATCH_SIZE) + "/~" + ((currentEntry.getSize() / (cumulativeSizeApproximation / row.getRecordNumber())) / BATCH_SIZE)
                                        + " dans la table " + tableName
                                        + " en " + (System.currentTimeMillis() - batchBegin) + "ms"
                                        + " (" + BATCH_SIZE / (((System.currentTimeMillis() - batchBegin) / 1000)) + " insert/sec)");
                                nm.notify(R.id.PROGRESS_NOTIFICATION_ID, new Notification.Builder(FillDataBaseService.this)
                                        .setContentTitle("Chargement de la table " + tableName + "...")
                                        .setSmallIcon(android.R.drawable.ic_popup_sync)
                                        .setCategory(Notification.CATEGORY_PROGRESS)
                                        .setProgress(100, (int) (cumulativeSizeApproximation / currentEntry.getSize()) * 100, false)
                                        .build());
                            }
                        }
                        // insert incomplete batch (if appliable)
                        insertHelper.prepareForInsert();
                        insertHelper.execute();

                        Log.i("test", "Terminé l'insertion de la table " + tableName + " en " + (System.currentTimeMillis() - begin) + "ms");
                    }
                }
            }
            Toast.makeText(FillDataBaseService.this, "Importé les tables: " + Arrays.toString(tables), Toast.LENGTH_SHORT).show();
        } catch (IOException err) {
            err.printStackTrace();
        } finally {
            nm.cancel(R.id.PROGRESS_NOTIFICATION_ID);
        }
    }
}