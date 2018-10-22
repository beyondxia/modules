package com.beyondxia.message.export;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.beyondxia.annotation.ExportMethod;
import com.beyondxia.annotation.ExportService;
import com.beyondxia.message.B2Activity;

/**
 * Created by xiaojunxia on 2018/9/26.
 */
@ExportService(moduleName = "business2")
public class Message implements IMessage{

    @ExportMethod
    public String getMessage(String msg) {
        return "b2 message";
    }

    @ExportMethod
    public void nav2B2Activity(Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, B2Activity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
