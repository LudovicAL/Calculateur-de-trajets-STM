package com.ludovical.tp1stm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FillDataBaseService extends IntentService {

    public static final int BATCH_SIZE = 50;

    public FillDataBaseService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String[] tables = intent.getExtras().getStringArray("tables");
        Log.d("test", "Population des tables: " + StringUtils.join(tables, ", "));
        ZipInputStream zis = new ZipInputStream(getResources().openRawResource(R.raw.gtfs_stm));
        ZipEntry currentEntry;
        SQLiteDatabase db = openOrCreateDatabase("stm_gtfs", MODE_PRIVATE, null);
        // db.setForeignKeyConstraintsEnabled(true);
        db.beginTransactionNonExclusive();
        // vider les tables en question!
        for (String table : tables) {
            db.delete(table, null, new String[]{});
        }
        try {
            while ((currentEntry = zis.getNextEntry()) != null) {
                Log.d("test", currentEntry.getName());
                for (int i = 0; i < tables.length; i++) {
                    String tableName = tables[i];
                    if (tableName.equals(currentEntry.getName().substring(0, currentEntry.getName().lastIndexOf(".")))) {
                        Log.d("test", "Chargement de la table " + tableName + "...");
                        Bundle progressBundle = new Bundle();
                        progressBundle.putInt(Notification.EXTRA_PROGRESS, i);
                        progressBundle.putInt(Notification.EXTRA_PROGRESS_MAX, tables.length);
                        nm.notify(R.id.PROGRESS_NOTIFICATION_ID, new Notification.Builder(FillDataBaseService.this)
                                .setContentTitle("Chargement de la table " + tableName + "...")
                                .setSmallIcon(android.R.drawable.ic_popup_sync)
                                .setCategory(Notification.CATEGORY_PROGRESS)
                                .setExtras(progressBundle)
                                .build());
                        CSVParser parser = new CSVParser(new InputStreamReader(zis), CSVFormat.RFC4180.withHeader());
                        for (CSVRecord row : parser) {
                            ContentValues values = new ContentValues();
                            for (Map.Entry<String, String> e : row.toMap().entrySet()) {
                                if (e.getValue().isEmpty()) { // gère les valeurs vides en CSV
                                    values.putNull(e.getKey());
                                } else {
                                    values.put(e.getKey(), e.getValue());
                                }
                            }
                            db.insert(tableName, null, values);
                        }
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (IOException err) {
            Log.d("test", "", err);
        } finally {
            nm.cancel(R.id.PROGRESS_NOTIFICATION_ID);
            db.endTransaction();
        }
        Toast.makeText(FillDataBaseService.this, "Imported tables " + Arrays.toString(tables), Toast.LENGTH_SHORT).show();
    }
}