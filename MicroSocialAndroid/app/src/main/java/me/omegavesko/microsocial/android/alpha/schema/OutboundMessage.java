package me.omegavesko.microsocial.android.alpha.schema;

/**
 * Created by Veselin on 4/5/2015.
 */
public class OutboundMessage
{
    String session_id;
    String to;
    String message;

    public OutboundMessage(String session_id, String to, String message)
    {
        this.session_id = session_id;
        this.to = to;
        this.message = message;
    }
}
