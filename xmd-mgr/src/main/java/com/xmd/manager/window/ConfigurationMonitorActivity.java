package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xmd.manager.AppConfig;
import com.xmd.manager.R;
import com.xmd.manager.adapter.EntryRecycleViewAdapter;
import com.xmd.manager.widget.DividerItemDecoration;

import butterknife.BindView;

public class ConfigurationMonitorActivity extends BaseActivity {

    @BindView(R.id.configuration_list)
    RecyclerView mConfigurationRecycleView;

    private LinearLayoutManager mLayoutManager;
    private EntryRecycleViewAdapter mConfigurationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_monitor);
        initContent();
    }

    private void initContent() {
        mLayoutManager = new LinearLayoutManager(this);
        mConfigurationRecycleView.setHasFixedSize(true);
        mConfigurationRecycleView.setLayoutManager(mLayoutManager);
        mConfigurationListAdapter = new EntryRecycleViewAdapter(AppConfig.generateEntryList());
        mConfigurationRecycleView.setAdapter(mConfigurationListAdapter);
        mConfigurationRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

}
