package me.omegavesko.microsocial.android.alpha.server;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

public class Server
{
    public Server()
    {

    }

    public void start()
    {
        Component serverComponent = new Component();
        serverComponent.getServers().add(Protocol.HTTP, 80);
        final Router router = new Router(serverComponent.getContext().createChildContext());
        router.attach("/api", SocialServerResource.class);
        serverComponent.getDefaultHost().attach(router);

        try
        {
            serverComponent.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
