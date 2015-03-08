package me.omegavesko.microsocial.android.alpha;

import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PushListener extends Thread
{
    ServerSocket serverSocket;

    public PushListener(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    public void run()
    {
        boolean listenerRunning = true;

        while (listenerRunning)
        {
            try
            {
                writeLog("Push listener waiting for connection on port " + serverSocket.getLocalPort());

                Socket socket = serverSocket.accept();
                writeLog("Received connection from " + socket.getInetAddress());

                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                writeLog("Created object streams.");

                Object pushObject = input.readObject();

                if (pushObject != null)
                {
                    if (pushObject instanceof ChatMessage)
                    {
                        ChatMessage message = (ChatMessage) pushObject;

                        writeLog(String.format(
                            "Received message push:\n<%s -> %s> %s",
                            message.sender, message.recepientUserName, message.messageText));
                    }
                    else if (pushObject instanceof UserStatus)
                    {
                        writeLog("Received UserStatus push, not supported yet.");
                    }
                }

                socket.close();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    void writeLog(String log)
    {
        Log.i("PushListener", log);
    }
}
