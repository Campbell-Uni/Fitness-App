package grp2.fitness.Helpers;

import android.icu.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {

    StringUtils(){}

    public static String getCurrentDateFormatted(){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(today);
    }

}
