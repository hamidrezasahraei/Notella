package sahraei.hamidreza.com.notella.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.UUID;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by hamidrezasahraei on 22/6/2017 AD.
 */
@Entity(
        tableName="folders",
        foreignKeys=@ForeignKey(
                entity=Folder.class,
                parentColumns="id",
                childColumns="parentId",
                onDelete=CASCADE),
        indices=@Index(value="parentId"))
public class Folder {

    @PrimaryKey
    public final String id;
    public final String title;
    public final String parentId;

    @Ignore
    public Folder(String title) {
        this(title, null);
    }

    @Ignore
    public Folder(String title, String parentId) {
        this(UUID.randomUUID().toString(), title, parentId);
    }

    public Folder(String id, String title, String parentId) {
        this.id=id;
        this.title=title;
        this.parentId=parentId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getParentId() {
        return parentId;
    }
}
