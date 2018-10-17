package com.beyondxia.modules;

/**
 * Create by ChenWei on 2018/8/31 10:17
 **/
public class PAServiceConfig {

    private volatile static Boolean isDebug = true;

    public static long maxLifeCycleInitWarningTime = 300;

    public static Boolean getIsDebug() {
        return isDebug;
    }

    public static void setIsDebug(Boolean isDebug) {
        PAServiceConfig.isDebug = isDebug;
    }
}
