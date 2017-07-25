package com.xmd.chat.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.chat.BR;
import com.xmd.chat.R;
import com.xmd.chat.SettingManager;
import com.xmd.chat.beans.FastReplySetting;
import com.xmd.chat.databinding.ActivityChatFastReplySettingBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatFastReplySettingActivity extends BaseActivity {
    private ActivityChatFastReplySettingBinding binding;

    public ObservableBoolean loading = new ObservableBoolean();
    public CommonRecyclerViewAdapter<String> adapter = new CommonRecyclerViewAdapter<>();

    private List<String> beforeDeleteList = new ArrayList<>();

    private final static int REQUEST_ADD = 1;
    private final static int REQUEST_EDIT = 2;

    private int editIndex;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_fast_reply_setting);
        binding.setData(this);

        setTitle("快捷回复设置");
        setBackVisible(true);

        adapter.setHandler(BR.handler, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.inset(0, 1);
            }
        });
        itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);

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
        if (SettingManager.getInstance().getFastReplySetting() == null ||
                SettingManager.getInstance().getFastReplySetting().data.size() >= 20) {
            XToast.show("最多只能自定义20条快捷回复!");
            return;
        }
        Intent intent = new Intent(this, ChatFastReplySettingEditActivity.class);
        startActivityForResult(intent, REQUEST_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD) {
            if (resultCode == RESULT_OK) {
                loadData();
                setResult(RESULT_OK);
            }
            return;
        }

        if (requestCode == REQUEST_EDIT) {
            if (resultCode == RESULT_OK) {
                loadData();
                setResult(RESULT_OK);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ItemTouchHelper.Callback itemTouchCallback = new ItemTouchHelper.Callback() {
        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(adapter.getDataList(), fromPosition, toPosition);
            adapter.notifyItemMoved(fromPosition, toPosition);
            updateSetting(adapter.getDataList(), false);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.START) {
                beforeDeleteList.clear();
                beforeDeleteList.addAll(adapter.getDataList());
                int position = viewHolder.getAdapterPosition();
                adapter.getDataList().remove(position);
                adapter.notifyItemRemoved(position);
                updateSetting(adapter.getDataList(), false);
            }
        }
    };

    private void updateSetting(List<String> dataList, final boolean isCancel) {
        FastReplySetting setting = new FastReplySetting();
        setting.getData().addAll(dataList);
        SettingManager.getInstance().saveFastReply(setting, new Callback<Void>() {
            @Override
            public void onResponse(Void result, Throwable error) {
                setResult(RESULT_OK);
                XLogger.d("save setting .... " + (error == null ? "ok" : "failed:" + error.getMessage()));
                if (error != null) {
                    Snackbar.make(binding.recyclerView, "删除失败：" + error.getMessage(), Snackbar.LENGTH_LONG);
                    loadData();
                }
//                if (error == null) {
//                    if (!isCancel) {
//                        Snackbar.make(binding.recyclerView, "删除成功", Snackbar.LENGTH_LONG)
//                                .setAction("撤消", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        updateSetting(beforeDeleteList, true);
//                                    }
//                                })
//                                .show();
//                    } else {
//                        loadData();
//                        XToast.show("撤消成功");
//                    }
//                }
            }
        });
    }

    public void onEdit(String data) {
        editIndex = adapter.getDataList().indexOf(data);
        if (editIndex < 0) {
            XToast.show("发生错误，无法编辑");
            return;
        }
        Intent intent = new Intent(this, ChatFastReplySettingEditActivity.class);
        intent.putExtra(ChatFastReplySettingEditActivity.EXTRA_EDIT_INDEX, editIndex);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    public void onDelete(String data) {
        int position = adapter.getDataList().indexOf(data);
        if (position < 0) {
            XToast.show("发生错误，无法删除");
            return;
        }
        adapter.getDataList().remove(position);
        adapter.notifyItemRemoved(position);
        updateSetting(adapter.getDataList(), false);
    }

    public boolean onTouch(View v, MotionEvent event, String data) {
        int position = adapter.getDataList().indexOf(data);
        if (position < 0) {
            XToast.show("发生错误，无法移动");
            return false;
        }
        RecyclerView.ViewHolder viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(position);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            itemTouchHelper.startDrag(viewHolder);
        }
        return false;
    }
}
