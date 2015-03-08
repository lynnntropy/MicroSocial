package me.omegavesko.microsocial.android.alpha;

import java.io.Serializable;

public class ChatMessage implements Serializable
{
    static final long serialVersionUID = 1L;

    public enum MessageType
    {
        TEXT_MESSAGE,
        IMAGE_MESSAGE
    }

    public long databaseID;
    public String recepientUserName;
    public String sender;
    public String messageText;

    public MessageType type;

    public ChatMessage() {}

    public ChatMessage(String sender, String recepientUserName, String messageText, MessageType type)
    {
        this.sender = sender;
        this.recepientUserName = recepientUserName;
        this.messageText = messageText;

        this.type = type;
    }

    public String toString()
    {
        return String.format("[%s -> %s] %s", sender, recepientUserName, messageText);
    }

    // TODO: Implement a field that holds a compressed image for image messages
}
