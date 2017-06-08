package com.xmd.appointment;

import android.content.Context;
import android.content.Intent;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.Constants;
import com.xmd.app.IFunctionInit;
import com.xmd.app.XmdApp;
import com.xmd.app.net.BaseBean;
import com.xmd.app.net.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by heyangya on 17-5-23.
 * 初始化预约功能
 */

public class XmdModuleAppointment implements IFunctionInit {
    private static final XmdModuleAppointment ourInstance = new XmdModuleAppointment();

    public static XmdModuleAppointment getInstance() {
        return ourInstance;
    }

    private XmdModuleAppointment() {

    }

    @Override
    public void init(Context context) {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(AppointmentEvent event) {
        switch (event.getCmd()) {
            case AppointmentEvent.CMD_SHOW:
                Intent intent = new Intent(XmdApp.getInstance().getContext(), AppointmentActivity.class);
                intent.putExtra(Constants.EXTRA_DATA, event.getData());
                intent.putExtra(Constants.EXTAR_EVENT_TAG, event.getTag());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                XmdApp.getInstance().getContext().startActivity(intent);
                break;
            case AppointmentEvent.CMD_SUBMIT:
                submitAppointment(event);
                break;
        }
    }

    private void submitAppointment(final AppointmentEvent event) {
        if (event.getData() == null) {
            XLogger.e("submitAppointment data is null!");
            postSubmitError(event, "submitAppointment data is null!");
            return;
        }
        XLogger.i("submitAppointment: " + event.getData());
        DataManager.getInstance().submitAppointment(event.getData(), new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                event.getData().setSubmitSuccess(true);
                event.getData().setSubmitOrderId((String) result.getRespData());
                AppointmentEvent backEvent = new AppointmentEvent(AppointmentEvent.CMD_SUBMIT_RESULT, event.getData());
                backEvent.setTag(event.getTag());
                EventBus.getDefault().post(backEvent);
            }

            @Override
            public void onCallbackError(Throwable e) {
                postSubmitError(event, e.getLocalizedMessage());
            }
        });
    }

    private void postSubmitError(AppointmentEvent event, String error) {
        event.getData().setSubmitSuccess(false);
        event.getData().setSubmitErrorString(error);
        AppointmentEvent backEvent = new AppointmentEvent(AppointmentEvent.CMD_SUBMIT_RESULT, event.getData());
        backEvent.setTag(event.getTag());
        EventBus.getDefault().post(backEvent);
    }
}
