package com.beyondxia.host.export;

import com.beyondxia.annotation.ExportMethod;
import com.beyondxia.annotation.ExportService;

/**
 * Create by ChenWei on 2018/8/27 14:04
 **/
@ExportService(moduleName = "app")
public class TestServiceImpl1 {

    @ExportMethod
    public void m1(int a, String s) {

    }

    //    @ExportMethod
    public int m2() {
        return 0;
    }

}
