package me.omegavesko.microsocial.android.alpha.server;

import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class SocialServerResource extends ServerResource
{
    @Get
    public String toString() {
        return "hello, world";
    }

}