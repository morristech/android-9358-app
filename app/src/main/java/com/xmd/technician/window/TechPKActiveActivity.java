package com.xmd.technician.window;

import android.content.Intent;
import android.os.Parcelable;
import android.widget.Button;

import com.xmd.technician.R;
import com.xmd.technician.bean.ActivityRankingBean;
import com.xmd.technician.bean.PKItemBean;
import com.xmd.technician.common.DateUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.PKActivityListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-4-7.
 */

public class TechPKActiveActivity extends BaseListActivity<ActivityRankingBean> {

    @Bind(R.id.btn_pk_ranking_detail)
    Button btnPkRankingDetail;

    public static final String PK_ITEM_ID = "pkActivityId"; //活动Id
    public static final String PK_ITEM_STATUS = "activityStatus";//活动状态
    public static final String PK_ITEM_START_DATE = "activityStartDate";//活动开始日期
    public static final String PK_ITEM_END_DATE = "activityEndDate";//活动结束日期
    public static final String PK_ACTIVITY_ITEM = "pk_item";//pk项目
    private EmptyView mEmptyView;
    private Subscription mPKActivityListSubscription;

    @Override
    protected void dispatchRequest() {
        mEmptyView.setStatus(EmptyView.Status.Loading);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_PK_ACTIVITY_LIST, params);
    }

    @Override
    protected void initView() {
        setTitle(ResourceUtils.getString(R.string.pk_active_title));
        setBackVisible(true);
        mPKActivityListSubscription = RxBus.getInstance().toObservable(PKActivityListResult.class).subscribe(
                activityListResult -> handlePKActivityListResult(activityListResult)
        );
    }

    private void handlePKActivityListResult(PKActivityListResult activityListResult) {
        mEmptyView.setStatus(EmptyView.Status.Gone);
        if (activityListResult.statusCode == 200) {
            onGetListSucceeded(activityListResult.pageCount, activityListResult.respData);
        } else {
            onGetListFailed(activityListResult.msg);
        }
    }

    @Override
    protected void setContentViewLayout() {
        setContentView(R.layout.activity_tech_pk_active);
        mEmptyView = (EmptyView) findViewById(R.id.empty_view_widget);
    }

    @OnClick(R.id.btn_pk_ranking_detail)
    public void onClick() {
        Intent personalRanking = new Intent(TechPKActiveActivity.this, TechPersonalRankingDetailActivity.class);
        startActivity(personalRanking);
    }

    @Override
    public void onItemClicked(ActivityRankingBean bean) {
        super.onItemClicked(bean);
        List<PKItemBean> pkList = new ArrayList<>();
        Intent intent = new Intent(TechPKActiveActivity.this, TechPKRankingDetailActivity.class);
        intent.putExtra(PK_ITEM_ID, bean.pkActivityId);
        intent.putExtra(PK_ITEM_STATUS, bean.status);
        intent.putExtra(PK_ITEM_START_DATE, bean.startDate);
        if (Utils.isNotEmpty(bean.endDate)) {
            intent.putExtra(PK_ITEM_END_DATE, bean.endDate);
        } else {
            intent.putExtra(PK_ITEM_END_DATE, DateUtil.getDate(System.currentTimeMillis()));
        }
        if (Utils.isNotEmpty(bean.rankingList.get(0).categoryId)) {
            for (int i = 0; i < bean.rankingList.size(); i++) {
                pkList.add(new PKItemBean(bean.rankingList.get(i).categoryId, bean.rankingList.get(i).categoryName));
            }
        } else {
            pkList.add(new PKItemBean(bean.categoryId, bean.categoryName));
        }

        intent.putParcelableArrayListExtra(PK_ACTIVITY_ITEM, (ArrayList<? extends Parcelable>) pkList);
        startActivity(intent);
    }

    @Override
    public boolean isPaged() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mPKActivityListSubscription);
    }
}
