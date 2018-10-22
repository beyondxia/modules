package com.beyondxia.message;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.beyondxia.bussiness1.export.LoginService;
import com.beyondxia.host.export.HostExportService;
import com.beyondxia.message.R;
import com.beyondxia.modules.BCDictionary;


public class B2Activity extends AppCompatActivity {
    private Button bt_host_call;
    private Button bt_business1_call;
    private Button bt_business1_call2;
    private Button bt_business1_nav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b2);
        setTitle("Business2");

        bt_host_call = (Button) findViewById(R.id.bt_host_call);
        bt_business1_call = (Button) findViewById(R.id.bt_business1_call);
        bt_business1_call2 = (Button) findViewById(R.id.bt_business1_call2);
        bt_business1_nav = (Button) findViewById(R.id.bt_business1_nav);


        bt_host_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hostName = HostExportService.get().getHostName();
                Toast.makeText(B2Activity.this,hostName,Toast.LENGTH_SHORT).show();
            }
        });
        bt_business1_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = LoginService.get().doLogin(B2Activity.this,"chenwei","xiaxiaojun");
                Toast.makeText(B2Activity.this,success?"login success":"login failed",Toast.LENGTH_SHORT).show();
            }
        });
        bt_business1_call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BCDictionary dictionary = LoginService.get().getUserInfo();
                // dictionary数据必须为java 基本类型
                MessageModel messageModel = dictionary.fromDictionary(MessageModel.class);
                Toast.makeText(B2Activity.this,messageModel.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        bt_business1_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginService.get().nav2LoginActivity(B2Activity.this);
            }
        });
    }

}
