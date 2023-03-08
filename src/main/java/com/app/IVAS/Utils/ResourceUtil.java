package com.app.IVAS.Utils;

import java.io.InputStream;
import java.net.URL;

public class ResourceUtil {

    public static InputStream getResourceAsStream(String fileName) {
//        System.out.println(fileName);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        InputStream stream = classloader.getResourceAsStream(fileName);
//        System.out.println(stream);
        return stream;
    }

    public static URL getResourceAsUrl(String fileName) {
//        System.out.println(fileName);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResource(fileName);
    }
}
