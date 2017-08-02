package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.ConfigurationAdapter;
import com.xmd.cashier.dal.bean.ConfigEntry;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;
import com.xmd.m.notify.push.XmdPushManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-7-31.
 */

public class ConfigurationActivity extends BaseActivity {
    private RecyclerView mConfigList;
    private List<ConfigEntry> mData = new ArrayList<>();
    private ConfigurationAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        showToolbar(R.id.toolbar, "参数配置");
        mConfigList = (RecyclerView) findViewById(R.id.rv_config);
        mData.add(new ConfigEntry("clientId", XmdPushManager.getInstance().getClientId()));
        mData.add(new ConfigEntry("bindGetui", XmdPushManager.getInstance().isBound() + ""));
        mAdapter = new ConfigurationAdapter(this);
        mAdapter.setData(mData);
        mConfigList.setHasFixedSize(true);
        mConfigList.setLayoutManager(new LinearLayoutManager(this));
        mConfigList.setItemAnimator(new DefaultItemAnimator());
        mConfigList.addItemDecoration(new CustomRecycleViewDecoration(2));
        mConfigList.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
