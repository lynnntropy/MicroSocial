// OUT OF ORDER ON ANDROID
// uh. no java.nio.file package on Android.
// have to reimplement that stuff with Android's proper SDK code instead.

package me.omegavesko.microsocial.android.alpha;

import android.content.Context;
import android.content.SharedPreferences;

import me.omegavesko.microsocial.android.alpha.AuthToken;
import me.omegavesko.microsocial.android.alpha.Users;

public class AuthTokenManager
{
    Context context;

    public AuthTokenManager(Context context)
    {
        this.context = context;
    }

//    public static AuthToken createToken(String username)
//    {
//        // check if user exists
//        if (Users.getUser(username) != null)
//        {
//            // generate a token string
//            String tokenString = null;
//            tokenString = UUID.randomUUID().toString();
//
//            // create an AuthToken object
//            AuthToken token = new AuthToken(username, tokenString);
//
//            // add it to the database
//            String dbString = String.format("%s: %s", username, tokenString);
//
//            List<String> dbStringList = new ArrayList<String>();
//            dbStringList.add(dbString);
//
//            FileHandler.writeLines("tokens.db", dbStringList);
//
//            return token;
//        }
//        else
//        {
//            return null;
//        }
//    }
//
//    public static boolean checkToken(AuthToken token)
//    {
//        List<String> dbStrings = FileHandler.readAllLines("tokens.db");
//
//        for (String line : dbStrings)
//        {
//            String[] segments = line.split(": ");
//
//            if (segments[0].equals(token.username) && segments[1].equals(token.tokenString))
//            {
//                // token is valid, return true
//                return true;
//            }
//        }
//
//        // no valid token found, return false
//        return false;
//    }

    public AuthToken getClientToken()
    {
//        if (FileHandler.fileExists("token.txt"))
//        {
//            List<String> tokenContents = FileHandler.readAllLines("token.txt");
//            String[] tokenLine = tokenContents.get(0).split(",");
//
//            return new AuthToken(tokenLine[0], tokenLine[1]);
//        }
//        else
//        {
//            return new AuthToken();
//        }

        SharedPreferences preferences = this.context.getSharedPreferences("token", 0);

//        return new AuthToken
//                (preferences.getString("username", ""), preferences.getString("tokenString", ""));

        // return omegavesko every time, for debug purposes
        return new AuthToken
                ("omegavesko", preferences.getString("tokenString", ""));
    }

    public void setClientToken(AuthToken token)
    {
//        if (FileHandler.fileExists("token.txt"))
//        {
//            try
//            {
//                Files.delete(Paths.get("token.txt"));
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        String dbLine = String.format("%s,%s", token.username, token.tokenString);
//        List<String> dbLines = new ArrayList<String>();
//        dbLines.add(dbLine);
//
//        FileHandler.writeLines("token.txt", dbLines);

        SharedPreferences.Editor editor = this.context.getSharedPreferences("token", 0).edit();
        editor.putString("username", token.username);
        editor.putString("tokenString", token.tokenString);
        editor.commit();
    }

//    public static void addServerToken(AuthToken token)
//    {
////        if (FileHandler.fileExists("token.txt"))
////        {
////            try
////            {
////                Files.delete(Paths.get("token.txt"));
////            }
////            catch (IOException e)
////            {
////                e.printStackTrace();
////            }
////        }
//
//        String dbLine = String.format("%s: %s", token.username, token.tokenString);
//        List<String> dbLines = new ArrayList<String>();
//        dbLines.add(dbLine);
//
//        FileHandler.writeLines("tokens.db", dbLines);
//    }
}
