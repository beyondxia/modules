package com.beyondxia.modules;

import android.os.Bundle;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by xiaojunxia on 2018/8/8.
 *
 * 叶子元素只允许是基本类型
 */
//public class BCDictionary extends Bundle {
public class BCDictionary extends HashMap {

    public BCDictionary() {
    }

    public BCDictionary(String json) {
        putAll(getMap(json));
    }

    // TODO 如何判断所有叶子节点为基本类型，暂时设为private，否则会出现内存问题
    private BCDictionary(BCBaseModel obj) {
        this(new Gson().toJson(obj));
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    // 提供给调用方使用
    public <V> V fromDictionary(Class<V> clazz) {
        String json = toJson();
        return new Gson().fromJson(json, clazz);
    }


    public static class Builder {
        private BCDictionary dictionary;

        public Builder() {
            dictionary = new BCDictionary();
        }

        public <T> Builder put(String key, T value){
            dictionary.put(key,value);
            return this;
        }

        public BCDictionary build(){
            return dictionary;
        }

    }

    private static Map<String, Object> getMap(String jsonString)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(jsonString);   @SuppressWarnings("unchecked")
        Iterator<String> keyIter = jsonObject.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyIter.hasNext())
            {
                key = (String) keyIter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;

    }
}
