package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.CustomerTypeExpandableAdapter;
import com.xmd.manager.beans.FavourableActivityBean;
import com.xmd.manager.beans.FavourableActivityGroup;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.FavourableActivityListResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sdcm on 17-5-22.
 */
public class GMessageSelectCouponFragment extends BaseFragment implements CustomerTypeExpandableAdapter.Callback {
    @BindView(R.id.select_coupon_hint)
    TextView selectCouponHint;
    @BindView(R.id.coupon_list)
    ExpandableListView couponListView;

    private CustomerTypeExpandableAdapter couponAdapter;

    private List<FavourableActivityGroup> couponGroupList = new ArrayList<>();
    private List<List<FavourableActivityBean>> couponChildList = new ArrayList<>();
    private int checkedGroupPosition = -1;
    private int checkedChildPosition = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_select_coupon, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        initCouponListView();
        initHintView();
        initData();
    }

    private void initData() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, "");
        params.put(RequestConstant.KEY_PAGE_SIZE, "100");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ClUB_FAVOURABLE_ACTIVITY, params);
    }

    private void initCouponListView() {
        couponListView.setGroupIndicator(null);
        couponAdapter = new CustomerTypeExpandableAdapter(couponGroupList, couponChildList, CustomerTypeExpandableAdapter.GROUP_TYPE_ACTIVITY_GROUP, this);
        couponListView.setAdapter(couponAdapter);
        couponListView.setOnGroupExpandListener(groupPosition -> {
            for (int i = 0; i < couponAdapter.getGroupCount(); i++) {
                if (groupPosition != i) {
                    couponListView.collapseGroup(i);
                }
            }
        });
    }

    private void initHintView() {
        if (checkedGroupPosition == -1) {
            selectCouponHint.setText(ResourceUtils.getString(R.string.text_no_activity_selected));
        } else {
            FavourableActivityBean bean = couponChildList.get(checkedGroupPosition).get(checkedChildPosition);
            selectCouponHint.setText(Utils.changeColor(String.format("已选择【%s】%s", Constant.MESSAGE_ACTIVITY_LABELS.get(bean.msgType), bean.name),
                    ResourceUtils.getColor(R.color.colorMain), 4, Constant.MESSAGE_ACTIVITY_LABELS.get(bean.msgType).length() + 4));
        }
    }

    @OnClick({R.id.btn_next_step, R.id.btn_previous_step})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous_step:
                clear();
                ((GroupMessageCustomerActivity) getActivity()).gotoCustomerFragment();
                break;
            case R.id.btn_next_step:
                ((GroupMessageCustomerActivity) getActivity()).gotoEditContentFragment();
                break;
        }
    }

    private void clear() {
        checkedGroupPosition = -1;
        checkedChildPosition = -1;
        for (int i = 0; i < couponAdapter.getGroupCount(); i++) {
            couponListView.collapseGroup(i);
        }
        initHintView();
    }

    public void handleFavourableActivityResult(FavourableActivityListResult result) {
        if (result.statusCode == 200) {
            couponChildList.clear();
            couponGroupList.clear();
            for (int i = 0; i < result.respData.size(); i++) {
                if (result.respData.get(i).list != null && !result.respData.get(i).list.isEmpty()) {
                    couponChildList.add(result.respData.get(i).list);
                    couponGroupList.add(result.respData.get(i));
                }
            }

            couponAdapter.setData(couponGroupList, couponChildList);
            if (couponGroupList.isEmpty()) {
                selectCouponHint.setText("暂无上线活动");
            }
        }
    }

    @Override
    public void onItemClicked(int groupPosition, int childPosition) {
        checkedGroupPosition = groupPosition;
        checkedChildPosition = childPosition;
        initHintView();
    }

    @Override
    public void onCreateGroupButtonClicked() {

    }

    @Override
    public boolean isChecked(int groupPosition, int childPosition) {
        if ((checkedGroupPosition == groupPosition) && (checkedChildPosition == childPosition)) {
            return true;
        }
        return false;
    }

    public FavourableActivityBean getCouponInfo() {
        if ((checkedGroupPosition == -1) || (checkedChildPosition == -1)) {
            return null;
        }

        return couponChildList.get(checkedGroupPosition).get(checkedChildPosition);

    }
}
