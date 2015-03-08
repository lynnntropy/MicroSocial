package me.omegavesko.microsocial.android.alpha;

import java.io.Serializable;

public class NetworkIdent implements Serializable
{
    static final long serialVersionUID = 1L;

    public String networkName;

    public NetworkIdent(String networkName)
    {
        this.networkName = networkName;
    }
}
