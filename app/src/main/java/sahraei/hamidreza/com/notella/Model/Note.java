package sahraei.hamidreza.com.notella.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by hamidrezasahraei on 22/6/2017 AD.
 */
@Entity(
        tableName="notes",
        foreignKeys=@ForeignKey(
                entity=Folder.class,
                parentColumns="id",
                childColumns="parentId",
                onDelete=CASCADE),
        indices=@Index(value="parentId"))
public class Note {
    @PrimaryKey
    private String id;
    private String title;
    private String text;
    private Date creationDate;
    private String parentId;


    @Ignore
    public Note(String title, String text) {
        this(title, text, null);
    }

    @Ignore
    public Note(String title, String text, String parentId) {
        this(UUID.randomUUID().toString(), title, text, new Date(), parentId);
    }

    public Note(String id, String title, String text, Date creationDate, String parentId) {
        this.id=id;
        this.title=title;
        this.parentId=parentId;
        this.creationDate=creationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
