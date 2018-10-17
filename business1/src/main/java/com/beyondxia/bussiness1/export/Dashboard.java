package com.beyondxia.bussiness1.export;

import android.content.Context;
import android.view.View;

import com.beyondxia.annotation.ExportMethod;
import com.beyondxia.annotation.ExportService;
import com.beyondxia.bussiness1.view.DashboardView;

/**
 * @author yuandunbin
 * @date 2018/9/29
 */
@ExportService(moduleName = "business1")
public class Dashboard {
    @ExportMethod
    public View getDashboardView(Context context) {
        return new DashboardView(context);
    }
}
