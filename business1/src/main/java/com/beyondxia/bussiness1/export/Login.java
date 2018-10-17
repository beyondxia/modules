package com.beyondxia.bussiness1.export;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.beyondxia.annotation.ExportMethod;
import com.beyondxia.annotation.ExportService;
import com.beyondxia.bussiness1.LoginActivity;
import com.beyondxia.bussiness1.LoginModel;
import com.beyondxia.bussiness1.MyLogin;
import com.beyondxia.modules.BCDictionary;
import com.beyondxia.modules.ILifeCycle;
import com.beyondxia.modules.PAService;

/**
 * Create by ChenWei on 2018/8/29 11:07
 **/
@ExportService(moduleName = "business1")
public class Login implements ILifeCycle{

    @ExportMethod
    public boolean doLogin(Context context, String userName, String password) {
        if (context == null || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) return false;
        return MyLogin.doLogin(userName,password);
    }

    @ExportMethod
    public String getUserName() {
        return MyLogin.getUserName();
    }

    // use BCDictionary to export model
    @ExportMethod
    public BCDictionary getUserInfo() {
        BCDictionary.Builder builder = new BCDictionary.Builder();
        BCDictionary dictionary =
        builder.put("userName","zhangsan")
        .put("password", "pass")
        .build();
        return  dictionary;

//        return new BCDictionary("zhangsan","pass");
    }

    @ExportMethod
    public void nav2LoginActivity(Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, LoginActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    public void onInstalled() {
        // just for time-spend test, you will get warning log like this: "you should not do some time-consuming operation on main thread! time spend..."
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUninstalled() {

    }
}
