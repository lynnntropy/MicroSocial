package me.omegavesko.microsocial.android.alpha;

import java.io.Serializable;

public class RequestCode implements Serializable
{
    static final long serialVersionUID = 5L;

    public enum Code
    {
        GET_NETWORK_INFO,

        GET_USERS, // no params

        GET_LATEST_MESSAGES, // no params
        GET_MESSAGES_FROM_USER, // parameters: username (or 'all' for general chat), start message (1 = newest), end message

        GET_STATUSES, // parameters: number of statuses to retrieve. 0 = all statuses in DB

        REGISTER_PUSH, // no params
        REGISTER_USER, // no params
        LOGIN_ATTEMPT, // no params

        SEND_MESSAGE, // no paramsNetworkInfo

        POST_STATUS // no params
    }

    public Code code;
    public AuthToken token;
    public String[] additionalParameters;

    public RequestCode(Code code, AuthToken token, String[] additionalParameters)
    {
        this.code = code;
        this.token = token;
        this.additionalParameters = additionalParameters;
    }

    public RequestCode(Code code, AuthToken token)
    {
        this.code = code;
        this.token = token;
        this.additionalParameters = null;
    }

    public RequestCode(Code code)
    {
        this.code = code;
        this.token = new AuthToken();
        this.additionalParameters = null;
    }

    public String toString()
    {
        String returnString = "";
        returnString += code.name();
        if (token != null) returnString += String.format(" (token for %s)", token.username);

        return returnString;
    }
}
