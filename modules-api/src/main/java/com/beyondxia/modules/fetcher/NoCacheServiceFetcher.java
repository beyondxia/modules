package com.beyondxia.modules.fetcher;

import com.beyondxia.modules.PAServiceEvn;

/**
 * Create by ChenWei on 2018/8/23 16:27
 * <p>
 * 无缓存方式, 不经常使用的Service
 **/
public abstract class NoCacheServiceFetcher<T>
        extends PAServiceEvn.ServiceFetcher implements ServiceFactory<T> {
    @Override
    public final Object getService(PAServiceEvn ctx) {
        return createService();
    }
}
