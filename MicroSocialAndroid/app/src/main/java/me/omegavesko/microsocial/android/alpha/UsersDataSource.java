package me.omegavesko.microsocial.android.alpha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UsersDataSource
{
    private Context context;

    // Database fields
    private SQLiteDatabase database;
    private DatabaseManager dbManager;

    private String[] allColumns = {
            DatabaseManager.COLUMN_USERID,
            DatabaseManager.COLUMN_USERNAME,
            DatabaseManager.COLUMN_DISPLAYNAME,
            DatabaseManager.COLUMN_PHONENUMBER };

    public UsersDataSource(Context context)
    {
        this.context = context;
        dbManager = new DatabaseManager(context);
    }

    public void open() throws SQLException
    {
        database = dbManager.getWritableDatabase();
    }

    public void close()
    {
        dbManager.close();
    }

    public RegisteredUser createUser(RegisteredUser user)
    {
        open();

        ContentValues values = new ContentValues();
        values.put(DatabaseManager.COLUMN_USERNAME, user.userName);
        values.put(DatabaseManager.COLUMN_DISPLAYNAME, user.userRealName);
        values.put(DatabaseManager.COLUMN_PHONENUMBER, user.userPhoneNumber);

        long insertId = database.insert(DatabaseManager.TABLE_USERS, null,
                values);

        Cursor cursor = database.query(DatabaseManager.TABLE_USERS,
                allColumns, DatabaseManager.COLUMN_USERID + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();
        RegisteredUser newUser = cursorToUser(cursor);
        cursor.close();

        close();

        return newUser;
    }

    public void deleteUser(RegisteredUser user)
    {
        open();

        long id = user.databaseID;
        System.out.println("User deleted with id: " + id);
        database.delete(DatabaseManager.TABLE_USERS, DatabaseManager.COLUMN_USERID
                + " = " + id, null);

        close();
    }

    public List<RegisteredUser> getAllUsers()
    {
        open();

        List<RegisteredUser> comments = new ArrayList<RegisteredUser>();

        Cursor cursor = database.query(DatabaseManager.TABLE_USERS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            RegisteredUser comment = cursorToUser(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();

        close();

        return comments;
    }

    private RegisteredUser cursorToUser(Cursor cursor)
    {
        open();

        RegisteredUser user = new RegisteredUser();

        user.databaseID = cursor.getLong(0);
        user.userName = cursor.getString(1);
        user.userRealName = cursor.getString(2);
        user.userPhoneNumber = cursor.getString(3);

        close();

        return user;
    }

    public List<RegisteredUser> refreshUsersFromServer()
    {
        try
        {
            ObjectSocket objectSocket =
                    ServerConnector.connect(
                            "192.168.1.100", 9000,
                            new RequestCode(RequestCode.Code.GET_USERS, new AuthTokenManager(context).getClientToken()));

            List<RegisteredUser> users = (List<RegisteredUser>) objectSocket.inputStream.readObject();

            // TODO: Clear all users from the table first
            clearAllUsers();

            for (RegisteredUser user : users)
            {
                this.createUser(user);
            }

            // Return a list of users from the server instead of returning nothing

            return users;
        }
        catch (Exception e)
        {
            Log.e("UDS", "exception", e);
            return null;
        }
    }

    public void clearAllUsers()
    {
        open();

        this.database
                .execSQL("delete from " + DatabaseManager.TABLE_USERS);

        close();
    }

}