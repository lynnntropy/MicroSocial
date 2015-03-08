package me.omegavesko.microsocial.android.alpha;

import java.io.Serializable;

public class ResponseCode implements Serializable
{
    static final long serialVersionUID = 1L;

    public enum Code
    {
        CONTINUE,
        SUCCESS,
        FAILURE,
        ACCESS_DENIED
    }

    public Code code;

    public ResponseCode(Code code)
    {
        this.code = code;
    }

    public String toString()
    {
        return code.name();
    }
}
