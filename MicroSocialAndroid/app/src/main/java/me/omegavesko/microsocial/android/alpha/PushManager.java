// OUT OF ORDER ON ANDROID
// nothing specific to fix here - it just relies on FileHandler, which
// has yet to be updated.

//package me.omegavesko.microsocial.android.alpha;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PushManager
//{
//    static void pushMessage (Object toPush)
//    {
//        try
//        {
//            if (toPush instanceof ChatMessage)
//            {
//                writeLog("Received ChatMessage to push to client..");
//                // push it to the recepient(s)
//                ChatMessage message = (ChatMessage) toPush;
//
//                String recipient = message.recepientUserName;
//                String ip = getAddress(recipient);
//
//
//                writeLog(String.format("Opening socket to %s:%s", ip, "9001"));
//                Socket socket = new Socket(ip, 9001);
//                writeLog(String.format("Socket opened."));
//
////                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
////                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
//                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
////                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
//                writeLog("Created socket and object streams.");
//
//                writeLog("Sending ChatMessage to client..");
//                output.writeObject(message);
//                writeLog("Complete.");
//
//            }
//            else if (toPush instanceof UserStatus)
//            {
//                // push it to everyone
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    static void registerClient(InetAddress address, String username)
//    {
//        FileHandler.writeLine("pushclients.tmp",
//                String.format("%s: %s", username, address.getHostAddress()));
//    }
//
//    static void clearClients()
//    {
//        if (FileHandler.fileExists("pushclients.tmp"))
//        {
//            try
//            {
//                new FileWriter("pushclients.tmp").close();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    static List<String> getAddresses (List<String> usernames)
//    {
//        List<String> lines = FileHandler.readAllLines("pushclients.tmp");
//        List<String> addresses = new ArrayList<String>();
//
//        for (String line : lines)
//        {
//            String[] segments = line.split(": ");
//
//            for (String username : usernames)
//            {
//                if (segments[0].equals(username))
//                {
//                    // found one
//                    addresses.add(segments[1]);
//                }
//            }
//        }
//
//        return addresses;
//    }
//
//    static String getAddress(String username)
//    {
//        List<String> lines = FileHandler.readAllLines("pushclients.tmp");
//
//        for (String line : lines)
//        {
//            String[] segments = line.split(": ");
//
//            if (segments[0].equals(username))
//            {
//                return segments[1];
//            }
//        }
//
//        return null;
//    }
//
//    static void writeLog(String log)
//    {
//        System.out.println("[PushManager] " + log);
//    }
//
//}
