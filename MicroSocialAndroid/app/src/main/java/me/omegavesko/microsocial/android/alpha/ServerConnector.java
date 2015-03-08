package me.omegavesko.microsocial.android.alpha;

import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerConnector
{
    public static ObjectSocket connect(String host, int port, RequestCode requestCode)
    {
        try
        {
            writeLog("Opening socket..");
            Socket socket = new Socket();

            // 2s timeout currently set, could be increased/decreased following testing
            socket.connect(new InetSocketAddress(host, port), 2000);

            writeLog("Opening streams..");
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            writeLog("Sending request " + requestCode.toString() + "..");
            output.writeObject(requestCode);

            writeLog("Waiting for response..");
            ResponseCode response = (ResponseCode) input.readObject();

            writeLog("Received response: " + response.toString());

            if (response != null && response.code == ResponseCode.Code.CONTINUE)
            {
                return new ObjectSocket(socket, input, output, response);
            }
            else
            {
                return new ObjectSocket(null, null, null, response);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new ObjectSocket(null, null, null, new ResponseCode(ResponseCode.Code.FAILURE));
        }
    }

    private static void writeLog(String log)
    {
        Log.i("ServerConnector", log);
    }
}
