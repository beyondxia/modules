package com.beyondxia.host;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.beyondxia.bussiness1.export.DashboardService;
import com.beyondxia.modules.R;

/**
 * @author yuandunbin
 * @date 2018/9/29
 */
public class ShowViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showview);

        setTitle("HostApp");

        View view = DashboardService.get().getDashboardView(this);
        ((LinearLayout) findViewById(R.id.add_view)).addView(view);


    }
}
