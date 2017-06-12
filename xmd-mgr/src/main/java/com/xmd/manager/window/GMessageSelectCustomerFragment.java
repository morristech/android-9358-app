package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xmd.manager.ClubData;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.CustomerTypeExpandableAdapter;
import com.xmd.manager.beans.GroupBean;
import com.xmd.manager.beans.GroupTagBean;
import com.xmd.manager.beans.GroupTagList;
import com.xmd.manager.beans.Technician;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.GroupInfoResult;
import com.xmd.manager.service.response.GroupListResult;
import com.xmd.manager.service.response.GroupTagListResult;
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

    private CustomerTypeExpandableAdapter customerExpandAdapter;

    private int selectCustomerCount = 0;
    private List<String> checkedIdMap = new ArrayList<>();
    private int checkedGroupPosition = -1;
    private int allCustomerCount = 0;
    private int activeCount = 0;
    private int unactiveCount = 0;
    private List<GroupTagList> couponGroupList = new ArrayList<>();
    private List<List<GroupTagBean>> couponChildList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_select_customer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initView() {
        initCustomerTypeView();
        initData();
        initCustomerCountTextView();
    }

    private void initCustomerCountTextView() {
        if (selectCustomerCount == 0) {
            selectCustomerCountText.setText("请选择客户");
        } else {
            String s = "已选中" + String.valueOf(selectCustomerCount) + "个客户";
            selectCustomerCountText.setText(Utils.changeColor(s, ResourceUtils.getColor(R.color.colorMain), 3, s.length() - 3));
        }
    }

    private void initCustomerTypeView() {
        customerGroupListView.setGroupIndicator(null);
        String[] group = new String[]{
                Constant.USER_GROUP_TYPE_BEHAVIOR,
                Constant.USER_GROUP_TYPE_TECH,
                Constant.USER_GROUP_TYPE_SPECIFIED};

        customerExpandAdapter = new CustomerTypeExpandableAdapter(couponGroupList, couponChildList,CustomerTypeExpandableAdapter.GROUP_TYPE_CUSTOMER_GROUP,this);
        customerGroupListView.setAdapter(customerExpandAdapter);

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
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_GROUP_TAG_LIST);
    }

    public void handlerGroupTagList(GroupTagListResult result) {
        if (result.statusCode == 200) {
            couponChildList.clear();
            couponGroupList.clear();
            for (int i = 0; i < result.respData.size(); i++) {
                if(result.respData.get(i).list != null){
                    couponChildList.add(result.respData.get(i).list);
                    couponGroupList.add(result.respData.get(i));
                }
            }

            customerExpandAdapter.setData(couponGroupList, couponChildList);
        }
    }

    public void handlerGroupInfoResult(GroupInfoResult result) {
        if (result.statusCode == 200) {
            String s = " 本月剩余发送次数  " + String.valueOf(result.respData.limitNumber) + " 次";
            limitSendAmount.setText(Utils.changeColor(s, ResourceUtils.getColor(R.color.colorMain), 10, s.length() - 1));
            if (Utils.isNotEmpty(result.respData.sendInterval)) {
                String lt = "(两次群发消息之间需要间隔" + result.respData.sendInterval + "小时)";
                totalSendTimeLimit.setText(lt);
            } else {
                totalSendTimeLimit.setText(ResourceUtils.getString(R.string.text_send_group_time_limit));
            }
            allCustomerCount = result.respData.allCount;
            activeCount = result.respData.activeCount;
            unactiveCount = result.respData.unactiveCount;
        }

    }

    @OnClick({R.id.btn_next_step, R.id.group_all})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_all:
                groupAll.setSelected(true);
                selectCustomerCount = allCustomerCount;
                initCustomerCountTextView();
                //关闭其他类型分组
                checkedIdMap.clear();
                checkedGroupPosition = -1;
                for (int i = 0; i < customerExpandAdapter.getGroupCount(); i++) {
                    customerGroupListView.collapseGroup(i);
                }
                break;
            case R.id.btn_next_step:
                if (selectCustomerCount == 0) {
                    Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.send_group_message_limit));
                    return;
                }
                ((GroupMessageCustomerActivity) getActivity()).gotoCouponFragment();
                break;
        }
    }

    @Override
    public void onItemClicked(int groupPosition, int childPosition) {
        groupAll.setSelected(false);
        Object bean = customerExpandAdapter.getChild(groupPosition, childPosition);
        int count = 0;
        if(bean instanceof Technician){
            count = ((Technician) bean).count;
        } else if(bean instanceof GroupBean){
            count = ((GroupBean) bean).totalCount;
        } else if(bean instanceof GroupTagBean){
            count = ((GroupTagBean) bean).userCount;
        } else if((groupPosition == 0)&&(childPosition == 0)){
            count = activeCount;
        } else if((groupPosition == 0)&&(childPosition == 1)){
            count = unactiveCount;
        }

        if(groupPosition == checkedGroupPosition){
            if(checkedIdMap.contains(String.valueOf(childPosition))){
                checkedIdMap.remove(String.valueOf(childPosition));
                selectCustomerCount -= count;
            }else {
                checkedIdMap.add(String.valueOf(childPosition));
                selectCustomerCount += count;
            }
        }else {
            checkedIdMap.clear();
            checkedGroupPosition = groupPosition;
            checkedIdMap.add(String.valueOf(childPosition));
            selectCustomerCount = count;
        }

        initCustomerCountTextView();
    }

    @Override
    public void onCreateGroupButtonClicked() {
        EditGroupActivity.starEditGroupActivity(getActivity(), "");
    }

    @Override
    public boolean isChecked(int groupPosition, int childPosition) {
        if((groupPosition == checkedGroupPosition) && checkedIdMap.contains(String.valueOf(childPosition))){
            return true;
        }
        return false;
    }

    public int getSelectCustomerCount() {
        return selectCustomerCount;
    }

    public String getSelectCustomerGroupType(){
        if((checkedGroupPosition == -1) || (checkedIdMap.size() == 0)){
            return "";
        }
        return ((GroupTagList)customerExpandAdapter.getGroup(checkedGroupPosition)).category;
    }

    public String getSelectCustomerGroupIds(){
        if((checkedGroupPosition == -1) || (checkedIdMap.size() == 0)){
            return "";
        }

        StringBuffer buffer = new StringBuffer("");
        for(int i = 0; i < checkedIdMap.size(); i++){
            Object bean = customerExpandAdapter.getChild(checkedGroupPosition, Integer.parseInt(checkedIdMap.get(i)));
            if(bean instanceof Technician){
                buffer.append(((Technician) bean).techId + ",");
            } else if(bean instanceof GroupBean){
                buffer.append(((GroupBean) bean).id + ",");
            } else if(bean instanceof GroupTagBean){
                buffer.append(((GroupTagBean) bean).tagId + ",");
            }
        }
        return buffer.toString().substring(0, buffer.length() - 1);
    }

    public String getSelectCustomerGroupNames(){
        if((checkedGroupPosition == -1) || (checkedIdMap.size() == 0)){
            return ResourceUtils.getString(R.string.all_customer);
        }

        StringBuffer buffer = new StringBuffer("");
        for(int i = 0; i < checkedIdMap.size(); i++){
            Object bean = customerExpandAdapter.getChild(checkedGroupPosition, Integer.parseInt(checkedIdMap.get(i)));
            if(bean instanceof Technician){
                if (Utils.isEmpty(((Technician) bean).techNo)) {
                    buffer.append(((Technician) bean).techName + "/");
                } else {
                    buffer.append(((Technician) bean).techName + "["+((Technician) bean).techNo +"]/");
                }
            } else if(bean instanceof GroupBean){
                buffer.append(((GroupBean) bean).name + "/");
            } else if(bean instanceof GroupTagBean){
                buffer.append(((GroupTagBean) bean).tagName + "/");
            }
        }
        return buffer.toString().substring(0, buffer.length() - 1);
    }
}
