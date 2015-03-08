package me.omegavesko.microsocial.android.alpha;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ObjectSocket
{
    public Socket rawSocket;
    public ObjectInputStream inputStream;
    public ObjectOutputStream outputStream;
    public ResponseCode response;

    public ObjectSocket(Socket rawSocket, ObjectInputStream inputStream, ObjectOutputStream outputStream, ResponseCode response)
    {
        this.rawSocket = rawSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;

        this.response = response;
    }
}
