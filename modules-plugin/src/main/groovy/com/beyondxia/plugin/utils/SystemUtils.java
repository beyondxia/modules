package com.beyondxia.plugin.utils;

/**
 * Create by ChenWei on 2018/10/23 17:37
 **/
public class SystemUtils {

    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    public static String getPathByOs(String originPath) {
        if (originPath == null) {
            return "";
        }
        if (isWindows()) {
            return originPath.replaceAll("/", "\\");
        }
        return originPath;

    }
}
