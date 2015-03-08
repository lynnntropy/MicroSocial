package me.omegavesko.microsocial.android.alpha;

//import  org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypto
{
    public static String getMD5(String string)
    {
        // use Apache library to get the MD5 of the input string
        // -- no longer doing this because intellij is retarded
        // return DigestUtils.md5Hex(string);

        // now using native Java MD5 implementation

        try
        {
            String plaintext = string;
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32)
            {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
