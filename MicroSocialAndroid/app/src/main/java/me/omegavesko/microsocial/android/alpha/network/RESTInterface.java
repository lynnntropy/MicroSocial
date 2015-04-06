package me.omegavesko.microsocial.android.alpha.network;

import java.util.List;

import me.omegavesko.microsocial.android.alpha.schema.CheckSession;
import me.omegavesko.microsocial.android.alpha.schema.LoginAttempt;
import me.omegavesko.microsocial.android.alpha.schema.Message;
import me.omegavesko.microsocial.android.alpha.schema.NewestMessages;
import me.omegavesko.microsocial.android.alpha.schema.OutboundMessage;
import me.omegavesko.microsocial.android.alpha.schema.ReceivedFeed;
import me.omegavesko.microsocial.android.alpha.schema.ReceivedMessages;
import me.omegavesko.microsocial.android.alpha.schema.RegisterAttempt;
import me.omegavesko.microsocial.android.alpha.schema.Status;
import me.omegavesko.microsocial.android.alpha.schema.User;
import me.omegavesko.microsocial.android.alpha.schema.UserList;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface RESTInterface
{
    // authentication methods

    @POST("/session")
    Response login(@Body LoginAttempt loginAttempt);

    @POST("/session/check")
    Response checkSession(@Body CheckSession checkSession);

    @POST ("/register")
    Response registerUser(@Body RegisterAttempt registerAttempt);

    // general methods

    @GET("/serverInfo")
    Response serverInfo();

    // user methods

    @GET("/getUsers")
    UserList listUsers();

    // feed/status methods

    @GET("/feed")
    ReceivedFeed getFeed(@Query("first") int first, @Query("last") int last);

    // messaging methods

    @GET("/messages")
    ReceivedMessages getMessages(@Query("session") String session, @Query("user") String user, @Query("first") int first, @Query("last") int last);

    @GET("/newestMessages")
    NewestMessages getNewestMessages(@Query("session") String session, @Query("first") int first, @Query("last") int last);

    @POST("/message")
    Response sendMessage(@Body OutboundMessage outboundMessage);
}
