package sahraei.hamidreza.com.notella.Database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by hamidrezasahraei on 22/6/2017 AD.
 */

public class Converter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
