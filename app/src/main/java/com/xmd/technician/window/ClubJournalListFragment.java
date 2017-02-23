package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.ClubJournalBean;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.gson.JournalListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;
import com.xmd.technician.widget.EmptyView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class ClubJournalListFragment extends BaseListFragment<ClubJournalBean> {


    @Bind(R.id.empty_view_widget)
    EmptyView mEmptyViewWidget;
    private Subscription mClubJournalListSubscription;
    private int mTotalAmount;

    public static ClubJournalListFragment getInstance(int totalAmount) {
        ClubJournalListFragment cf = new ClubJournalListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT, totalAmount);
        cf.setArguments(bundle);
        return cf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTotalAmount = getArguments().getInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT);
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        return view;
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
            if (journalListResult.respData == null) {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_PROPAGANDA_LIST);
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mEmptyViewWidget.setEmptyViewWithDescription(R.drawable.ic_failed, "期刊已下线");
            } else {
                if (journalListResult.respData.size() != mTotalAmount) {
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_PROPAGANDA_LIST);
                }
                onGetListSucceeded(1, journalListResult.respData);
            }
        } else {
            onGetListFailed(journalListResult.msg);
        }
    }

    @Override
    public void onShareClicked(ClubJournalBean bean) {
        super.onShareClicked(bean);
        ShareController.doShare(bean.image, bean.shareUrl,  bean.title,
                bean.subTitle, Constant.SHARE_COUPON, "");
    }

    @Override
    public void onItemClicked(ClubJournalBean bean) throws HyphenateException {
        super.onItemClicked(bean);
        if(Utils.isNotEmpty(bean.shareUrl)){
            bean.shareUrl = "http://192.168.1.105:9880/spa-manager/journal/#/1?id=99&userId=768364493666258944";
            ShareDetailActivity.startShareDetailActivity(getActivity(),bean.shareUrl, ResourceUtils.getString(R.string.club_journal_list_title),false);

        }else{
            return;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mClubJournalListSubscription);
        ButterKnife.unbind(this);
    }

    @Override
    public boolean isPaged() {
        return false;
    }
}
