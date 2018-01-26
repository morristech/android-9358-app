package com.xmd.cashier.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.cashier.R;
import com.xmd.cashier.adapter.ConfigurationAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.ConfigEntry;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.MonitorManager;
import com.xmd.cashier.pos.PosImpl;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;
import com.xmd.m.network.BaseBean;
import com.xmd.m.notify.push.XmdPushManager;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by zr on 17-7-31.
 */

public class ConfigurationActivity extends BaseActivity {
    private static final String TAG = "ConfigurationActivity";
    private RecyclerView mConfigList;
    private Button mUploadBtn;
    private List<ConfigEntry> mData = new ArrayList<>();
    private ConfigurationAdapter mAdapter;
    private Subscription mUploadLogSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        showToolbar(R.id.toolbar, "参数配置");
        mUploadBtn = (Button) findViewById(R.id.btn_upload);
        mConfigList = (RecyclerView) findViewById(R.id.rv_config);
        mData.add(new ConfigEntry("clientId", XmdPushManager.getInstance().getClientId()));
        mData.add(new ConfigEntry("bindGetui", XmdPushManager.getInstance().isBound() + ""));
        mData.add(new ConfigEntry("DeviceId | EN", PosImpl.getInstance().getPosIdentifierNo()));
        mData.add(new ConfigEntry("最近上传日志时间", SPManager.getInstance().getLastUploadTime()));
        mAdapter = new ConfigurationAdapter(this);
        mAdapter.setData(mData);
        mConfigList.setHasFixedSize(true);
        mConfigList.setLayoutManager(new LinearLayoutManager(this));
        mConfigList.setItemAnimator(new DefaultItemAnimator());
        mConfigList.addItemDecoration(new CustomRecycleViewDecoration(2));
        mConfigList.setAdapter(mAdapter);

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isNetworkEnabled(ConfigurationActivity.this)) {
                    XToast.show("当前网络状况不佳，无法上传");
                    return;
                }

                if (Utils.isWifiNetwork(ConfigurationActivity.this)) {
                    uploadLog();    //wifi环境
                } else {
                    new CustomAlertDialogBuilder(ConfigurationActivity.this)
                            .setMessage("上传日志需要网络流量，当前网络非Wifi网络，确认继续上传?")
                            .setPositiveButton("继续上传", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    uploadLog();
                                }
                            })
                            .setNegativeButton("取消上传", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            }
        });
    }

    private void uploadLog() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "上传日志：" + RequestConstant.URL_APP_UPLOAD_LOG);
        showLoading();
        if (mUploadLogSubscription != null) {
            mUploadLogSubscription.unsubscribe();
        }
        mUploadLogSubscription = MonitorManager.getInstance().uploadLogFile(null, new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                hideLoading();
                XToast.show("上传成功");
            }

            @Override
            public void onError(String error) {
                hideLoading();
                XToast.show(error);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUploadLogSubscription != null) {
            mUploadLogSubscription.unsubscribe();
        }
    }
}
