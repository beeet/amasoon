package org.books.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CredentialsLoader {

    public static final String TIMESTAMP_FORMAT = "TIMESTAMP_FORMAT";
    public static final String MAC_ALGORITHM = "MAC_ALGORITHM";
    public static final String BASE64_CHARS = "BASE64_CHARS";
    public static final String ACCESS_KEY_ID = "ACCESS_KEY_ID";
    public static final String SECRET_ACCESS_KEY = "SECRET_ACCESS_KEY";
    public static final String ASSOCIACTE_TAG = "ASSOCIACTE_TAG";
    private Properties props;

    public CredentialsLoader() {
        load();
    }

    private void load() {
        if (null != props) {
            return;
        }
        try {
            props = new Properties();
            props.load(new FileReader("src/credentials.properties"));
        } catch (IOException ex) {
            Logger.getLogger(CredentialsLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getTimestampFormat() {
        return props.getProperty(TIMESTAMP_FORMAT);
    }

    public String getMacAlgorithm() {
        return props.getProperty(MAC_ALGORITHM);
    }

    public String getBase64Chars() {
        return props.getProperty(BASE64_CHARS);
    }

    public String getAccessKeyId() {
        return props.getProperty(ACCESS_KEY_ID);
    }

    public String getAssociateTag() {
        return props.getProperty(ASSOCIACTE_TAG);
    }

    public String getSecretAccessKey() {
        return props.getProperty(SECRET_ACCESS_KEY);
    }

}
