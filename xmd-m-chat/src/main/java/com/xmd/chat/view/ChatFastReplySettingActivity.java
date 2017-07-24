package com.xmd.chat.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.chat.BR;
import com.xmd.chat.R;
import com.xmd.chat.SettingManager;
import com.xmd.chat.beans.FastReplySetting;
import com.xmd.chat.databinding.ActivityChatFastReplySettingBinding;

public class ChatFastReplySettingActivity extends BaseActivity {
    private ActivityChatFastReplySettingBinding binding;

    public ObservableBoolean loading = new ObservableBoolean();
    public CommonRecyclerViewAdapter<String> adapter = new CommonRecyclerViewAdapter<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_fast_reply_setting);
        binding.setData(this);

        setTitle("快捷回复设置");
        setBackVisible(true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        loading.set(true);
        SettingManager.getInstance().loadFastReply(new Callback<FastReplySetting>() {
            @Override
            public void onResponse(FastReplySetting result, Throwable error) {
                loading.set(false);
                if (error != null) {
                    XToast.show("加载数据失败：" + error.getMessage());
                    finish();
                    return;
                }
                adapter.setData(R.layout.list_item_setting_fast_reply, BR.data, result.data);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void onClickAdd() {
        Intent intent = new Intent(this, ChatFastReplySettingAddActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadData();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
