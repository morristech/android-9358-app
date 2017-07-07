package com.xmd.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.View;

import com.shidou.commonlibrary.Callback;
import com.xmd.app.user.User;
import com.xmd.chat.beans.Location;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.view.ChatActivity;
import com.xmd.chat.view.SubmenuEmojiFragment;
import com.xmd.chat.view.SubmenuFastReplyFragment;
import com.xmd.image_tool.ImageTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo on 17-7-6.
 * 创建各类聊天菜单
 */

public class MenuFactory {
    private ImageTool imageTool = new ImageTool();

    public List<ChatMenu> createMenuList(ChatActivity activity, User remoteUser, Editable editable) {
        List<ChatMenu> menus = new ArrayList<>();

        menus.add(createPictureMenu(activity, remoteUser));
        menus.add(createEmojiMenu(editable));
        menus.add(createLocationMenu(activity, remoteUser));
        menus.add(createFastReplyMenu(activity, remoteUser));

        return menus;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return imageTool.onActivityResult(requestCode, resultCode, data);
    }

    //创建图片菜单
    public ChatMenu createPictureMenu(final ChatActivity activity, final User remoteUser) {
        return new ChatMenu(R.drawable.chat_menu_image, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageTool.onlyPick(true).start(activity, new ImageTool.ResultListener() {
                    @Override
                    public void onResult(String s, Uri uri, Bitmap bitmap) {
                        ChatMessage chatMessage = MessageManager.getInstance()
                                .sendImageMessage(remoteUser.getChatId(), uri.getPath());
                        activity.addNewChatMessageToUi(chatMessage);
                    }
                });
            }
        }, null);
    }

    //创建表情菜单
    public ChatMenu createEmojiMenu(Editable editable) {
        List<Fragment> emojiFragmentList = new ArrayList<>();
        SubmenuEmojiFragment submenuEmojiFragment = new SubmenuEmojiFragment();
        submenuEmojiFragment.setOutputView(editable);
        emojiFragmentList.add(submenuEmojiFragment);
        return new ChatMenu(R.drawable.chat_menu_emoji, null, emojiFragmentList);
    }

    //创建位置菜单
    public ChatMenu createLocationMenu(final ChatActivity activity, final User remoteUser) {
        return new ChatMenu(R.drawable.chat_menu_location, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmdChat.getInstance().getClubLocation(new Callback<Location>() {
                    @Override
                    public void onResponse(Location result, Throwable error) {
                        if (result != null) {
                            ChatMessage chatMessage = MessageManager.getInstance()
                                    .sendLocationMessage(remoteUser.getChatId(), result);
                            activity.addNewChatMessageToUi(chatMessage);
                        }
                    }
                });
            }
        }, null);
    }

    //创建快捷回复菜单
    public ChatMenu createFastReplyMenu(final ChatActivity activity, final User remoteUser) {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> messageList1 = new ArrayList<>();
        messageList1.add("很高兴能为您解决问题，客官给个好评哦，么么哒");
        messageList1.add("不好意思，现在暂时回答不了您的问题，稍后回复您");
        messageList1.add("店里搞活动啦！送您个优惠券，记得过来啊！");
        messageList1.add("好久不见了，有空过来玩玩啊！");
        messageList1.add("约定了，记得准时来，不见不散");
        List<String> messageList2 = new ArrayList<>();
        messageList2.add("客官，打赏几个铜板鼓励鼓励嘛！");
        messageList2.add("这个月完成不了任务了，大侠可否帮忙点个钟？");
        messageList2.add("不好意思，现在暂时回答不了您的问题，稍后回复您～");
        messageList2.add("方便的话麻烦留一个联系方式，以后常联系～");
        messageList2.add("多谢客官打赏");
        SubmenuFastReplyFragment fragment1 = new SubmenuFastReplyFragment();
        fragment1.setMessages(activity, remoteUser.getChatId(), messageList1);
        fragmentList.add(fragment1);
        SubmenuFastReplyFragment fragment2 = new SubmenuFastReplyFragment();
        fragment2.setMessages(activity, remoteUser.getChatId(), messageList2);
        fragmentList.add(fragment2);
        return new ChatMenu(R.drawable.chat_menu_fast_reply, null, fragmentList);
    }
}
