package com.bhne.easemobdamo.base;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseUI;

/**
 * Created by maolin on 2017/6/15.
 */

public class BaseApplication extends Application {
    private Context AppContext;
    @Override
    public void onCreate() {
        super.onCreate();
//        EMOptions options=new EMOptions();
        AppContext=this;
        EaseUI.getInstance().init(this,null);
        EMClient.getInstance().setDebugMode(true);//开启debug模式
    }

}
