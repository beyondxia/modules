package com.beyondxia.message.export;

import android.content.Context;
import java.lang.String;

/**
 * Auto generate by apt, don't edit */
public interface IMessage {
  String SERVICE_NAME = "com.beyondxia.message.export.IMessage";

  String getMessage(String msg);

  void nav2B2Activity(Context context);
}
