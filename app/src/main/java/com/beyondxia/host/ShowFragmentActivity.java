package com.beyondxia.host;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.beyondxia.bussiness1.export.FragmentShowService;
import com.beyondxia.modules.R;

/**
 * @author yuandunbin
 * @date 2018/9/29
 */
public class ShowFragmentActivity extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_showfragment);
    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.fragment_container, FragmentShowService.get().getSimpleFragment())
        .commit();

    setTitle("HostApp");
  }
}
