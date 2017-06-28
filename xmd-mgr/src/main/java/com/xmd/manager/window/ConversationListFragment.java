package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.xmd.manager.R;
import com.xmd.manager.beans.ConversationListResult;
import com.xmd.manager.beans.EmchatMsgResult;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.widget.ChatMessageManagerDialog;
import com.xmd.manager.widget.ClearableEditText;
import com.xmd.manager.widget.DividerItemDecoration;

import butterknife.Bind;
import rx.Subscription;

/**
 * Created by Administrator on 2016/11/9.
 */
public class ConversationListFragment extends BaseListFragment<EMConversation> {

    @Bind(R.id.search_word)
    ClearableEditText mCevSearchWord;

    private Subscription mGetEmchatMsgCountSubscription;
    private Subscription mGetConversationListSubscription;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
        return view;
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONVERSATION_LIST);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(mCevSearchWord.getText()))
            onRefresh();
    }

    @Override
    public void onItemClicked(EMConversation conversation) {
//        String username = conversation.getUserName();
//        if (username.equals(SharedPreferenceHelper.getEmchatId()))
//            makeShortToast(getActivity(), ResourceUtils.getString(R.string.cant_chat_with_yourself));
//        else if (username.equals(EmchatConstant.MESSAGE_SYSTEM_NOTICE)) {
////            Intent intent = new Intent(this, SysNoticeListActivity.class);
////            // it's single chat
////            intent.putExtra(EmchatConstant.EMCHAT_ID, username);
////            startActivity(intent);
//        } else {
//            if (conversation.getLastMessage().getFrom().equals(SharedPreferenceHelper.getEmchatId())) {
//                if (Utils.isNotEmpty(SharedPreferenceHelper.getChatUserType(conversation.getLastMessage().getTo()))) {
//                    EmchatUserHelper.startToChat(username, "", "", null, EmchatConstant.MESSAGE_TECH_TYPE);
//                } else {
//                    EmchatUserHelper.startToChat(username, "", "");
//                }
//            } else {
//                try {
//                    if (Utils.isNotEmpty(conversation.getLastMessage().getStringAttribute(RequestConstant.KEY_TECH_ID))) {
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
    public void onSlideDeleteItem(EMConversation bean) {
        super.onSlideDeleteItem(bean);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEG_DELETE_CONVERSATION_FROM_DB, bean);
    }


    @Override
    public boolean isSlideable() {
        return false;
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    protected void initView() {
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(ResourceUtils.getString(R.string.home_activity_comment));
        ImageView imageLeft = (ImageView) view.findViewById(R.id.toolbar_left);
        imageLeft.setVisibility(View.GONE);
        mListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

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
        mGetConversationListSubscription = RxBus.getInstance().toObservable(ConversationListResult.class).subscribe(
                result -> handlerEmchatMsgResult(result)
        );
    }

    private void handlerEmchatMsgResult(ConversationListResult result) {
        if (result.statusCode == 200) {
            onGetListSucceeded(result.pageCount, result.respData);
        } else {
            onGetListFailed(result.msg);
        }

    }

    @Override
    public void onLongClicked(EMConversation bean) {
        super.onLongClicked(bean);
        ChatMessageManagerDialog dialog = new ChatMessageManagerDialog(getActivity());
        dialog.setCanceledOnTouchOutside(true);
        dialog.setItemClickedListener(new ChatMessageManagerDialog.OnItemClickedListener() {
            @Override
            public void onItemClicked() {
                dialog.dismiss();
                //  MsgDispatcher.dispatchMessage(MsgDef.MSG_DEG_DELETE_CONVERSATION_FROM_DB);
//                EmchatManager.deleteConversion(bean.getUserName(), true);

            }
        });
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetEmchatMsgCountSubscription, mGetConversationListSubscription);
    }
}
