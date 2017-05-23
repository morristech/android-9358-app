package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.adapter.CustomerTypeExpandableAdapter;
import com.xmd.manager.beans.GroupBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.GroupInfoResult;
import com.xmd.manager.service.response.GroupListResult;
import com.xmd.manager.service.response.TechListResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sdcm on 17-5-19.
 */
public class GMessageSelectCustomerFragment extends BaseFragment implements CustomerTypeExpandableAdapter.Callback {

    @Bind(R.id.total_send_amount)
    TextView limitSendAmount;
    @Bind(R.id.total_send_time_limit)
    TextView totalSendTimeLimit;
    @Bind(R.id.group_all)
    TextView groupAll;
    @Bind(R.id.select_customer_count)
    TextView selectCustomerCountText;

    @Bind(R.id.group_customer_expand_list)
    ExpandableListView customerGroupListView;

    private List<GroupBean> mGroupList = new ArrayList<>();

    private List<String> groupIds;

    private CustomerTypeExpandableAdapter customerExpandAdapter;

    private String currentGroupType = "";
    private String currentGroupIds = "";
    private int selectCustomerCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_select_customer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initView() {
        groupIds = new ArrayList<>();
        initCustomerTypeView();
        initData();
        initCustomerCountTextView();
    }

    private void initCustomerCountTextView() {
        if (selectCustomerCount == 0) {
            selectCustomerCountText.setText("请选择客户");
        } else {
            String s = " 已选中" + String.valueOf(selectCustomerCount) + "个客户";
            selectCustomerCountText.setText(Utils.changeColor(s, ResourceUtils.getColor(R.color.colorMain), 12, s.length() - 1));
        }
    }

    private void initCustomerTypeView() {
        customerGroupListView.setGroupIndicator(null);
        String[] group = new String[]{
                ResourceUtils.getString(R.string.group_by_behavior),
                ResourceUtils.getString(R.string.group_by_tech),
                ResourceUtils.getString(R.string.group_by_customize)};

        customerExpandAdapter = new CustomerTypeExpandableAdapter(Arrays.asList(group), this);
        customerGroupListView.setAdapter(customerExpandAdapter);

        String[] consumeGroup = new String[]{
                ResourceUtils.getString(R.string.active_customer),
                ResourceUtils.getString(R.string.valid_customer)};
        customerExpandAdapter.setChildData(0, Arrays.asList(consumeGroup));
        customerGroupListView.setOnGroupExpandListener(groupPosition -> {
            for (int i = 0; i < customerExpandAdapter.getGroupCount(); i++) {
                if (groupPosition != i) {
                    customerGroupListView.collapseGroup(i);
                }
            }
        });
    }

    private void initData() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_GROUP_INFO);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ClUB_GROUP_LIST);
        //技师列表
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, "1");
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(1000));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_TECH_LIST, params);

    }

    public void handlerGroupList(GroupListResult groupListResult) {
        if (groupListResult.statusCode == 200) {
            mGroupList.clear();
            for (int i = 0; i < groupListResult.respData.size(); i++) {
                if (groupIds.contains(groupListResult.respData.get(i).id)) {
                    mGroupList.add(new GroupBean(groupListResult.respData.get(i).id, groupListResult.respData.get(i).name, groupListResult.respData.get(i).description,
                            groupListResult.respData.get(i).totalCount, true));
                } else {
                    mGroupList.add(new GroupBean(groupListResult.respData.get(i).id, groupListResult.respData.get(i).name, groupListResult.respData.get(i).description,
                            groupListResult.respData.get(i).totalCount, false));
                }
            }

            customerExpandAdapter.setChildData(2, mGroupList);
        }
    }

    public void handlerGroupInfoResult(GroupInfoResult result) {
        if (result.statusCode == 200) {
            String s = " 本月剩余发送次数  " + String.valueOf(result.respData.limitNumber) + " 次";
            limitSendAmount.setText(Utils.changeColor(s, ResourceUtils.getColor(R.color.colorMain), 12, s.length() - 1));
            if (Utils.isNotEmpty(result.respData.sendInterval)) {
                String lt = "(两次群发消息之间需要间隔" + result.respData.sendInterval + "小时)";
                totalSendTimeLimit.setText(lt);
            } else {
                totalSendTimeLimit.setText(ResourceUtils.getString(R.string.text_send_group_time_limit));
            }
        }

    }

    public void handleTechsResult(TechListResult result) {
        if (result.statusCode == 200) {
            customerExpandAdapter.setChildData(1, result.respData);
        }
    }

    @OnClick({R.id.btn_next_step, R.id.group_all})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_all:
                currentGroupType = "";
                currentGroupIds = "";
                groupAll.setSelected(true);
                //关闭其他类型分组
                for (int i = 0; i < customerExpandAdapter.getGroupCount(); i++) {
                    customerGroupListView.collapseGroup(i);
                }
                customerExpandAdapter.clearSelectUnlessPosition(-1);
                break;
            case R.id.btn_next_step:
                ((GroupMessageCustomerActivity) getActivity()).gotoCouponFragment();
                break;
        }
    }

    @Override
    public void onItemClicked(Object bean) {
        groupAll.setSelected(false);
    }

    @Override
    public void onItemClicked(int groupPosition, int childPosition) {
        groupAll.setSelected(false);
        customerExpandAdapter.setSelect(groupPosition, childPosition);
    }

    @Override
    public void onCreateGroupButtonClicked() {
        EditGroupActivity.starEditGroupActivity(getActivity(), "");
    }


}
