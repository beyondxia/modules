package com.beyondxia.host.export;

import com.beyondxia.annotation.ExportMethod;
import com.beyondxia.annotation.ExportService;

/**
 * Created by xiaojunxia on 2018/9/26.
 *
 * note: the export should not have super class
 */
@ExportService(moduleName = "Host")
public class HostExport {
    @ExportMethod
    public String getHostName() {
        return "Host APP Name";
    }

//    @ExportMethod
//    public String getHostName() {
//        return "Host APP Name";
//    }
}
