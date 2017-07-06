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
                                    .sendLocationMessage(remoteUser.getChatId(), result.latitude, result.longitude, result.street);
                            activity.addNewChatMessageToUi(chatMessage);
                        }
                    }
                });
            }
        }, null);
    }
}
