package com.xmd.chat.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.app.BaseFragment;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.EmojiManager;
import com.xmd.chat.BR;
import com.xmd.chat.R;
import com.xmd.chat.databinding.FragmentSubmenuEmojiBinding;
import com.xmd.chat.viewmodel.SubMenuEmojiViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo on 17-7-6.
 * 聊天菜单--表情
 */

public class SubmenuFragmentEmoji extends BaseFragment {
    private FragmentSubmenuEmojiBinding mBinding;
    CommonRecyclerViewAdapter<SubMenuEmojiViewModel> adapter = new CommonRecyclerViewAdapter<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_submenu_emoji, container, false);
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        mBinding.recyclerView.setAdapter(adapter);
        return mBinding.getRoot();
    }

    public void setOutputView(final Editable output) {
        List<SubMenuEmojiViewModel> dataList = new ArrayList<>();
        for (final String key : EmojiManager.getInstance().getEmojiMap().keySet()) {
            dataList.add(new SubMenuEmojiViewModel(key, EmojiManager.getInstance().getEmojiMap().get(key), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    output.append(EmojiManager.getInstance().getEmojiSpannableString(key));
                }
            }));
        }
        adapter.setData(R.layout.list_item_submenu_emoji, BR.data, dataList);
        adapter.notifyDataSetChanged();
    }
}
