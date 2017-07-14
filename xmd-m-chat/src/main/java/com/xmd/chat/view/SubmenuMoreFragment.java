package com.xmd.chat.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.app.BaseFragment;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.chat.BR;
import com.xmd.chat.ChatMenu;
import com.xmd.chat.R;
import com.xmd.chat.databinding.FragmentSubmenuMoreBinding;
import com.xmd.chat.viewmodel.SubMenuMoreViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo on 17-7-6.
 * 聊天菜单--表情
 */

public class SubmenuMoreFragment extends BaseFragment {
    private FragmentSubmenuMoreBinding mBinding;
    CommonRecyclerViewAdapter<SubMenuMoreViewModel> adapter = new CommonRecyclerViewAdapter<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_submenu_more, container, false);
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mBinding.recyclerView.setAdapter(adapter);
        return mBinding.getRoot();
    }

    public void setData(List<ChatMenu> list) {
        List<SubMenuMoreViewModel> dataList = new ArrayList<>();
        for (final ChatMenu menu : list) {
            dataList.add(new SubMenuMoreViewModel(menu, menu.listener));
        }
        adapter.setData(R.layout.list_item_submenu_more, BR.data, dataList);
        adapter.notifyDataSetChanged();
    }
}
