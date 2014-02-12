package org.books.utils;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SignatureGenerator {

    private static final CredentialsLoader loader = new CredentialsLoader();

    
    public static Credentials generate(String operation){
        loader.load();
        Credentials credentials = new Credentials();
        credentials.setTimestamp(getTimestamp());
        credentials.setSignature(getSignature(operation,credentials.getTimestamp()));
        credentials.setAccessKeyId(getAccessKeyId());
        return credentials;
    }
    private static String getSignature(String operation, String timestamp) {
        try {
            Mac mac = Mac.getInstance(loader.getMacAlgorithm());
            SecretKey key = new SecretKeySpec(loader.getSecretAccessKey().getBytes(), loader.getMacAlgorithm());
            mac.init(key);
            byte[] data = mac.doFinal((operation + timestamp).getBytes());
            return encodeBase64(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat(loader.getTimestampFormat());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static String getAccessKeyId() {
        return loader.getAccessKeyId();
    }

    private static String encodeBase64(byte[] data) {
        String encoded = "";
        int padding = (3 - data.length % 3) % 3;
        byte[] padded = new byte[data.length + padding];
        System.arraycopy(data, 0, padded, 0, data.length);
        byte[] buffer = new byte[3];
        for (int i = 0; i < padded.length; i += 3) {
            System.arraycopy(padded, i, buffer, 0, 3);
            int n = new BigInteger(1, buffer).intValue();
            for (int j = 3; j >= 0; j--) {
                int k = (n >> (6 * j)) & 0x3f;
                encoded += loader.getBase64Chars().charAt(k);
            }
        }
        return encoded.substring(0, encoded.length() - padding) + "==".substring(0, padding);
    }
}
