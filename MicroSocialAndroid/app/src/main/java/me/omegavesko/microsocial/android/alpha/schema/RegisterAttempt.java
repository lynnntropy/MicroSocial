package me.omegavesko.microsocial.android.alpha.schema;

/**
 * Created by Veselin on 4/4/2015.
 */
public class RegisterAttempt
{
    String username, password, fullName, email;

    public RegisterAttempt(String username, String password, String fullName, String email)
    {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
    }
}
