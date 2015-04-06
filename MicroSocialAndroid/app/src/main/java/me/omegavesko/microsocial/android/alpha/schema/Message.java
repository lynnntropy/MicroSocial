package me.omegavesko.microsocial.android.alpha.schema;

import java.util.Date;

public class Message
{
    public String messageBody;
    public String time; // SQLite DateTime string, GSON doesn't understand it
    public String senderName;
    public String recipientName;
    public String senderEmail;

    public String displayName;

    public Message(String messageBody, String time, String senderName, String recipientName)
    {
        this.messageBody = messageBody;
        this.time = time;
        this.senderName = senderName;
        this.recipientName = recipientName;
    }

    public Message()
    {
    }
}