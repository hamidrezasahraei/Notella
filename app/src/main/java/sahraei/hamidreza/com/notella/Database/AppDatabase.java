package sahraei.hamidreza.com.notella.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.text.Html;
import android.text.Spannable;

import sahraei.hamidreza.com.notella.Database.DAO.FolderDAO;
import sahraei.hamidreza.com.notella.Database.DAO.NoteDAO;
import sahraei.hamidreza.com.notella.Model.Folder;
import sahraei.hamidreza.com.notella.Model.Note;

/**
 * Created by hamidrezasahraei on 22/6/2017 AD.
 */
@Database(entities = {Note.class, Folder.class}, version = 1)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract NoteDAO noteDAO();

    public abstract FolderDAO folderDAO();

    private static AppDatabase db;

    public static AppDatabase getInstance(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context, AppDatabase.class, "notlleaDB").build();
        }
        return db;
    }

}
