package com.beyondxia.host.export;

import com.beyondxia.annotation.ExportMethod;
import com.beyondxia.annotation.ExportService;

import java.util.Map;

/**
 * Create by ChenWei on 2018/8/27 10:11
 **/
@ExportService(moduleName = "apptest",preload = true)
public class TestServiceImpl {

    @ExportMethod
    public String t1(String a) {
        return "";
    }

    @ExportMethod
    public int t2(Map<String, String> str) {
        return 0;
    }

}
