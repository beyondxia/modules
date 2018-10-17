package com.beyondxia.modules.fetcher;

import com.beyondxia.modules.PAServiceEvn;

/**
 * Create by ChenWei on 2018/8/23 16:12
 * <p>
 * 有缓存方式, 频繁使用的Service
 **/
public abstract class CacheServiceFetcher<T> extends PAServiceEvn.ServiceFetcher
        implements ServiceFactory<T> {

    @Override
    public final Object createService(PAServiceEvn ctx) {
        return createService();
    }
}
