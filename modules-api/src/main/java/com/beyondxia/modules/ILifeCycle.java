package com.beyondxia.modules;

/**
 * Created by xiaojunxia on 2018/9/25.
 */

public interface ILifeCycle {
//    public abstract void onInit();

    /**
    *
     * we suggest you write code here in a new thread, especially it wait a long time
     */
    public abstract void onInstalled();
    public abstract void onUninstalled();
}
