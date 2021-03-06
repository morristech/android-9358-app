package com.xmd.technician.onlinepaynotify.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.widget.TextView;

import com.xmd.app.widget.CircleAvatarView;
import com.xmd.technician.R;
import com.xmd.technician.common.DateUtils;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfo;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfoManager;

import java.util.Locale;

/**
 * Created by heyangya on 17-1-18.
 */

public class PayNotifyInfoViewModel {
    public PayNotifyInfo info;
    public ObservableBoolean isArchived = new ObservableBoolean();

    public PayNotifyInfoViewModel(PayNotifyInfo info) {
        this.info = info;
        isArchived.set(info.isArchived);
    }

    @BindingAdapter("time")
    public static void setTime(TextView view, long time) {
        view.setText(DateUtils.getSdf("yyyy-MM-dd HH:mm").format(time));
    }

    @BindingAdapter("moneyfen")
    public static void setMoneyFen(TextView view, long amount) {
        view.setText(String.format(Locale.getDefault(), ".%02d", amount % 100));
    }

    @BindingAdapter("moneyyuan")
    public static void setMoneyYuan(TextView view, long amount) {
        view.setText(String.format(Locale.getDefault(), "%d", amount / 100));
    }

    @BindingAdapter("othertech")
    public static void setOtherTech(TextView view, PayNotifyInfo info) {
        StringBuilder builder = new StringBuilder("合并买单：");
        if (info.combineTechs != null && info.combineTechs.size() > 0) {
            for (String tech : info.combineTechs) {
                builder.append("[" + tech + "]、");
            }
            builder.setLength(builder.length() - 1);
        } else {
            builder.append("— —");
        }
        view.setText(builder.toString());
    }

    @BindingAdapter("status")
    public static void setStatus(TextView view, int status) {
        String text = "";
        int color = R.color.color616161;
        switch (status) {
            case PayNotifyInfo.STATUS_UNVERIFIED:
                text = "待确认";
                break;
            case PayNotifyInfo.STATUS_REJECTED:
                text = "异常";
                color = R.color.colorPrimary;
                break;
            case PayNotifyInfo.STATUS_ACCEPTED:
                text = "已确认";
                break;
        }
        view.setText(text);
        view.setTextColor(view.getResources().getColor(color));
    }

    @BindingAdapter("avatar")
    public static void setAvatar(CircleAvatarView view, PayNotifyInfo data) {
        if (data != null) {
            view.setUserInfo(data.userId, data.userAvatar, false);
        }
    }

    public void setArchived() {
        isArchived.set(true);
        info.isArchived = true;
        PayNotifyInfoManager.getInstance().setPayNotifyInfoArchived(info);
    }
}
