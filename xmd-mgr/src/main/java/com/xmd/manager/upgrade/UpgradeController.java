package com.xmd.manager.upgrade;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import com.shidou.update.IUpdater;
import com.shidou.update.UpdateConfig;
import com.shidou.update.UpdateConstants;
import com.shidou.update.UpdaterController;
import com.xmd.app.XmdActivityManager;
import com.xmd.manager.Constant;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.AbstractController;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.AppUpdateConfigResult;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by sdcm on 15-10-22.
 */
public class UpgradeController extends AbstractController {
    private UpdaterController mUpdaterController;
    private Context mContext;
    private boolean mIsForeUpdate;
    private boolean mIsBackgroundUpdate;

    public void init(Context context) {
        mContext = context;
        mUpdaterController = new UpdaterController(mContext);
        RxBus.getInstance().toObservable(AppUpdateConfigResult.class).subscribe(
                configResult -> {
                    if (!configResult.update) {
                        if (!mIsBackgroundUpdate) {
                            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN,
                                    () -> Toast.makeText(context, "当前已是最新版本", Toast.LENGTH_LONG).show());
                        }
                    } else {
                        mIsForeUpdate = configResult.config.type == UpdateConstants.UPDATE_TYPE_FORCE;
                        mUpdaterController.startUpdate(configResult.config, new IUpdater.UpdateListener() {
                            @Override
                            public void onUpdateError(UpdateConfig updateConfig, int i, String s) {

                            }

                            @Override
                            public void onUserCancel(UpdateConfig updateConfig) {
                                if (mIsForeUpdate) {
                                    XmdActivityManager.getInstance().exitApplication();
                                }
                            }

                            @Override
                            public void onUpdateComplete(UpdateConfig updateConfig) {

                            }
                        });
                    }
                });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MsgDef.MSG_DEF_AUTO_CHECK_UPGRADE:
                autoCheckUpdate();
                break;
            case MsgDef.MSG_DEF_MANUALLY_CHECK_UPGRADE:
                manuallyCheckUpdate();
                break;
        }

        return true;
    }

    private void autoCheckUpdate() {
        mIsBackgroundUpdate = true;
        getConfig();
    }

    private void manuallyCheckUpdate() {
        mIsBackgroundUpdate = false;
        getConfig();
    }

    private void getConfig() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_UPDATE_USER_ID, SharedPreferenceHelper.getUserId());
        params.put(RequestConstant.KEY_UPDATE_VERSION, String.valueOf(Utils.getVersionCode()));
        params.put(RequestConstant.KEY_UPDATE_APP_ID, String.valueOf(Constant.APP_ID));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_APP_UPDATE_CONFIG, params);
    }
}
