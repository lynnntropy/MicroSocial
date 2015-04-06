package me.omegavesko.microsocial.android.alpha.network;

import retrofit.RestAdapter;

/**
 * Created by Veselin on 4/2/2015.
 */
public class RESTManager
{
    public RESTInterface restInterface;
    private static RESTManager manager = null;

    protected RESTManager()
    {

    }

    public static RESTManager getManager()
    {
        if (manager == null)
        {
            manager = new RESTManager();
            manager.init();
        }

        return manager;
    }

    // TODO other methods..

    private void init()
    {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://192.168.1.100:9000") // TODO..
                .build();

        this.restInterface = restAdapter.create(RESTInterface.class);
    }
}
