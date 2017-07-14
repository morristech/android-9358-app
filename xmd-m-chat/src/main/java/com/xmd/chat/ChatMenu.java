package com.xmd.chat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.helper.ThreadPoolManager;

import java.util.List;

public class ChatMenu {
    public String name;
    public int icon;
    public View.OnClickListener listener;
    public List<Fragment> subMenuList;
    public FragmentStatePagerAdapter adapter;
    private boolean needRequestLayout;

    public ChatMenu(AppCompatActivity activity, int icon, View.OnClickListener listener, List<Fragment> subMenuList) {
        init(activity, null, icon, listener, subMenuList);
    }

    public ChatMenu(AppCompatActivity activity, String name, int icon, View.OnClickListener listener, List<Fragment> subMenuList) {
        init(activity, name, icon, listener, subMenuList);
    }

    private void init(AppCompatActivity activity, String name, int icon, View.OnClickListener listener, List<Fragment> subMenuList) {
        this.name = name;
        this.icon = icon;
        this.listener = listener;
        this.subMenuList = subMenuList;
        needRequestLayout = true;
        adapter = new FragmentStatePagerAdapter(activity.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return ChatMenu.this.subMenuList.get(position);
            }

            @Override
            public int getCount() {
                return ChatMenu.this.subMenuList.size();
            }

            @Override
            public void finishUpdate(final ViewGroup container) {
                super.finishUpdate(container);
                if (needRequestLayout) {
                    //因为动态增加到viewpager，在没有增加子view之前viewpager的size是无效的，
                    // 这里是首次增加子view，所以强制viewpager计算一下size
                    ThreadPoolManager.postToUI(new Runnable() {
                        @Override
                        public void run() {
                            container.requestLayout();
                        }
                    });
                    needRequestLayout = false;
                }
            }

            @Override
            public void startUpdate(ViewGroup container) {
                super.startUpdate(container);
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);
            }
        };
    }
}
