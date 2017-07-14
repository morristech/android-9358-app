package com.xmd.manager.window;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.xmd.m.comment.bean.AllGroupListBean;
import com.xmd.m.comment.bean.UserGroupListBean;
import com.xmd.m.comment.event.EditCustomerGroupEvent;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.AddGroupResult;
import com.xmd.manager.service.response.UserGroupSaveResult;
import com.xmd.manager.widget.AddGroupDialog;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.FlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/12/6.
 */

public class AddGroupActivity extends BaseActivity {

    @BindView(R.id.selected_group)
    FlowLayout selectedGroupView;
    @BindView(R.id.all_group)
    FlowLayout allGroupView;

    private Subscription mGroupAddSubscription;
    private Subscription mGroupSaveEditSubscription;
    public static final String CUSTOMER_GROUP = "customer_group";
    private List<String> selectedGroups;
    private List<String> primarySelectedGroups;
    private List<String> allGroups;
    private List<AllGroupListBean> allGroupList;
    private List<UserGroupListBean> userGroupList;
    private String userId;
    private AddGroupDialog dialog;
    private String newAddGroupName;
    private String groupIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        allGroupList = new ArrayList<>();
        userGroupList = new ArrayList<>();
        allGroupList.addAll((Collection<? extends AllGroupListBean>) intent.getSerializableExtra(Constant.KEY_ALL_GROUPS));
        userGroupList.addAll((Collection<? extends UserGroupListBean>) intent.getSerializableExtra(Constant.KEY_USER_GROUPS));
        userId = intent.getStringExtra(Constant.KEY_USER_ID);
        initView();

    }

    private void initView() {
        selectedGroups = new ArrayList<>();
        primarySelectedGroups = new ArrayList<>();
        allGroups = new ArrayList<>();

        for (int i = 0; i < userGroupList.size(); i++) {
            selectedGroups.add(userGroupList.get(i).name);
            primarySelectedGroups.add(userGroupList.get(i).name);
        }
        for (int i = 0; i < allGroupList.size(); i++) {
            allGroups.add(allGroupList.get(i).name);
        }

        setRightVisible(true, ResourceUtils.getString(R.string.btn_save), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupIsChanged()) {
                    saveGroupChanges();
                } else {
                    finish();
                }
            }
        });
        initSelectedGroupView(selectedGroups);
        initALlGroupView(allGroups);
        mGroupAddSubscription = RxBus.getInstance().toObservable(AddGroupResult.class).subscribe(
                addResult -> handlerAddGroupResult(addResult)
        );
        mGroupSaveEditSubscription = RxBus.getInstance().toObservable(UserGroupSaveResult.class).subscribe(
                saveResult -> handlerSaveGroupResult(saveResult)
        );
    }

    private void handlerAddGroupResult(AddGroupResult addResult) {
        if (addResult.statusCode == 200) {
            makeShortToast(addResult.msg);
            selectedGroups.add(newAddGroupName);
            allGroups.add(newAddGroupName);
            allGroupList.add(new AllGroupListBean(addResult.respData, newAddGroupName));
            initSelectedGroupView(selectedGroups);
            initALlGroupView(allGroups);
        } else {
            makeShortToast(addResult.msg);
        }
    }

    private void handlerSaveGroupResult(UserGroupSaveResult saveResult) {
        if (saveResult.statusCode == 200) {
            makeShortToast(saveResult.msg);
            finish();
            EventBus.getDefault().post(new EditCustomerGroupEvent(""));
        } else {
            makeShortToast(saveResult.msg);
        }
    }

    private void saveGroupChanges() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_GROUP_ID, groupIds);
        params.put(RequestConstant.KEY_USER_ID, userId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_USER_ADD_GROUP, params);
    }

    private void initSelectedGroupView(List<String> selected) {
        selectedGroupView.removeAllViews();
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = Utils.dip2px(this, 10);
        lp.bottomMargin = Utils.dip2px(this, 10);

        for (int i = 0; i < selected.size(); i++) {
            TextView view = new TextView(this);
            view.setPadding(10, 10, 10, 10);
            view.setText(selected.get(i));
            view.setTextColor(ResourceUtils.getColor(R.color.number_color));
            view.setBackgroundResource(R.drawable.bg_group_selected);
            selectedGroupView.addView(view, lp);
        }

        TextView addView = new TextView(this);
        addView.setPadding(20, 10, 10, 10);
        addView.setText(ResourceUtils.getString(R.string.all_group));
        addView.setTextColor(ResourceUtils.getColor(R.color.add_group));
        addView.setOnClickListener(v -> {

            dialog = new AddGroupDialog(AddGroupActivity.this);
            dialog.setOnNegativeListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setOnPositiveListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) dialog.getEditText();
                    newAddGroupName = editText.getText().toString();
                    saveAddGroup(newAddGroupName);
                    dialog.dismiss();
                }
            });

            dialog.show();

        });
        selectedGroupView.addView(addView, lp);

    }

    private void saveAddGroup(String groupName) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_GROUP_DESCRIPTION, "");
        params.put(RequestConstant.KEY_GROUP_ID, "");
        params.put(RequestConstant.KEY_GROUP_NAME, groupName);
        params.put(RequestConstant.KEY_GROUP_USER_ID, userId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_GROUP_SAVE, params);
    }

    private void initALlGroupView(List<String> allGroups) {
        allGroupView.removeAllViews();
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = Utils.dip2px(this, 10);
        lp.bottomMargin = Utils.dip2px(this, 10);
        for (int i = 0; i < allGroups.size(); i++) {
            TextView view = new TextView(this);
            view.setPadding(10, 10, 10, 10);
            view.setText(allGroups.get(i));
            if (selectedGroups.contains(allGroups.get(i))) {
                view.setTextColor(ResourceUtils.getColor(R.color.number_color));
                view.setBackgroundResource(R.drawable.bg_group_selected);
            } else {
                view.setTextColor(ResourceUtils.getColor(R.color.order_verification_title));
                view.setBackgroundResource(R.drawable.bg_group_un_selected);
            }
            view.setOnClickListener(v -> {
                if (selectedGroups.contains(view.getText().toString())) {
                    view.setTextColor(ResourceUtils.getColor(R.color.order_verification_title));
                    view.setBackgroundResource(R.drawable.bg_group_un_selected);
                    selectedGroups.remove(view.getText().toString());
                    initSelectedGroupView(selectedGroups);
                } else {
                    selectedGroups.add(view.getText().toString());
                    view.setTextColor(ResourceUtils.getColor(R.color.number_color));
                    view.setBackgroundResource(R.drawable.bg_group_selected);
                    initSelectedGroupView(selectedGroups);
                }
            });
            allGroupView.addView(view, lp);
        }

    }

    @OnClick({R.id.toolbar_left})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_left:
                if (groupIsChanged()) {
                    new AlertDialogBuilder(AddGroupActivity.this).setTitle("温馨提示").setMessage("您编辑了分组尚未进行保存").setCancelable(true).setNegativeButton("离开", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setResult(Activity.RESULT_CANCELED);
                            finish();
                        }
                    }).setPositiveButton("保存", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveGroupChanges();
                        }
                    }).show();
                } else {
                    finish();
                }
                break;


        }

    }

    private boolean groupIsChanged() {
        if (primarySelectedGroups.containsAll(selectedGroups) && selectedGroups.containsAll(primarySelectedGroups)) {
            return false;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < allGroupList.size(); i++) {
            for (int j = 0; j < selectedGroups.size(); j++) {
                if (allGroupList.get(i).name.equals(selectedGroups.get(j))) {
                    buffer.append(allGroupList.get(i).id + ",");
                }
            }
        }
        groupIds = buffer.toString();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGroupAddSubscription, mGroupSaveEditSubscription);
    }
}
