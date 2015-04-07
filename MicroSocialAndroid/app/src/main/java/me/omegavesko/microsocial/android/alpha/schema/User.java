package me.omegavesko.microsocial.android.alpha.schema;

public class User
{
    public String username;

    public String fullName;
    public String email;
    public String phoneNumber;

    public User()
    {

    }

    public User(String username)
    {
        this.username = username;
    }

    public User(String username, String fullName, String email, String phoneNumber)
    {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}