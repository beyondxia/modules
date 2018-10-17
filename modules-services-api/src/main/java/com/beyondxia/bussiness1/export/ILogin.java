package com.beyondxia.bussiness1.export;

import android.content.Context;
import com.beyondxia.modules.BCDictionary;
import java.lang.String;

/**
 * Auto generate by apt, don't edit */
public interface ILogin {
  String SERVICE_NAME = "com.beyondxia.bussiness1.export.ILogin";

  boolean doLogin(Context context, String userName, String password);

  String getUserName();

  BCDictionary getUserInfo();

  void nav2LoginActivity(Context context);
}
