package com.xmd.chat.event;

import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.viewmodel.ChatRowViewModel;

/**
 * Created by mo on 17-7-8.
 * 新增消息到ui界面
 */

public class EventNewUiMessage {
    private ChatRowViewModel viewModel;

    public EventNewUiMessage(ChatRowViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public ChatMessage getChatMessage() {
        return viewModel.getChatMessage();
    }

    public ChatRowViewModel getViewModel() {
        return viewModel;
    }
}
