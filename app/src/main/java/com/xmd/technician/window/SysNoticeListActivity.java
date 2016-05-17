package com.xmd.technician.window;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.Adapter.SysNoticeAdapter;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.CommonUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SysNoticeListActivity extends BaseActivity{

    @Bind(R.id.list_view) RecyclerView mNoticeListView;

    private boolean mIsLoading;
    private boolean mHaveMoreData = true;
    private int mPageSize = 10;
    private String mToChatUsername;
    private SysNoticeAdapter mNoticeAdapter;
    private EMConversation mConversation;
    private int mLastVisibleItem;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_notice_list);
        ButterKnife.bind(this);

        setTitle(R.string.system_notice_activity_title);
        setBackVisible(true);

        mToChatUsername = getIntent().getExtras().getString(ChatConstant.EMCHAT_ID);

        onConversationInit();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNoticeAdapter.refreshList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mConversation.clear();
    }

    private void initView(){
        mNoticeAdapter = new SysNoticeAdapter(this, mToChatUsername,ChatConstant.CHATTYPE_SINGLE);
        mNoticeAdapter.setOnFooterClickListener(v -> loadMore());
        mLayoutManager = new LinearLayoutManager(this);
        mNoticeListView.setHasFixedSize(true);
        mNoticeListView.setLayoutManager(mLayoutManager);
        mNoticeListView.setAdapter(mNoticeAdapter);
        mNoticeListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mLastVisibleItem + 1 == mNoticeAdapter.getItemCount()) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void onConversationInit(){
        // 获取当前conversation对象
        mConversation = EMClient.getInstance().chatManager().getConversation(mToChatUsername, CommonUtils.getConversationType(ChatConstant.CHATTYPE_SINGLE), true);
        // 把此会话的未读数置为0
        mConversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = mConversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        int a = mConversation.getAllMsgCount();
        if (msgCount < mConversation.getAllMsgCount() && msgCount < mPageSize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            mConversation.loadMoreMsgFromDB(msgId, mPageSize - msgCount);
        }
    }

    private void loadMore(){
        if(!mIsLoading && mHaveMoreData){
            List<EMMessage> messages;
            try {
                messages = mConversation.loadMoreMsgFromDB(mNoticeAdapter.getItem(0).getMsgId(),
                        mPageSize);
            }catch (Exception e){
                return;
            }

            if(messages.size() > 0){
                mNoticeAdapter.refreshList();
                if(messages.size() != mPageSize){
                    mHaveMoreData = false;
                }
            }else {
                mHaveMoreData = false;
            }

            mIsLoading = false;
        }
    }
}
