package me.omegavesko.microsocial.android.alpha.schema;

import java.util.List;

/**
 * Created by Veselin on 4/5/2015.
 */
public class NewestMessages
{
    public String user;
    public int first;
    public int last;
    public List<Message> messages;

    public NewestMessages(String user, int first, int last, List<Message> messages)
    {
        this.user = user;
        this.first = first;
        this.last = last;
        this.messages = messages;
    }
}
