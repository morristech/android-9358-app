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
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.message.ChatMessage;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatHelper;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.chat.event.DeleteConversionResult;
import com.xmd.technician.chat.event.EventEmChatLoginSuccess;
import com.xmd.technician.chat.event.EventReceiveMessage;
import com.xmd.technician.chat.utils.UserUtils;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.ContactPermissionChatResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.AlertDialogBuilder;
import com.xmd.technician.widget.ChatMessageManagerDialog;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Subscription mContactPermissionChatSubscription;
    private Subscription mDeleteConversionSubscription;
    private TextView mSearchView;
    private String mMessageFrom;
    private ChatHelper emchat;

    private LoginTechnician technician = LoginTechnician.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.message_fragment_title);
        return view;
    }

    private void initToolBarCustomerService() {
        if (!"Y".equals(technician.getCustomerService())) {
            return;
        }
        LinearLayout container = (LinearLayout) getView().findViewById(R.id.contact_more);
        container.setVisibility(View.GONE);
        ImageView imageView = (ImageView) getView().findViewById(R.id.toolbar_right_img);
        imageView.setImageResource(R.drawable.ic_customer_service);
        TextView checkBox = new TextView(getContext());
        boolean open = technician.isCustomerServiceTimeValid();
        if (open) {
            checkBox.setTag("checked");
            checkBox.setBackgroundResource(R.drawable.ic_checkbox_open);
        } else {
            checkBox.setTag(null);
            checkBox.setBackgroundResource(R.drawable.ic_checkbox_close);
        }
        container.addView(checkBox);
        ((LinearLayout.LayoutParams) checkBox.getLayoutParams()).leftMargin = 16;
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.getTag() == null) {
                    checkBox.setTag("checked");
                    checkBox.setBackgroundResource(R.drawable.ic_checkbox_open);
                    technician.setCustomerServiceDisableTime(0L);
                } else {
                    new AlertDialogBuilder(getContext())
                            .setTitle("提示")
                            .setMessage("关闭后，收到客服消息后不会进行提醒，不影响接收其他消息，12小时后会自动开启，是否要关闭？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    checkBox.setTag(null);
                                    checkBox.setBackgroundResource(R.drawable.ic_checkbox_close);
                                    technician.setCustomerServiceDisableTime(System.currentTimeMillis());
                                }
                            })
                            .show();
                }
            }
        });
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

        //监听获取联系人权限消息
        mContactPermissionChatSubscription = RxBus.getInstance()
                .toObservable(ContactPermissionChatResult.class)
                .subscribe(this::handleContactPermissionChat);

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
    public void onPause() {
        super.onPause();
        RxBus.getInstance().unsubscribe(mNewMessageSubscription);
    }


    @Override
    protected void dispatchRequest() {
        List<EMConversation> list = emchat.getAllConversationList();
        //不显示无法聊天的用户
        mConversationList.clear();
        for (EMConversation conversation : list) {
            ChatMessage lastMessage = ChatMessageFactory.get(conversation.getLastMessage());
            if (lastMessage == null) {
                continue;
            }
            boolean lastIsHello = lastMessage.getTag() != null && lastMessage.getTag().contains(ChatMessage.MSG_TAG_HELLO);
            if (conversation.getLatestMessageFromOthers() == null && lastIsHello) {
                conversation.clear();
                continue;
            }
            conversation.clear();
            mConversationList.add(conversation);
        }

        onGetListSucceeded(0, mConversationList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(
                mLoginStatusSubscription,
                mContactPermissionChatSubscription,
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

    private void getContactPermissionChat(EMConversation conversation) {
        Map<String, Object> params = new HashMap<>();
        params.put(RequestConstant.KEY_REQUEST_CONTACT_PERMISSION_TAG, Constant.REQUEST_CONTACT_PERMISSION_EMCHAT);
        params.put(RequestConstant.KEY_ID, conversation.conversationId());
        params.put(RequestConstant.KEY_CONTACT_ID_TYPE, Constant.REQUEST_CONTACT_ID_TYPE_EMCHAT);
        params.put(RequestConstant.KEY_CHAT_CONVERSATION_BEAN, conversation);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONTACT_PERMISSION, params);
    }

    private void handleContactPermissionChat(ContactPermissionChatResult result) {
        if (result != null && result.statusCode == 200) {
            if (result.respData.echat) {
                // 跳转聊天
                EMConversation conversation = result.emConversation;
                Intent intent = new Intent(getContext(), TechChatActivity.class);
                intent.putExtra(ChatConstant.TO_CHAT_USER_ID, conversation.conversationId());
                ChatHelper.getInstance().clearUnreadMessage(conversation);
                startActivity(intent);
            } else {
                // 跳转详情
                Intent intent = new Intent(getActivity(), ContactInformationDetailActivity.class);
                intent.putExtra(RequestConstant.KEY_USER_ID, result.respData.customerId);
                intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, Constant.CONTACT_INFO_DETAIL_TYPE_CUSTOMER);
                Bundle bundle = new Bundle();
                bundle.putSerializable(RequestConstant.KEY_CONTACT_PERMISSION_INFO, result.respData);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } else {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), result.msg, Toast.LENGTH_SHORT).show();

            }
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

            if (Utils.isEmpty(mMessageFrom) || mMessageFrom.equals(ChatConstant.TO_CHAT_USER_TYPE_CUSTOMER)) {
                getContactPermissionChat(conversation);
            } else {
                Intent intent = new Intent(getContext(), TechChatActivity.class);
                intent.putExtra(ChatConstant.TO_CHAT_USER_ID, tochat);
                getActivity().startActivity(intent);
            }

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


}
