package com.xmd.chat.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.app.BaseFragment;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.chat.BR;
import com.xmd.chat.ChatMessageManager;
import com.xmd.chat.R;
import com.xmd.chat.databinding.FragmentSubmenuFastReplyBinding;
import com.xmd.chat.viewmodel.SubMenuFastReplyViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo on 17-7-6.
 * 聊天菜单--表情
 */

public class SubmenuFastReplyFragment extends BaseFragment {
    private FragmentSubmenuFastReplyBinding mBinding;
    CommonRecyclerViewAdapter<SubMenuFastReplyViewModel> adapter = new CommonRecyclerViewAdapter<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_submenu_fast_reply, container, false);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.setAdapter(adapter);
        return mBinding.getRoot();
    }

    public void setData(final String remoteChatId, List<String> list) {
        List<SubMenuFastReplyViewModel> dataList = new ArrayList<>();
        for (final String s : list) {
            dataList.add(new SubMenuFastReplyViewModel(s, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatMessageManager.getInstance().sendTextMessage(remoteChatId, s);
                }
            }));
        }
        adapter.setData(R.layout.list_item_submenu_fast_reply, BR.data, dataList);
        adapter.notifyDataSetChanged();
    }
}
