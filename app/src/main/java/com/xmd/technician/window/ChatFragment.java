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
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.ConversationListResult;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.ContactPermissionChatResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.ChatMessageManagerDialog;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import rx.Subscription;
import rx.functions.Action1;

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
    private Subscription mGetConversationListSubscription;
    private Subscription mContactPermissionChatSubscription;
    private TextView mSearchView;
    private String mMessageFrom;

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
        mGetConversationListSubscription = RxBus.getInstance().toObservable(ConversationListResult.class).subscribe(
                conversationListResult -> handleGetConversationListResult(conversationListResult)
        );
        mContactPermissionChatSubscription = RxBus.getInstance().toObservable(ContactPermissionChatResult.class).subscribe(new Action1<ContactPermissionChatResult>() {
            @Override
            public void call(ContactPermissionChatResult contactPermissionChatResult) {
                handleContactPermissionChat(contactPermissionChatResult);
            }
        });
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONVERSATION_LIST);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetConversationListSubscription, mContactPermissionChatSubscription);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSearchView != null && !TextUtils.isEmpty(mSearchView.getText()))
            mSearchView.setText("");
        onRefresh();
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
        params.put(RequestConstant.KEY_ID, conversation.getUserName());
        params.put(RequestConstant.KEY_CONTACT_ID_TYPE, Constant.REQUEST_CONTACT_ID_TYPE_EMCHAT);
        params.put(RequestConstant.KEY_CHAT_CONVERSATION_BEAN, conversation);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONTACT_PERMISSION, params);
    }

    private void handleContactPermissionChat(ContactPermissionChatResult result) {
        if (result != null && result.statusCode == 200) {
            if (result.respData.echat) {
                // 跳转聊天
                EMConversation conversation = result.emConversation;
                Intent intent = new Intent(getContext(), ChatActivity.class);
                if (conversation.isGroup()) {
                    if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
                        intent.putExtra(ChatConstant.EXTRA_CHAT_TYPE, ChatConstant.CHATTYPE_CHATROOM);
                    } else {
                        intent.putExtra(ChatConstant.EXTRA_CHAT_TYPE, ChatConstant.CHATTYPE_GROUP);
                    }
                }
                conversation.getAllMessages();
                intent.putExtra(ChatConstant.EMCHAT_ID, conversation.getUserName());
                intent.putExtra(ChatConstant.EMCHAT_IS_TECH, "");
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
            // TODO
        }
    }

    private void handleGetConversationListResult(ConversationListResult result) {
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetListFailed(result.msg);
            mEmptyView.setStatus(EmptyView.Status.Failed);
        } else {
            onGetListSucceeded(0, result.respData);
            if (result.respData != null) {
                if (!mIsLoadingMore) {
                    mConversationList.clear();
                }
                mConversationList.addAll(result.respData);
            }
            if (result.respData.isEmpty()) {
                mEmptyView.setStatus(EmptyView.Status.Empty);
            } else {
                mEmptyView.setStatus(EmptyView.Status.Gone);
            }
        }
    }

    @Override
    public void onItemClicked(EMConversation conversation) {
        String username = conversation.getUserName();
        if (username.equals(SharedPreferenceHelper.getEmchatId()))
            ((BaseFragmentActivity) getActivity()).makeShortToast(ResourceUtils.getString(R.string.cant_chat_with_yourself));
        else if (username.equals(ChatConstant.MESSAGE_SYSTEM_NOTICE)) {
            Intent intent = new Intent(getContext(), SysNoticeListActivity.class);
            // it's single chat
            intent.putExtra(ChatConstant.EMCHAT_ID, username);
            startActivity(intent);
        } else {
            if (conversation.getLastMessage().getFrom().equals(SharedPreferenceHelper.getEmchatId())) {
                if (SharedPreferenceHelper.getUserIsTech(conversation.getUserName()).equals("tech")) {
                    mMessageFrom = "tech";
                } else if (SharedPreferenceHelper.getUserIsTech(conversation.getUserName()).equals("manager")) {
                    mMessageFrom = "manager";
                } else {
                    mMessageFrom = "";
                }
            } else {
                mMessageFrom = "";
                try {
                    if (Utils.isNotEmpty(conversation.getLastMessage().getStringAttribute(ChatConstant.KEY_TECH_ID)))
                        ;
                    mMessageFrom = "tech";
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                try {
                    if (Utils.isNotEmpty(conversation.getLastMessage().getStringAttribute(ChatConstant.KEY_GAME_CLUB_ID))) {
                        mMessageFrom = "manager";
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }

            if (mMessageFrom.equals("")) {
                // customer
                getContactPermissionChat(conversation);
            } else {
                // tech or manager
                Intent intent = new Intent(getContext(), ChatActivity.class);
                if (conversation.isGroup()) {
                    if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
                        intent.putExtra(ChatConstant.EXTRA_CHAT_TYPE, ChatConstant.CHATTYPE_CHATROOM);
                    } else {
                        intent.putExtra(ChatConstant.EXTRA_CHAT_TYPE, ChatConstant.CHATTYPE_GROUP);
                    }
                }
                conversation.getAllMessages();
                intent.putExtra(ChatConstant.EMCHAT_ID, conversation.getUserName());
                intent.putExtra(ChatConstant.EMCHAT_IS_TECH, mMessageFrom);
                startActivity(intent);
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
                    String username = value.getUserName();


                    EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
                    if (group != null) {
                        username = group.getGroupName();
                    } else {
                        username = UserUtils.getUserNick(value.getUserName());
                        /*ChatUser  chatUser = UserUtils.getUserInfo(value.getUserName());
                        if(chatUser != null){
                            username = chatUser.getNick();
                        }*/
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
        //  MsgDispatcher.dispatchMessage(MsgDef.MSG_DEG_DELETE_CONVERSATION_FROM_DB, bean);
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
