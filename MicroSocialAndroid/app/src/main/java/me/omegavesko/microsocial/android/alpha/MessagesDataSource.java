package me.omegavesko.microsocial.android.alpha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Veselin on 1/8/2015.
 */
public class MessagesDataSource
{
    private Context context;

    // Database fields
    private SQLiteDatabase database;
    private DatabaseManager dbManager;

    private String[] allColumns = {
            DatabaseManager.COLUMN_MESSAGEID,
            DatabaseManager.COLUMN_SENDER,
            DatabaseManager.COLUMN_RECIPIENT,
            DatabaseManager.COLUMN_MESSAGETEXT };

    public MessagesDataSource(Context context)
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

    public ChatMessage storeMessage(ChatMessage message)
    {
        open();

        ContentValues values = new ContentValues();
        values.put(DatabaseManager.COLUMN_SENDER, message.sender);
        values.put(DatabaseManager.COLUMN_RECIPIENT, message.recepientUserName);
        values.put(DatabaseManager.COLUMN_MESSAGETEXT, message.messageText);

        long insertId = database.insert(DatabaseManager.TABLE_MESSAGES, null,
                values);

        Cursor cursor = database.query(DatabaseManager.TABLE_MESSAGES,
                allColumns, DatabaseManager.COLUMN_MESSAGEID + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();
        ChatMessage chatMessage = cursorToMessage(cursor);
        cursor.close();

        close();

        return chatMessage;
    }

    public void deleteMessage(ChatMessage chatMessage)
    {
        open();

        long id = chatMessage.databaseID;
        System.out.println("Message deleted with id: " + id);
        database.delete(DatabaseManager.TABLE_MESSAGES, DatabaseManager.COLUMN_MESSAGEID
                + " = " + id, null);

        close();
    }

    public List<ChatMessage> getAllMessages()
    {
        open();

        List<ChatMessage> messages = new ArrayList<ChatMessage>();

        Cursor cursor = database.query(DatabaseManager.TABLE_MESSAGES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            ChatMessage chatMessage = cursorToMessage(cursor);
            messages.add(chatMessage);
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();

        close();

        return messages;
    }

    private ChatMessage cursorToMessage(Cursor cursor)
    {
        open();

        ChatMessage message = new ChatMessage();

        message.databaseID = cursor.getLong(0);
        message.sender = cursor.getString(1);
        message.recepientUserName = cursor.getString(2);
        message.messageText = cursor.getString(3);

        close();

        return message;
    }

//    public List<RegisteredUser> refreshUsersFromServer()
//    {
//        try
//        {
//            ObjectSocket objectSocket =
//                    ServerConnector.connect(
//                            "192.168.1.100", 9000,
//                            new RequestCode(RequestCode.Code.GET_USERS, new AuthTokenManager(context).getClientToken()));
//
//            List<RegisteredUser> users = (List<RegisteredUser>) objectSocket.inputStream.readObject();
//
//            clearAllUsers();
//
//            for (RegisteredUser user : users)
//            {
//                this.storeMessage(user);
//            }
//
//            // Return a list of users from the server instead of returning nothing
//
//            return users;
//        }
//        catch (Exception e)
//        {
//            Log.e("UDS", "exception", e);
//            return null;
//        }
//    }

    /**
     * Ask the server for the newest message from each user. This ensures that we don't have to download
     * every message ever sent just to display the most recent ones.
     */
    public List<ChatMessage> getLatestMessagesFromServer()
    {
        try
        {
            ObjectSocket objectSocket =
                    ServerConnector.connect(
                            "192.168.1.100", 9000,
                            new RequestCode(RequestCode.Code.GET_LATEST_MESSAGES, new AuthTokenManager(context).getClientToken(), null));

            List<ChatMessage> messages = (List<ChatMessage>) objectSocket.inputStream.readObject();

            clearAllMessages();

            for (ChatMessage message : messages)
            {
                this.storeMessage(message);
            }

            // Return a list of messages from the server instead of returning nothing
            return messages;
        }
        catch (Exception e)
        {
            Log.e("MDS", "exception", e);
            return null;
        }
    }

    public void clearAllMessages()
    {
        open();

        this.database
                .execSQL("delete from " + DatabaseManager.TABLE_MESSAGES);

        close();
    }

}