package com.xmd.chat.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.BR;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatActivityBinding;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends BaseActivity {
    public static final String EXTRA_CHAT_ID = "extra_chat_id";

    private ChatActivityBinding mBinding;
    private CommonRecyclerViewAdapter<BaseChatRowData> mAdapter;
    private List<BaseChatRowData> mDataList = new ArrayList<>();

    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    private User user;

    private EMConversation mConversation;
    private final int PAGE_SIZE = 20;
    private int mPageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.chat_activity);

        String chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        if (TextUtils.isEmpty(chatId)) {
            XToast.show("必须传入聊天ID!");
            finish();
            return;
        }

        user = userInfoService.getUserByChatId(chatId);
        if (user == null) {
            XToast.show("无法找到用户信息!");
            finish();
            return;
        }
        mAdapter = new CommonRecyclerViewAdapter<>();
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.setAdapter(mAdapter);

        mBinding.setData(this);

        mConversation = EMClient.getInstance().chatManager().getConversation(chatId);
        loadData(mPageIndex);
    }

    private void loadData(int page) {
        if (mConversation.getAllMsgCount() == 0) {
            return;
        }

        mConversation.loadMoreMsgFromDB(null, PAGE_SIZE);
        for (EMMessage message : mConversation.getAllMessages()) {
            mDataList.add(new BaseChatRowData(ChatMessageFactory.get(message)));
        }
        mAdapter.setData(R.layout.list_item_message_receive, BR.data, mDataList);
        mAdapter.notifyDataSetChanged();
    }
}
