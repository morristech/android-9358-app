package com.xmd.chat.view;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableInt;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.Constants;
import com.xmd.chat.BR;
import com.xmd.chat.R;
import com.xmd.chat.ShareDataManager;
import com.xmd.chat.databinding.ChatShareListActivityBinding;
import com.xmd.chat.databinding.ChatShareListTypeBinding;
import com.xmd.chat.event.ChatUmengStatisticsEvent;
import com.xmd.chat.viewmodel.ShareViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 各种列表分享共用界面
 * xxxxxxxxx []
 * xxxxxxxxx []
 * ........
 * 传入参数 ：
 * Constants.EXTRA_CHAT_ID : String 对方的聊天ID
 * EXTRA_DATA_TYPE_LIST : List<String> 加载的数据类型， 接下来使用类别从ShareDataManager中读取数据
 * EXTRA_DATA_TYPE_VIEW_LIST: 数据类型对应的布局id
 * 传出参数：
 * Constants.EXTRA_CHAT_ID : String 对方的聊天ID
 * Constants.EXTRA_DATA : ArrayList<ShareViewModel<? extends Parcelable> extends Parcelable> 已选择的数据项
 */

public class ShareListActivity extends BaseActivity {
    public static final String EXTRA_DATA_TYPE_LIST = "extra_data_type_list";
    public static final String ACTIVITY_TITLE = "activity_title";

    private ChatShareListActivityBinding binding;
    private Map<String, List> selectResultMap = new HashMap<>();
    public ObservableInt selectCount = new ObservableInt();

    private List<String> dataTypeList;
    private List<RecyclerView> recyclerViewList;
    private List<ImageView> arrowViewList;

    private ShareDataManager dataManager = ShareDataManager.getInstance();
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.chat_share_list_activity);

        initToolbar();
        setBackVisible(true);
        dataTypeList = getIntent().getStringArrayListExtra(EXTRA_DATA_TYPE_LIST);
        if (dataTypeList == null || dataTypeList.size() == 0) {
            XToast.show("必须设置 " + EXTRA_DATA_TYPE_LIST);
            finish();
            return;
        }
        title = getIntent().getStringExtra(ACTIVITY_TITLE);
        binding.tvTitle.setText(title);
        recyclerViewList = new ArrayList<>();
        arrowViewList = new ArrayList<>();
        for (int i = 0; i < dataTypeList.size(); i++) {
            ChatShareListTypeBinding typeBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.chat_share_list_type, binding.contentView, true);
            typeBinding.setVariable(BR.data, dataTypeList.get(i));
            typeBinding.setVariable(BR.handler, this);
            arrowViewList.add(typeBinding.arrowView);

            RecyclerView recyclerView = new RecyclerView(this);
            recyclerViewList.add(recyclerView);

            binding.contentView.addView(recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            final int finalI = i;
            CommonRecyclerViewAdapter<ShareViewModel> adapter
                    = new CommonRecyclerViewAdapter<ShareViewModel>() {
                @Override
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                    ViewGroup contentView = (ViewGroup) viewHolder.getBinding().getRoot().findViewById(R.id.contentView);

                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.getContext()),
                            dataManager.getDataTypeLayoutId(dataTypeList.get(finalI)),
                            contentView,
                            true);
                    return viewHolder;
                }

                @Override
                public void onBindViewHolder(ViewHolder holder, int position) {
                    ViewGroup contentView = (ViewGroup) holder.getBinding().getRoot().findViewById(R.id.contentView);
                    ViewDataBinding binding = DataBindingUtil.getBinding(contentView.getChildAt(0));
                    binding.setVariable(BR.data, getData(position).getData());
                    super.onBindViewHolder(holder, position);
                }
            };
            List<ShareViewModel> dataList = new ArrayList<>();
            for (Object data : ShareDataManager.getInstance().getDataList(dataTypeList.get(i))) {
                ShareViewModel d = new ShareViewModel<>(data);
                d.setListener(new OnClickListener(dataTypeList.get(i), d));
                dataList.add(d);
            }
            adapter.setData(R.layout.chat_share_list_item_wrapper, BR.data, dataList);
            recyclerView.setAdapter(adapter);
        }


        binding.setData(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick() {
        dataManager.onShare(getIntent().getStringExtra(Constants.EXTRA_CHAT_ID), selectResultMap);
        finish();
    }

    private class OnClickListener implements View.OnClickListener {
        private String type;
        private ShareViewModel data;

        public OnClickListener(String type, ShareViewModel data) {
            this.type = type;
            this.data = data;
        }

        @Override
        public void onClick(View v) {
            if (title.equals("营销活动")) {
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_ACTIVITY_SEND));
            } else if (title.equals("电子期刊")) {
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_JOURNAL_SEND));
            } else {
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_MALL_SEND));
            }
            data.select.set(!data.select.get());
            List dataList = selectResultMap.get(type);
            if (dataList == null) {
                dataList = new ArrayList();
                selectResultMap.put(type, dataList);
            }
            if (data.select.get()) {
                dataList.add(data.getData());
                selectCount.set(selectCount.get() + 1);
            } else {
                dataList.remove(data.getData());
                selectCount.set(selectCount.get() - 1);
            }
        }
    }

    public void onClickGroup(String title) {
        int position = dataTypeList.indexOf(title);
        if (position < 0) {
            return;
        }
        RecyclerView recyclerView = recyclerViewList.get(position);
        boolean isShow = recyclerView.getVisibility() == View.VISIBLE;
        recyclerView.setVisibility(isShow ? View.GONE : View.VISIBLE);
        arrowViewList.get(position).setImageResource(isShow ? R.drawable.arrow_right : R.drawable.arrow_down);
    }
}
