package me.omegavesko.microsocial.android.alpha;

/**
 * Created by Veselin on 1/8/2015.
 */
public class NetworkInfo
{
    public String networkName;
    public String networkIP;
    public int networkPort;

    public NetworkInfo(String networkName, String networkIP, int networkPort)
    {
        this.networkName = networkName;
        this.networkIP = networkIP;
        this.networkPort = networkPort;
    }
}
