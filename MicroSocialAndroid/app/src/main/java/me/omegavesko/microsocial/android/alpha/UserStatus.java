package me.omegavesko.microsocial.android.alpha;

import java.io.Serializable;

/**
 * Created by Veselin on 12/25/2014.
 */
public class UserStatus implements Serializable
{
    static final long serialVersionUID = 1L;

    String ownerUserName;
    String statusText;

    public UserStatus(String ownerUserName, String statusText)
    {
        this.ownerUserName = ownerUserName;
        this.statusText = statusText;
    }

    // TODO: Add a field to hold a potential compressed image
}
