package me.omegavesko.microsocial.android.alpha.schema;

/**
 * Created by Veselin on 4/7/2015.
 */
public class OutboundStatus
{
    String session_id;
    String status_body;

    public OutboundStatus(String session_id, String status_body)
    {
        this.session_id = session_id;
        this.status_body = status_body;
    }
}
