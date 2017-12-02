package com.xmd.technician.umengstatistics;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.xmd.app.Constants;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.black.event.EditOrAddCustomerStatisticsEvent;
import com.xmd.chat.event.ChatUmengStatisticsEvent;
import com.xmd.contact.event.ContactUmengStatisticsEvent;
import com.xmd.technician.event.MainPageStatistics;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Lhj on 17-10-12.
 */

public class UmengStatisticsManager {

    private static UmengStatisticsManager manager;
    private Context mContext;

    public UmengStatisticsManager() {

    }

    public static UmengStatisticsManager getStatisticsManagerInstance() {
        if (manager == null) {
            manager = new UmengStatisticsManager();
        }

        return manager;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        EventBusSafeRegister.register(this);
    }

    @Subscribe
    public void mainPageStatisticsSubscribe(MainPageStatistics event) {
        switch (event.index) {
            case Constants.UMENG_STATISTICS_HOME_BROWSE:
                MobclickAgent.onEvent(mContext, Constants.KEY_HOME_BROWSE);
                break;

            case Constants.UMENG_STATISTICS_NEARBY_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_NEARBY_CLICK);
                break;

            case Constants.UMENG_STATISTICS_HELLO_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_HELLO_CLICK);
                break;
        }
    }

    @Subscribe
    public void contactUmengStatisticsSubscribe(ContactUmengStatisticsEvent event) {
        switch (event.index) {
            case Constants.UMENG_STATISTICS_ALL_BROWSE:
                MobclickAgent.onEvent(mContext, Constants.KEY_ALL_BROWSE);
                break;
            case Constants.UMENG_STATISTICS_MINE_BROWSE:
                MobclickAgent.onEvent(mContext, Constants.KEY_MINE_BROWSE);
                break;
            case Constants.UMENG_STATISTICS_RECENTLY_BROWSE:
                MobclickAgent.onEvent(mContext, Constants.KEY_RECENTLY_BROWSE);
                break;
            case Constants.UMENG_STATISTICS_COLLEAGUE_BROWSE:
                MobclickAgent.onEvent(mContext, Constants.KEY_COLLEAGUE_BROWSE);
                break;
            case Constants.UMENG_STATISTICS_NEW_CUSTOMER_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_NEW_CUSTOMER_CLICK);
                break;
            case Constants.UMENG_STATISTICS_FILTER_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_FILTER_CLICK);
                break;
            case Constants.UMENG_STATISTICS_FILTER_CUSTOMER_CHOOSE:
                MobclickAgent.onEvent(mContext, Constants.KEY_FILTER_CUSTOMER_CHOOSE);
                break;
            case Constants.UMENG_STATISTICS_FILTER_VIP_CHOOSE:
                MobclickAgent.onEvent(mContext, Constants.KEY_FILTER_VIP_CHOOSE);
                break;
            case Constants.UMENG_STATISTICS_FILTER_TYPE_CHOOSE:
                MobclickAgent.onEvent(mContext, Constants.KEY_FILTER_TYPE_CHOOSE);
                break;
            case Constants.UMENG_STATISTICS_FILTER_BELONG_CHOOSE:
                MobclickAgent.onEvent(mContext, Constants.KEY_FILTER_BELONG_CHOOSE);
                break;
            case Constants.UMENG_STATISTICS_FILTER_BTN_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_FILTER_BTN_CLICK);
                break;
            case Constants.UMENG_STATISTICS_CUSTOMER_GROUP:
                MobclickAgent.onEvent(mContext, Constants.KEY_FILTER_CUSTOMER_GROUP);
                break;

        }
    }

    @Subscribe
    public void editOrAddCustomerStatisticsSubscribe(EditOrAddCustomerStatisticsEvent event) {
        switch (event.index) {
            case Constants.UMENG_STATISTICS_CUSTOMER_SAVE_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_NEW_CUSTOMER_SAVE_CLICK);
                break;
        }
    }

    @Subscribe
    public void chatUmengStatisticsSubscribe(ChatUmengStatisticsEvent event) {
        switch (event.index) {
            case Constants.UMENG_STATISTICS_PICTURE_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_PICTURE_CLICK);
                break;
            case Constants.UMENG_STATISTICS_PICTURE_SEND:
                MobclickAgent.onEvent(mContext, Constants.KEY_PICTURE_SEND);
                break;
            case Constants.UMENG_STATISTICS_EMOJI_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_EMOJI_CLICK);
                break;
            case Constants.UMENG_STATISTICS_EMOJI_SEND:
                MobclickAgent.onEvent(mContext, Constants.KEY_EMOJI_SEND);
                break;
            case Constants.UMENG_STATISTICS_QUICK_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_QUICK_CLICK);
                break;
            case Constants.UMENG_STATISTICS_QUICK_SEND:
                MobclickAgent.onEvent(mContext, Constants.KEY_QUICK_SEND);
                break;
            case Constants.UMENG_STATISTICS_COUPON_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_COUPON_CLICK);
                break;
            case Constants.UMENG_STATISTICS_COUPON_SEND:
                MobclickAgent.onEvent(mContext, Constants.KEY_COUPON_SEND);
                break;
            case Constants.UMENG_STATISTICS_BOOK_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_BOOK_CLICK);
                break;
            case Constants.UMENG_STATISTICS_BOOKED_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_BOOKED_CLICK);
                break;
            case Constants.UMENG_STATISTICS_BOOKED_SEND:
                MobclickAgent.onEvent(mContext, Constants.KEY_BOOKED_SEND);
                break;
            case Constants.UMENG_STATISTICS_REWARDED_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_REWARDED_CLICK);
                break;
            case Constants.UMENG_STATISTICS_REWARDED_SEND:
                MobclickAgent.onEvent(mContext, Constants.KEY_REWARDED_SEND);
                break;
            case Constants.UMENG_STATISTICS_ACTIVITY_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_ACTIVITY_CLICK);
                break;
            case Constants.UMENG_STATISTICS_ACTIVITY_SEND:
                MobclickAgent.onEvent(mContext, Constants.KEY_ACTIVITY_SEND);
                break;
            case Constants.UMENG_STATISTICS_JOURNAL_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_JOURNAL_CLICK);
                break;
            case Constants.UMENG_STATISTICS_JOURNAL_SEND:
                MobclickAgent.onEvent(mContext, Constants.KEY_JOURNAL_SEND);
                break;
            case Constants.UMENG_STATISTICS_MALL_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_MALL_CLICK);
                break;
            case Constants.UMENG_STATISTICS_MALL_SEND:
                MobclickAgent.onEvent(mContext, Constants.KEY_MALL_SEND);
                break;
            case Constants.UMENG_STATISTICS_LOCATION_CLICK:
                MobclickAgent.onEvent(mContext, Constants.KEY_LOCATION_CLICK);
                break;
            case Constants.UMENG_STATISTICS_BOOK_COMPLETE:
                MobclickAgent.onEvent(mContext, Constants.KEY_BOOK_COMPLETE);
                break;
            case Constants.UMENG_STATISTICS_BOOK_CANCEL:
                MobclickAgent.onEvent(mContext, Constants.KEY_BOOK_CANCEL);
                break;
            case Constants.UMENG_STATISTICS_INVITATION:
                MobclickAgent.onEvent(mContext, Constants.KEY_INVITATION_CLICK);
                break;

        }
    }

    @Subscribe
    public void appointmentUmengStatisticsSubscribe(ChatUmengStatisticsEvent event) {
        switch (event.index) {
            case Constants.UMENG_STATISTICS_BOOK_SEND:
                MobclickAgent.onEvent(mContext, Constants.KEY_BOOK_SEND);
                break;
        }
    }

}
