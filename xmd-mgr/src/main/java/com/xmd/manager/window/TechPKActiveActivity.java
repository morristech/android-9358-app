package com.xmd.manager.window;

import android.content.Intent;
import android.os.Parcelable;

import com.xmd.manager.R;
import com.xmd.manager.beans.ActivityRankingBean;
import com.xmd.manager.beans.PKItemBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.PKActivityListResult;
import com.xmd.manager.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;
import rx.Subscription;


/**
 * Created by Lhj on 17-4-7.
 */

public class TechPKActiveActivity extends BaseListActivity<ActivityRankingBean, PKActivityListResult> {


    public static final String PK_ITEM_ID = "pkActivityId"; //活动Id
    public static final String PK_ITEM_STATUS = "activityStatus";//活动状态
    public static final String PK_ITEM_START_DATE = "activityStartDate";//活动开始日期
    public static final String PK_ITEM_END_DATE = "activityEndDate";//活动结束日期
    public static final String PK_ACTIVITY_ITEM = "pk_item";//pk项目
    private EmptyView mEmptyView;
    private Subscription mPkActivityListSubscription;

    @Override
    protected void initOtherViews() {
        super.initOtherViews();
        setTitle(ResourceUtils.getString(R.string.pk_active_title));
    }

    @Override
    protected void setContentViewLayout() {
        setContentView(R.layout.activity_tech_pk_active);
        mEmptyView = (EmptyView) findViewById(R.id.empty_view_widget);
        mPkActivityListSubscription = RxBus.getInstance().toObservable(PKActivityListResult.class).subscribe(
                result -> mEmptyView.setStatus(EmptyView.Status.Gone)
        );
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
    protected void dispatchRequest() {
        mEmptyView.setStatus(EmptyView.Status.Loading);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_PK_ACTIVITY_LIST, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mPkActivityListSubscription);
    }
}
