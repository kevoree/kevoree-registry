package org.kevoree.registry.server.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by leiko on 16/12/14.
 */
public class Properties {

    /**
     * Gets the value from -Dkey or "config.properties" if -Dkey is not set
     * Or null if none
     * @param key
     * @return
     */
    public static String get(String key) {
        String value;
        if (System.getProperty(key) != null) {
            value = System.getProperty(key);
        } else {
            java.util.Properties props = new java.util.Properties();
            InputStream input = null;
            try {
                input = new FileInputStream("config.properties");
                props.load(input);
                value = props.getProperty(key);
            } catch (IOException e) {
                e.printStackTrace();
                value = null;
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return value;
    }
}
