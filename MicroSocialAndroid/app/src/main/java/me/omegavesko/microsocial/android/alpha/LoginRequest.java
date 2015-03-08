package me.omegavesko.microsocial.android.alpha;

import java.io.Serializable;

public class LoginRequest implements Serializable
{
    static final long serialVersionUID = 1L;

    String username;
    String passwordHash;

    public LoginRequest(String username, String passwordHash)
    {
        this.username = username;
        this.passwordHash = passwordHash;
    }
}
