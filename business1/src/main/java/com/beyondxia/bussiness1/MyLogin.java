package com.beyondxia.bussiness1;

/**
 * Created by xiaojunxia on 2018/9/26.
 */

public class MyLogin {
    public static boolean doLogin(String userName, String password) {
        if (userName.equals("chenwei") && password.equals("xiaxiaojun")) return true;
        return false;
    }

    public static String getUserName() {
        return "UserName:chenwei";
    }
}
