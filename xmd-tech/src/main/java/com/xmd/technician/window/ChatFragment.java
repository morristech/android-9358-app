package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.net.BaseBean;
import com.xmd.app.net.NetworkEngine;
import com.xmd.app.net.NetworkSubscriber;
import com.xmd.app.net.RetrofitFactory;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.message.ChatMessage;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.ContactPermissionInfo;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatHelper;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.chat.event.DeleteConversionResult;
import com.xmd.technician.chat.event.EventEmChatLoginSuccess;
import com.xmd.technician.chat.event.EventReceiveMessage;
import com.xmd.technician.chat.utils.UserUtils;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.NetService;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.permission.contact.ContactPermissionManager;
import com.xmd.technician.widget.AlertDialogBuilder;
import com.xmd.technician.widget.ChatMessageManagerDialog;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import rx.Subscription;

/**
 * Created by sdcm on 16-3-23.
 */
public class ChatFragment extends BaseListFragment<EMConversation> {

    @Bind(R.id.empty_view_widget)
    EmptyView mEmptyView;
    @Bind(R.id.header_container)
    FrameLayout mHeadContainer;
    protected List<EMConversation> mConversationList = new ArrayList<>();
    private Filter mFilter;
    private Subscription mLoginStatusSubscription;
    private Subscription mNewMessageSubscription;
    private Subscription mDeleteConversionSubscription;
    private TextView mSearchView;
    private String mMessageFrom;
    private ChatHelper emchat;

    private LoginTechnician technician = LoginTechnician.getInstance();
    private int mWaitProcessCount;

