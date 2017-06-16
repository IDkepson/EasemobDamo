package com.bhne.easemobdamo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bhne.easemobdamo.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by maolin on 2017/6/16.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_login_rigst;
    private EditText edt_psd,edt_username;
    private Button btn_login;

    private boolean isLogin=true;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_login);
        tv_login_rigst = (TextView) findViewById(R.id.tv_login_rigst);
        edt_psd = (EditText) findViewById(R.id.edt_psd);
        edt_username= (EditText) findViewById(R.id.edt_username);
        btn_login = (Button) findViewById(R.id.btn_login);
        if(EMClient.getInstance().isLoggedInBefore()){
            //enter to main activity directly if you logged in before.
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        initData();
//        if (em)
    }

    private void initData() {
        tv_login_rigst.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_login_rigst:
                String str = tv_login_rigst.getText().toString();
                if ("注册".equals(str)){
                    btn_login.setText("注册");
                    tv_login_rigst.setText("登录");
                    isLogin=false;
                }else {
                    btn_login.setText("登录");
                    tv_login_rigst.setText("注册");
                    isLogin=true;
                }

                break;
            case R.id.btn_login:
                String psd = edt_psd.getText().toString();
                String username = edt_username.getText().toString();
                if (TextUtils.isEmpty(psd)||TextUtils.isEmpty(username)){
                    Toast.makeText(this,"username or password is not null",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isLogin){
                    Login(username,psd);
                }else {
                    Rigst(username,psd);
                }
                break;
        }
    }

    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(LoginActivity.this,"createAccount Success",Toast.LENGTH_SHORT).show();
                    btn_login.setText("登录");
                    tv_login_rigst.setText("注册");
                    isLogin=true;
                    break;
                case 2:
                    Toast.makeText(LoginActivity.this,"createAccount Error",Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });
    private void Rigst(final String name,final String psd) {
        Toast.makeText(this,"do createAccount...",Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=1;
                try {
                    EMClient.getInstance().createAccount(name,psd);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    i=2;
                    System.out.println("--------->"+e.getMessage());
                }
                handler.sendEmptyMessage(i);
            }
        }).start();
    }

    private void Login(String name,String psd) {
        Toast.makeText(this,"do Login...",Toast.LENGTH_SHORT).show();
        EMClient.getInstance().login(name, psd, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"Login Success",Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }

            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("------------->"+"i=="+i+",s=="+s);
                        Toast.makeText(LoginActivity.this,"Login Error",Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
}
