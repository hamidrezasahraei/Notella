package sahraei.hamidreza.com.notella;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import sahraei.hamidreza.com.notella.Database.AppDatabase;
import sahraei.hamidreza.com.notella.Model.Folder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "notella").build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Folder> childs = db.folderDAO().findChildFolder(db.folderDAO().findRootDirectory().getId());
                db.folderDAO().delete(childs.get(0));

                System.out.println(db.folderDAO().getAll().size()+"");
            }
        });
    }
}
