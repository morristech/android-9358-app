package com.xmd.chat.view;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.chat.ChatSettingManager;
import com.xmd.chat.R;
import com.xmd.chat.beans.FastReplySetting;
import com.xmd.chat.databinding.ActivityChatFastReplySettingAddBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatFastReplySettingEditActivity extends BaseActivity {
    public final static String EXTRA_EDIT_INDEX = "extra_edit_index";

    private ActivityChatFastReplySettingAddBinding binding;

    public ObservableBoolean loading = new ObservableBoolean();
    public CommonRecyclerViewAdapter<String> adapter = new CommonRecyclerViewAdapter<>();

    public ObservableInt words = new ObservableInt(0);
    private int wordsLimit = 30;
    private String content;

    private int editIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_fast_reply_setting_add);
        editIndex = getIntent().getIntExtra(EXTRA_EDIT_INDEX, -1);
        if (editIndex >= 0) {
            setTitle("修改快捷回复");
            content = ChatSettingManager.getInstance().getFastReplySetting().getData().get(editIndex);
            words.set(content.length());
            binding.editText.setText(content);
            binding.editText.setSelection(content.length());
        } else {
            setTitle("添加快捷回复");
        }
        setBackVisible(true);

        binding.setData(this);
    }

    public void onClickSave() {
        if (TextUtils.isEmpty(content)) {
            XToast.show("不能保存纯空格");
            return;
        }
        showLoading();
        FastReplySetting saveSetting;
        FastReplySetting setting = ChatSettingManager.getInstance().getFastReplySetting();
        if (setting == null) {
            setting = new FastReplySetting();
        }
        List<String> list = new ArrayList<>(setting.data);
        if (editIndex < 0) {
            list.add(0, content);
        } else {
            list.set(editIndex, content);
        }
        saveSetting = new FastReplySetting();
        saveSetting.setData(list);
        ChatSettingManager.getInstance().saveFastReply(saveSetting, new Callback<Void>() {
            @Override
            public void onResponse(Void result, Throwable error) {
                hideLoading();
                if (error != null) {
                    XToast.show("保存失败：" + error.getMessage());
                } else {
                    XToast.show("保存成功！");
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    public void onTextChanged(Editable editable) {
        words.set(editable.toString().length());
        content = editable.toString();
    }

    public int getWordsLimit() {
        return wordsLimit;
    }

    public String getContent() {
        return content;
    }
}
