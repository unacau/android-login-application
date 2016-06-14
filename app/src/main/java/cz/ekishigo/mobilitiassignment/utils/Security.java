package cz.ekishigo.mobilitiassignment.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ekishigo on 13.6.16.
 */
public class Security {

    public static String encodeSha1(String input) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest mDigest = null;
            mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(input.getBytes());
            for (int i = 0; i < result.length; i++) {
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // TODO log
        }
        return sb.toString();
    }
}
