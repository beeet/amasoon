package org.books.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Cryptor {

    public static String hash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return new String(MessageDigest.getInstance("SHA-256").digest(input.getBytes("UTF-8")));
    }

}
