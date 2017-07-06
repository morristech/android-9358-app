package com.xmd.chat;

import android.support.v4.app.Fragment;
import android.view.View;

import java.util.List;

public class ChatMenu {
    public int icon;
    public View.OnClickListener listener;
    public List<Fragment> subMenuList;

    public ChatMenu(int icon, View.OnClickListener listener, List<Fragment> subMenuList) {
        this.icon = icon;
        this.listener = listener;
        this.subMenuList = subMenuList;
    }
}
