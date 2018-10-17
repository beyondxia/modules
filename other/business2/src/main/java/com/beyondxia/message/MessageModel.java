package com.beyondxia.message;

/**
 * Created by xiaojunxia on 2018/9/27.
 */

public class MessageModel {
//    "userName","zhangsan"
//    "password", "pass"
    public String userName;
    public String password;

    @Override
    public String toString() {
        return "MessageModel{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
