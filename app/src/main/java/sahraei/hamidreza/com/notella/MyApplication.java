package sahraei.hamidreza.com.notella;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

/**
 * Created by hamidrezasahraei on 29/6/2017 AD.
 */

public class MyApplication extends Application {
    public static MyApplication instance;
    public static int DEVICE_HEIGHT;
    public static String last_path;
    public static final String BACKUP_EXTENSION = "notella";
    public static final String LAST_PATH_KEY = "path";

    Stack<String> historyFolderStack = new Stack<>();

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        // Get preferences
        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        last_path = prefs.getString(LAST_PATH_KEY, null);

        Point size = new Point();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        DEVICE_HEIGHT = size.y;

        instance = this;
    }

    public void putPrefs(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public void putPrefs(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public boolean backupFromDatabase(String pathToSave){
//        final String inFileName = "/data/data/<your.app.package>/databases/foo.db";
        final String databaseAddress = getApplicationContext().getDatabasePath("notlleaDB").getPath();
        File dbFile = new File(databaseAddress);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(dbFile);
            String outFileName = pathToSave+"/"+generateBackupName();
            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);
            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }
            // Close the streams
            output.flush();
            output.close();
            fis.close();
            Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean restoreFromBackup(String backupAddress){
//        final String inFileName = "/data/data/<your.app.package>/databases/foo.db";
        final String databaseAddress = getApplicationContext().getDatabasePath("notlleaDB").getPath();
        File dbFile = new File(backupAddress);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(dbFile);
            String outFileName = databaseAddress;
            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);
            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }
            // Close the streams
            output.flush();
            output.close();
            fis.close();
            Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private String generateBackupName(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("Notella");
        sb.append(calendar.get(Calendar.YEAR));
        sb.append(calendar.get(Calendar.DAY_OF_YEAR));
        sb.append(calendar.get(Calendar.HOUR));
        sb.append(calendar.get(Calendar.MINUTE));
        sb.append(calendar.get(Calendar.SECOND));
        sb.append("."+BACKUP_EXTENSION);
        return sb.toString();
    }
}
