package com.beyondxia.host;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beyondxia.bussiness1.export.LoginService;
import com.beyondxia.message.export.MessageService;
import com.beyondxia.modules.BCDictionary;
import com.beyondxia.modules.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final TextView tv = findViewById(R.id.tv);
//
//        try {
//            tv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    IMyMessage iMyMessage = MyMessageService.get();
//                    tv.setText(iMyMessage.getMessage("message"));
//                }
//            });
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_business1_call:
                String username = LoginService.get().getUserName();
                Toast.makeText(this,username,Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_business1_call2:
                boolean success = LoginService.get().doLogin(this,"chenwei","xiaxiaojun");
                Toast.makeText(this,success?"login success":"login failed",Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_business1_call3:
                BCDictionary dictionary = LoginService.get().getUserInfo();
                Toast.makeText(this,dictionary.toJson(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_business1_nav:
                LoginService.get().nav2LoginActivity(this);
                break;
            case R.id.bt_business2_nav:
                MessageService.get().nav2B2Activity(this);
                break;
            case R.id.bt_submodule_view:
                startActivity(new Intent(this, ShowViewActivity.class));
                break;
            case R.id.bt_submodule_fragment:
                startActivity(new Intent(this, ShowFragmentActivity.class));
                break;
            default:
                break;
        }
    }
}
