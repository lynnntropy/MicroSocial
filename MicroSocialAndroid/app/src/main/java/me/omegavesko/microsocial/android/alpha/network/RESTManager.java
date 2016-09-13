package me.omegavesko.microsocial.android.alpha.network;

import android.content.Context;

import retrofit.RestAdapter;

/**
 * Created by Veselin on 4/2/2015.
 */
public class RESTManager
{
    public RESTInterface restInterface;
    private static RESTManager restManager = null;
    private static RestAdapter restAdapter;

    public String currentNetworkLocation;

    protected RESTManager()
    {

    }

    public static RESTManager getManager(Context context)
    {
        if (restManager == null)
        {
            restManager = new RESTManager();
            restManager.init(context);
        }

        return restManager;
    }

    public static void refreshServerLocation(Context context)
    {
        String serverLocation = context.getSharedPreferences("lastNetwork", 0).getString("ip", "192.168.1.1");
        if (restManager.currentNetworkLocation != serverLocation)
        {
            // need to update
            restManager.init(context);
        }
    }

    private void init(Context context)
    {
        String serverLocation = context.getSharedPreferences("lastNetwork", 0).getString("ip", "192.168.1.1");
        this.currentNetworkLocation = serverLocation;

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://" + serverLocation + ":9000")
                .build();

        this.restInterface = restAdapter.create(RESTInterface.class);
    }
}
