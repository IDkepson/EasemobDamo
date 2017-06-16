package com.bhne.easemobdamo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bhne.easemobdamo.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maolin on 2017/6/16.
 */

public class MainActivity extends AppCompatActivity{

    private TabLayout tablayout;

    private EaseConversationListFragment conversationListFragment;
    private EaseContactListFragment contactListFragment;
    private SettingsFragment settingFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        tablayout.addTab(tablayout.newTab().setText("会话"),true);
        tablayout.addTab(tablayout.newTab().setText("联系人"));
        tablayout.addTab(tablayout.newTab().setText("设置"));

        initData();

        initlisenter();

    }

    /**
     * prepared users, password is "123456"
     * you can use these user to test
     * @return
     */
    private Map<String, EaseUser> getContacts(){
        List<String> list=new ArrayList<>();
        try {
            list=EMClient.getInstance().contactManager().getAllContactsFromServer();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        Map<String, EaseUser> contacts = new HashMap<String, EaseUser>();
        for(int i = 0; i < list.size(); i++){
            EaseUser user=new EaseUser(list.get(i));
            contacts.put(list.get(i), user);
        }
        return contacts;
    }
    private void initData() {
        conversationListFragment = new EaseConversationListFragment();
        contactListFragment = new EaseContactListFragment();
        settingFragment = new SettingsFragment();
        new Thread(new Runnable() {
            @Override
            public void run() {
                contactListFragment.setContactsMap(getContacts());
                contactListFragment.refresh();
            }
        }).start();
        fragments = new Fragment[] { conversationListFragment, contactListFragment, settingFragment };
        // add and show first fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, conversationListFragment)
                .add(R.id.fragment_container, contactListFragment)
                .add(R.id.fragment_container, settingFragment)
                .hide(contactListFragment)
                .hide(settingFragment)
                .show(conversationListFragment)
                .commit();
    }

    private String str="";
    private void cheakPermision(String str){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 101);
        }else {
            startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, str));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==101){
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, str));
            }else {
                Toast.makeText(this,"permission.RECORD_AUDIO is not",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initlisenter() {
        contactListFragment.setContactListItemClickListener(new EaseContactListFragment.EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                str=user.getUsername();
//                startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
                cheakPermision(str);
            }
        });
        conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                str=conversation.conversationId();
//                startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()));
                cheakPermision(str);
            }
        });
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                index=position;
                if (currentTabIndex != index) {
                    FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                    trx.hide(fragments[currentTabIndex]);
                    if (!fragments[index].isAdded()) {
                        trx.add(R.id.fragment_container, fragments[index]);
                    }
                    trx.show(fragments[index]).commit();
                }
                currentTabIndex = index;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
