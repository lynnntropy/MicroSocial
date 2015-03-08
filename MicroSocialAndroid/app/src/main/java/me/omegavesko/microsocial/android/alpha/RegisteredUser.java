package me.omegavesko.microsocial.android.alpha;

import java.io.Serializable;

public class RegisteredUser implements Serializable
{
    static final long serialVersionUID = 1L;

    public long databaseID;

    public String userPhoneNumber;
    public String userRealName;
    public String userName;

    public String passwordHash;

    public RegisteredUser() {}

    public RegisteredUser(String userPhoneNumber, String userName, String userRealName, String passwordHash)
    {
        this.userPhoneNumber = userPhoneNumber;
        this.userName = userName;
        this.userRealName = userRealName;

        this.passwordHash = passwordHash;
    }
}
