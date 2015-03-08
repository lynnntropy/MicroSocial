package me.omegavesko.microsocial.android.alpha;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Veselin on 1/6/2015.
 */
public class DatabaseManager extends SQLiteOpenHelper
{
    ///// General DB info

    private static final String DATABASE_NAME = "microsocial.db";
    private static final int DATABASE_VERSION = 1;

    ///// Users table

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USERID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_DISPLAYNAME = "displayname";
    public static final String COLUMN_PHONENUMBER = "phonenumber";

    private static final String CREATE_TABLE_USERS = "create table "
            + TABLE_USERS + "(" +
            COLUMN_USERID + " integer primary key autoincrement, " +
            COLUMN_USERNAME + " text not null, " +
            COLUMN_DISPLAYNAME + " text not null, " +
            COLUMN_PHONENUMBER + " text not null);";

    ///// Messages table

    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGEID = "id";
    public static final String COLUMN_SENDER = "sender";
    public static final String COLUMN_RECIPIENT = "recipient";
    public static final String COLUMN_MESSAGETEXT = "messagetext";

    private static final String CREATE_TABLE_MESSAGES = "create table "
            + TABLE_MESSAGES + "(" +
            COLUMN_MESSAGEID + " integer primary key autoincrement, " +
            COLUMN_SENDER + " text not null, " +
            COLUMN_RECIPIENT + " text not null, " +
            COLUMN_MESSAGETEXT + " text not null);";

    public DatabaseManager(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(DatabaseManager.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);

        onCreate(db);
    }
}
