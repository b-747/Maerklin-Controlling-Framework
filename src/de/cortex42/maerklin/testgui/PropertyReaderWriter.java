package de.cortex42.maerklin.testgui;

import java.io.*;
import java.util.Properties;

/**
 * Created by ivo on 07.11.15.
 */
public final class PropertyReaderWriter {

    private PropertyReaderWriter(){}

    public static String readPropertyValue(String file, String key) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(file);

        properties.load(inputStream);

        return properties.getProperty(key);
    }

    public static void writePropertyValue(String file, String key, String value) throws IOException {
        Properties properties = new Properties();
        OutputStream outputStream = new FileOutputStream(file);

        properties.setProperty(key, value);

        properties.store(outputStream, null);
    }
}
