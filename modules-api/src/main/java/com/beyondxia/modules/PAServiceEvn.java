package com.beyondxia.modules;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Create by ChenWei on 2018/8/23 15:51
 **/
public class PAServiceEvn {
    private static PAServiceEvn sInstance = new PAServiceEvn();
    private Application application;
    private FragmentActivity curActivity;
    private static final HashMap<String, ServiceFetcher> FEIFAN_SERVICE_MAP =
            new HashMap<>();
    private static int sNextPerContextServiceCacheIndex = 0;

    public static PAServiceEvn getInstance() {
        return sInstance;
    }

    private PAServiceEvn() {

    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application app) {
        this.application = app;
    }

    public Activity getCurActivity() {
        return curActivity;
    }

    public void setCurActivity(FragmentActivity curActivity) {
        this.curActivity = curActivity;
    }

    public void destroyCurActivity(Activity curActivity) {
        if (this.curActivity == curActivity) {
            this.curActivity = null;
        }
    }

    public FragmentManager getFragmentManager() {
        return null == curActivity ? null : curActivity.getSupportFragmentManager();
    }

    /**
     * 注册模块对应的Service
     *
     * @param serviceName 注册的Service名称
     * @param fetcher     ServiceFetcher或StaticServiceFetcher
     */
    public static void registerService(String serviceName, ServiceFetcher fetcher) {
        if (!(fetcher instanceof StaticServiceFetcher)) {
            fetcher.mContextCacheIndex = sNextPerContextServiceCacheIndex++;
        }
        FEIFAN_SERVICE_MAP.put(serviceName, fetcher);
    }

    public static Object getService(String name) {
        ServiceFetcher fetcher = FEIFAN_SERVICE_MAP.get(name);
        return fetcher == null ? null : fetcher.getService(getInstance());
    }


    private final List<Object> mServiceCache = new ArrayList<>();

    /**
     * Override this class when the the service constructor needs a FFanEnv.  Else, use StaticServiceFetcher below.
     */
    public static class ServiceFetcher {
        int mContextCacheIndex = -1;

        /**
         * Main entrypoint; only override if you don't need caching.
         */
        public Object getService(PAServiceEvn ctx) {
            Object service;
            synchronized (ctx.mServiceCache) {
                List<Object> cache = ctx.mServiceCache;
                if (cache.size() == 0) {
                    // Initialize the cache vector on first access.
                    // At this point sNextPerContextServiceCacheIndex
                    // is the number of potential services that are
                    // cached per-Context.
                    for (int i = 0; i < sNextPerContextServiceCacheIndex; i++) {
                        cache.add(null);
                    }
                } else {
                    service = cache.get(mContextCacheIndex);
                    if (service != null) {
                        return service;
                    }
                }
                service = createService(ctx);
                cache.set(mContextCacheIndex, service);
                return service;
            }
        }

        public Object createService(PAServiceEvn ctx) {
            throw new RuntimeException("Not implemented");
        }
    }

    /**
     * Override this class for services to be cached process-wide.
     */
    public abstract static class StaticServiceFetcher extends ServiceFetcher {
        private Object mCachedInstance;

        @Override
        public final Object getService(PAServiceEvn unused) {
            synchronized (StaticServiceFetcher.this) {
                Object service = mCachedInstance;
                if (service != null) {
                    return service;
                }
                return mCachedInstance = createStaticService();
            }
        }

        public abstract Object createStaticService();
    }


}