    private boolean isChangingCustomerStatus;
    private Subscription changeCustomerStatusSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.message_fragment_title);
        return view;
    }


    @Override
    protected void initView() {
        initTitleView(ResourceUtils.getString(R.string.message_fragment_title));
        emchat = ChatHelper.getInstance();
        View searchView = getActivity().getLayoutInflater().inflate(R.layout.search_bar, mHeadContainer, false);
        mHeadContainer.addView(searchView);
        mSearchView = (TextView) searchView.findViewById(R.id.search_word);
        ((TextView) searchView.findViewById(R.id.search_word)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getFilter().filter(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEmptyView.setStatus(EmptyView.Status.Gone);
        mEmptyView.setEmptyPic(R.drawable.empty);
        mEmptyView.setEmptyTip("");

        //监听登录消息
        mLoginStatusSubscription = RxBus.getInstance().toObservable(EventEmChatLoginSuccess.class).subscribe(
                eventEmChatLogin -> {
                    //登录成功的消息,刷新一次
                    onRefresh();
                }
        );
        //监听删除聊天，刷新
        mDeleteConversionSubscription = RxBus.getInstance().toObservable(DeleteConversionResult.class).subscribe(
                result -> dispatchRequest()
        );


        initToolBarCustomerService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (changeCustomerStatusSubscription != null) {
            changeCustomerStatusSubscription.unsubscribe();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        RxBus.getInstance().unsubscribe(mNewMessageSubscription);
    }


    @Override
    protected void dispatchRequest() {
        List<EMConversation> list = emchat.getAllConversationList();
        //不显示无法聊天的用户
        mConversationList.clear();
        if (list.size() > 0) {
            mWaitProcessCount = list.size();
            for (EMConversation conversation : list) {
                ChatMessage lastMessage = ChatMessageFactory.get(conversation.getLastMessage());
                if (lastMessage == null) {
                    ChatHelper.getInstance().clearUnreadMessage(conversation);
                    onLoadPermissionFinish();
                    continue;
                }
                String remoteChatId = lastMessage.getFromChatId();
                if (remoteChatId != null && remoteChatId.equals(LoginTechnician.getInstance().getEmchatId())) {
                    remoteChatId = lastMessage.getToChatId();
                }
                User user = UserInfoServiceImpl.getInstance().getUserByChatId(remoteChatId);
                if (user == null) {
                    XLogger.e("没有用户信息： chatId=" + remoteChatId);
                    ChatHelper.getInstance().clearUnreadMessage(conversation);
                    onLoadPermissionFinish();
                    continue;
                }
                ContactPermissionManager.getInstance().getPermission(user.getId(), new NetworkSubscriber<ContactPermissionInfo>() {
                    @Override
                    public void onCallbackSuccess(ContactPermissionInfo result) {
                        if (result.echat) {
                            mConversationList.add(conversation);
                        } else {
                            ChatHelper.getInstance().clearUnreadMessage(conversation);
                        }
                        onLoadPermissionFinish();
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        onLoadPermissionFinish();
                    }
                });
            }
        } else {
            onGetListSucceeded(0, mConversationList);
        }
    }

    private void onLoadPermissionFinish() {
        mWaitProcessCount--;
        if (mWaitProcessCount == 0) {
            Collections.sort(mConversationList, new Comparator<EMConversation>() {
                @Override
                public int compare(EMConversation o1, EMConversation o2) {
                    EMMessage last1 = o1.getLastMessage();
                    EMMessage last2 = o2.getLastMessage();
                    if (last1 == null && last2 == null) {
                        return 0;
                    }
                    if (last1 == null) {
                        return 1;
                    }
                    if (last2 == null) {
                        return -1;
                    }
                    return (int) -(last1.getMsgTime() - last2.getMsgTime());
                }
            });
            onGetListSucceeded(0, mConversationList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(
                mLoginStatusSubscription,
                mDeleteConversionSubscription);
        if (mNewMessageSubscription != null) {
            RxBus.getInstance().unsubscribe(mNewMessageSubscription);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSearchView != null && !TextUtils.isEmpty(mSearchView.getText()))
            mSearchView.setText("");
        onRefresh();
        //监听新的消息，刷新  result -> onRefresh()
        mNewMessageSubscription = RxBus.getInstance().toObservable(EventReceiveMessage.class).subscribe(result -> onRefresh());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mSearchView != null && !TextUtils.isEmpty(mSearchView.getText()))
                mSearchView.setText("");
            onRefresh();
        }
    }

    @Override
    public void onItemClicked(EMConversation conversation) {
        String username = conversation.conversationId();
        String tochat = "";

        if (username.equals(SharedPreferenceHelper.getEmchatId()))
            ((BaseFragmentActivity) getActivity()).makeShortToast(ResourceUtils.getString(R.string.cant_chat_with_yourself));
        else if (username.equals(ChatConstant.MESSAGE_SYSTEM_NOTICE)) {
            Intent intent = new Intent(getContext(), SysNoticeListActivity.class);
            intent.putExtra(ChatConstant.EMCHAT_ID, username);
            startActivity(intent);
        } else {
            if (conversation.getLastMessage().getFrom().equals(SharedPreferenceHelper.getEmchatId())) {
                tochat = conversation.getLastMessage().getTo();
            } else {
                tochat = conversation.getLastMessage().getFrom();
            }
            if (null != UserProfileProvider.getInstance().getChatUserInfo(tochat)) {
                mMessageFrom = UserProfileProvider.getInstance().getChatUserInfo(tochat).getUserType();
            }

            Intent intent = new Intent(getContext(), TechChatActivity.class);
            intent.putExtra(ChatConstant.TO_CHAT_USER_ID, tochat);
            getActivity().startActivity(intent);
            ChatHelper.getInstance().clearUnreadMessage(conversation);
        }
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ConversationFilter(mConversationList);
        }
        return mFilter;
    }

    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            /*if (prefix == null || prefix.length() == 0) {
                results.values = mConversationList;
                results.count = mConversationList.size();
            } else*/
            {
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.conversationId();


                    EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
                    if (group != null) {
                        username = group.getGroupName();
                    } else {
                        username = UserUtils.getUserNick(value.conversationId());
                    }

                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            onGetListSucceeded(0, (List<EMConversation>) results.values);
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
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEG_DELETE_CONVERSATION_FROM_DB, bean);
            }
        });
        dialog.show();
    }

    @Override
    public boolean isPaged() {
        return false;
    }


    //显示客服上下线开关
    private void initToolBarCustomerService() {
        if (technician.getCustomerService() == null) {
            return;
        }
        LinearLayout container = (LinearLayout) getView().findViewById(R.id.contact_more);
        container.setVisibility(View.VISIBLE);
        ImageView imageView = (ImageView) getView().findViewById(R.id.toolbar_right_img);
        imageView.setImageResource(R.drawable.ic_service);
        TextView checkBox = new TextView(getContext());
        if (Constant.CUSTOMER_STATUS_WORKING.equals(technician.getCustomerService())) {
            checkBox.setTag("checked");
            checkBox.setBackgroundResource(R.drawable.nav_top_open);
        } else {
            checkBox.setTag(null);
            checkBox.setBackgroundResource(R.drawable.nav_top_close);
        }
        container.addView(checkBox);
        ((LinearLayout.LayoutParams) checkBox.getLayoutParams()).leftMargin = 16;
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChangingCustomerStatus) {
                    return;
                }
                isChangingCustomerStatus = true;
                if (checkBox.getTag() == null) {
                    changeCustomerStatusSubscription = NetworkEngine.doRequest(RetrofitFactory.getService(NetService.class).changeCustomerStatus(Constant.CUSTOMER_STATUS_WORKING), new NetworkSubscriber<BaseBean>() {
                        @Override
                        public void onCallbackSuccess(BaseBean result) {
                            isChangingCustomerStatus = false;
                            checkBox.setTag("checked");
                            checkBox.setBackgroundResource(R.drawable.nav_top_open);
                        }

                        @Override
                        public void onCallbackError(Throwable e) {
                            isChangingCustomerStatus = false;
                            XToast.show("修改状态失败！");
                        }
                    });
                } else {
                    new AlertDialogBuilder(getContext())
                            .setTitle("提示")
                            .setMessage("关闭后，不再接收新客服消息，不影响其他消息的接收，是否确定关闭？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    changeCustomerStatusSubscription = NetworkEngine.doRequest(RetrofitFactory.getService(NetService.class).changeCustomerStatus(Constant.CUSTOMER_STATUS_REST), new NetworkSubscriber<BaseBean>() {
                                        @Override
                                        public void onCallbackSuccess(BaseBean result) {
                                            isChangingCustomerStatus = false;
                                            checkBox.setTag(null);
                                            checkBox.setBackgroundResource(R.drawable.nav_top_close);
                                        }

                                        @Override
                                        public void onCallbackError(Throwable e) {
                                            isChangingCustomerStatus = false;
                                            XToast.show("修改状态失败！");
                                        }
                                    });
                                }
                            })
                            .show();
                }
            }
        });
    }

}
