package com.beyondxia.modules;

import android.util.Log;

import com.beyondxia.modules.exception.NoServiceException;

/**
 * Create by ChenWei on 2018/8/23 15:40
 **/
public abstract class PAService {

    protected static <T> T getService(String name) throws NoServiceException {
        T t = (T) PAServiceEvn.getService(name);
        if (t == null) {
            String error = String.format("[=== can not find Service:%s ===]", name);
            Log.e("ServiceError", error);
            throw new NoServiceException(error);
        }
        return t;
    }

}
