package me.omegavesko.microsocial.android.alpha;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Veselin on 4/5/2015.
 */
public class Utils
{
    public static Date jsonDateTimeToDate(String jsonDateTime)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try
        {
            return simpleDateFormat.parse(jsonDateTime);
        }
        catch (Exception e)
        {
            Log.e("DateParser", "e", e);
            return new Date();
        }
    }

    public static String getRelativeTimeString(Date date)
    {
        return DateUtils.getRelativeTimeSpanString(date.getTime()).toString();
    }
}
