package com.xmd.technician.window;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xmd.technician.Adapter.EntryRecycleViewAdapter;
import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.widget.DividerItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/22.
 */
public class ConfigurationMonitorActivity extends BaseActivity {
    @Bind(R.id.configuration_list)
    RecyclerView mConfigurationRecycleView;

    private LinearLayoutManager mLayoutManager;
    private EntryRecycleViewAdapter mConfigurationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_monitor);
        setTitle("配置信息");
        setBackVisible(true);
        ButterKnife.bind(this);
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
