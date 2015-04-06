package me.omegavesko.microsocial.android.alpha.schema;

import java.util.List;

/**
 * Created by Veselin on 4/5/2015.
 */
public class ReceivedMessages
{
    public String user1;
    public String user2;
    public int first;
    public int last;
    public List<Message> messages;

    public ReceivedMessages(String user1, String user2, int first, int last, List<Message> messages)
    {
        this.user1 = user1;
        this.user2 = user2;
        this.first = first;
        this.last = last;
        this.messages = messages;
    }
}
