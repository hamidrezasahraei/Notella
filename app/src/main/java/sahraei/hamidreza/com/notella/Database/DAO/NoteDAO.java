package sahraei.hamidreza.com.notella.Database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import sahraei.hamidreza.com.notella.Model.Note;

/**
 * Created by hamidrezasahraei on 22/6/2017 AD.
 */
@Dao
public interface NoteDAO {

    @Query("SELECT * FROM notes")
    List<Note> getAll();

    @Query("SELECT * FROM notes WHERE id IN (:noteIds)")
    List<Note> loadAllByIds(String[] noteIds);

    @Query("SELECT * FROM notes WHERE id = (:noteIds)")
    Note loadNoteById(String noteIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Note... notes);

    @Delete
    void delete(Note note);
}
