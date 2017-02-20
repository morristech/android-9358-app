package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.ClubJournalBean;
import com.xmd.technician.http.gson.JournalListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;

import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class ClubJournalListFragment extends BaseListFragment<ClubJournalBean> {


    private Subscription mClubJournalListSubscription;

    public static ClubJournalListFragment getInstance() {
        return new ClubJournalListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_JOURNAL_LIST);
    }

    @Override
    protected void initView() {
        mClubJournalListSubscription = RxBus.getInstance().toObservable(JournalListResult.class).subscribe(
                journalListResult -> handleJournalListResult(journalListResult)
        );
    }

    private void handleJournalListResult(JournalListResult journalListResult) {
        if (journalListResult.statusCode == 200) {
            onGetListSucceeded(1, journalListResult.respData);
        } else {
            onGetListFailed(journalListResult.msg);
        }
    }

    @Override
    public void onShareClicked(ClubJournalBean bean) {
        super.onShareClicked(bean);
        ShareController.doShare(bean.image, bean.shareUrl, SharedPreferenceHelper.getUserClubName() + "-" + bean.title,
                "会所最新期刊发布了", Constant.SHARE_COUPON, "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mClubJournalListSubscription);
    }

    @Override
    public boolean isPaged() {
        return false;
    }
}
