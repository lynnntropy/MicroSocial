package me.omegavesko.microsocial.android.alpha.schema;

import java.util.Date;

public class Status
{
    public User poster;
    public String time; // DateTime string
    public String statusContent;

    public Status()
    {

    }

    public Status(User poster, String time, String statusContent)
    {
        this.poster = poster;
        this.time = time;
        this.statusContent = statusContent;
    }
}