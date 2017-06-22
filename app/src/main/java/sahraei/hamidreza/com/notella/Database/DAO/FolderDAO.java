package sahraei.hamidreza.com.notella.Database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import sahraei.hamidreza.com.notella.Model.Folder;
import sahraei.hamidreza.com.notella.Model.Note;

/**
 * Created by hamidrezasahraei on 22/6/2017 AD.
 */
@Dao
public interface FolderDAO {

    @Query("SELECT * FROM folders")
    List<Folder> getAll();

    @Query("SELECT * FROM folders WHERE id IN (:folderIds)")
    List<Folder> loadAllByIds(int[] folderIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Folder... folders);

    @Delete
    void delete(Folder folder);

    @Query("SELECT * FROM folders WHERE parentId IS NULL")
    Folder findRootDirectory();

    @Query("SELECT * FROM folders WHERE parentId=:parentId")
    List<Folder> findChildFolder(String parentId);

}
