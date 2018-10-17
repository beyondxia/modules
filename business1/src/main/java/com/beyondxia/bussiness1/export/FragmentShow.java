package com.beyondxia.bussiness1.export;

import android.support.v4.app.Fragment;

import com.beyondxia.annotation.ExportMethod;
import com.beyondxia.annotation.ExportService;
import com.beyondxia.bussiness1.fragment.SimpleFragment;

/**
 * @author yuandunbin
 * @date 2018/9/29
 */
@ExportService(moduleName = "business1")
public class FragmentShow {
    @ExportMethod
    public Fragment getSimpleFragment(){
        return new SimpleFragment();
    }
}
