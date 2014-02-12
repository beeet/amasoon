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

    private static final CredentialsLoader credentials = new CredentialsLoader();

    public static String getSignature(String operation) {
        credentials.load();
        try {
            Mac mac = Mac.getInstance(credentials.getMacAlgorithm());
            SecretKey key = new SecretKeySpec(credentials.getSecretAccessKey().getBytes(), credentials.getMacAlgorithm());
            mac.init(key);
            byte[] data = mac.doFinal((operation + getTimestamp()).getBytes());
            return encodeBase64(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTimestamp() {
        credentials.load();
        DateFormat dateFormat = new SimpleDateFormat(credentials.getTimestampFormat());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static String getAssociateTag() {
        credentials.load();
        return credentials.getAssociateTag();
    }

    private static String encodeBase64(byte[] data) {
        credentials.load();
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
                encoded += credentials.getBase64Chars().charAt(k);
            }
        }
        return encoded.substring(0, encoded.length() - padding) + "==".substring(0, padding);
    }
}
