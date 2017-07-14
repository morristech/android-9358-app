package com.xmd.technician.chat.chatview;

import com.hyphenate.chat.EMMessage;

/**
 * Created by Lhj on 16-7-11.
 */
public interface EMessageListItemClickListener {
    void onResendClick(EMMessage message);

    /**
     * there is default handling when bubble is clicked, if you want handle it, return true
     * another way is you implement in onBubbleClick() of chat row
     *
     * @param message
     * @return
     */
    boolean onBubbleClick(EMMessage message);

    void onBubbleLongClick(EMMessage message);

    void onUserAvatarClick(String username);

    void onUserAvatarLongClick(String username);
}
