package com.beyondxia.modules.fetcher;

import com.beyondxia.modules.PAServiceEvn;

/**
 * Create by ChenWei on 2018/8/23 16:28
 * <p>
 * 单例模式, 介于无缓存和有缓存之间
 **/
public abstract class SingleInstanceServiceFetcher<T>
        extends PAServiceEvn.StaticServiceFetcher implements ServiceFactory<T> {

    @Override
    public final Object createStaticService() {
        return createService();
    }
}
