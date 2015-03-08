package me.omegavesko.microsocial.android.alpha;

import java.io.Serializable;

/**
 * Created by Veselin on 12/28/2014.
 */
public class AuthToken implements Serializable
{
    static final long serialVersionUID = 1L;

    public String username;
    public String tokenString;

    boolean insecureToken;

    public AuthToken(String username, String tokenString)
    {
        this.username = username;
        this.tokenString = tokenString;
    }

    public AuthToken()
    {
        this.insecureToken = true;
        this.username = "insecure";
    }

}
