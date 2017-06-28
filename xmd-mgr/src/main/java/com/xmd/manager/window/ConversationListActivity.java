package com.xmd.manager.window;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.hyphenate.chat.EMConversation;
import com.xmd.manager.R;
import com.xmd.manager.beans.ConversationListResult;
import com.xmd.manager.beans.EmchatMsgResult;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.widget.ClearableEditText;
import com.xmd.manager.widget.DividerItemDecoration;

import butterknife.Bind;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-24.
 */
public class ConversationListActivity extends BaseListActivity<EMConversation, ConversationListResult> {

    @Bind(R.id.search_word)
    ClearableEditText mCevSearchWord;

    private Subscription mGetEmchatMsgCountSubscription;

    @Override
    protected void setContentViewLayout() {
        setContentView(R.layout.activity_conversation_list);
    }

    @Override
    protected void initOtherViews() {

        mListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mCevSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONVERSATION_LIST, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mGetEmchatMsgCountSubscription = RxBus.getInstance().toObservable(EmchatMsgResult.class).subscribe(
                result -> onRefresh()
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetEmchatMsgCountSubscription);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(mCevSearchWord.getText()))
            onRefresh();
    }

    @Override
    public void onItemClicked(EMConversation conversation) {
//        String username = conversation.getUserName();
//        if (username.equals(SharedPreferenceHelper.getEmchatId()))
//            makeShortToast(ResourceUtils.getString(R.string.cant_chat_with_yourself));
//        else if (username.equals(EmchatConstant.MESSAGE_SYSTEM_NOTICE)) {
////            Intent intent = new Intent(this, SysNoticeListActivity.class);
////            // it's single chat
////            intent.putExtra(EmchatConstant.EMCHAT_ID, username);
////            startActivity(intent);
//        } else {
//            if (conversation.getLastMessage().getFrom().equals(SharedPreferenceHelper.getEmchatId())) {
//                if (Utils.isNotEmpty(conversation.getLastMessage().getTo())) {
//                    EmchatUserHelper.startToChat(username, "", "", null, EmchatConstant.MESSAGE_TECH_TYPE);
//                } else {
//                    EmchatUserHelper.startToChat(username, "", "");
//                }
//
//            } else {
//                try {
//                    if (!TextUtils.isEmpty(conversation.getLastMessage().getStringAttribute(RequestConstant.KEY_TECH_ID))) {
//                        SharedPreferenceHelper.setChatUserType(conversation.getLastMessage().getTo(), EmchatConstant.MESSAGE_TECH_TYPE);
//                        EmchatUserHelper.startToChat(username, "", "", null, EmchatConstant.MESSAGE_TECH_TYPE);
//                    }
//                } catch (HyphenateException e) {
//                    e.printStackTrace();
//                    EmchatUserHelper.startToChat(username, "", "");
//                }
//            }
//        }
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONVERSATION_LIST);
    }

    @Override
    public void onSlideDeleteItem(EMConversation bean) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEG_DELETE_CONVERSATION_FROM_DB, bean);
    }

    @Override
    public boolean isSlideable() {
        return true;
    }
}
