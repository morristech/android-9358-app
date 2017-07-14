package com.xmd.manager.window;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.manager.R;
import com.xmd.manager.adapter.GroupMemberAdapter;
import com.xmd.manager.beans.GroupMemberBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.AddGroupResult;
import com.xmd.manager.service.response.DeleteGroupResult;
import com.xmd.manager.service.response.UserGroupDetailListResult;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.ClearableEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/12/5.
 */

public class EditGroupActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.group_name_edit)
    ClearableEditText groupNameEdit;
    @BindView(R.id.group_remark_edit)
    ClearableEditText groupRemarkEdit;
    @BindView(R.id.group_member_recycler_view)
    RecyclerView groupMemberRecyclerView;
    @BindView(R.id.btn_delete_group)
    Button btnDeleteGroup;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_right)
    FrameLayout mToolbarRight;
    @BindView(R.id.toolbar_right_text)
    TextView toolbarRightText;
    @BindView(R.id.toolbar_left)
    ImageView toolbarLeft;
    @BindView(R.id.smooth_scroll_view)
    ScrollView mSmoothScrollView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    public static final int SELECT_GROUP_RESULT = 0x001;
    public static final int EDIT_GROUP_RESULT_CODE = 0x002;
    private String mGroupId;
    private GroupMemberAdapter mAdapter;
    private Subscription mGroupDetailSubscription;
    private Subscription mGroupSaveEditSubscription;
    private Subscription mGroupDeleteResultSubscription;
    private List<GroupMemberBean> mMembers;
    private String userIds;
    private String initUserIds;
    private String mGroupName = "";
    private String mGroupDescription = "";


    public static void starEditGroupActivity(Activity activity, String groupId) {
        Intent intent = new Intent(activity, EditGroupActivity.class);
        intent.putExtra(RequestConstant.KEY_GROUP_ID, groupId);
        activity.startActivityForResult(intent, EDIT_GROUP_RESULT_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        ButterKnife.bind(this);
        mGroupId = getIntent().getStringExtra(RequestConstant.KEY_GROUP_ID);
        initView();

    }

    private void initView() {
        userIds = "";
        if (Utils.isEmpty(mGroupId)) {
            btnDeleteGroup.setVisibility(View.GONE);
            toolbarTitle.setText(R.string.create_group);
            initUserIds = "";
            mSmoothScrollView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } else {

            toolbarTitle.setText(R.string.edit_group);
        }
        toolbarLeft.setOnClickListener(leftClick);

        Utils.hideKeyboard(this);
        toolbarTitle.setVisibility(View.VISIBLE);
        mToolbarRight.setVisibility(View.VISIBLE);
        toolbarRightText.setText(R.string.btn_save);
        toolbarRightText.setVisibility(View.VISIBLE);
        toolbarRightText.setOnClickListener(this);
        mMembers = new ArrayList<>();
        groupMemberRecyclerView.setHasFixedSize(true);
        groupMemberRecyclerView.setNestedScrollingEnabled(true);
        groupMemberRecyclerView.setLayoutManager(new GridLayoutManager(EditGroupActivity.this, 4));
        groupMemberRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new GroupMemberAdapter(this, new GroupMemberAdapter.OnItemClickListener() {
            @Override
            public void onAddMember() {
                Intent intent = new Intent(EditGroupActivity.this, SelectGroupCustomerActivity.class);
                intent.putExtra(SelectGroupCustomerActivity.HAD_SELECTED_CUSTOMER, getUserIds());
                startActivityForResult(intent, SELECT_GROUP_RESULT);
            }

            @Override
            public void onDeleteMember() {
                mAdapter.refreshDataDeleterView(true);
                toolbarRightText.setText(R.string.cancel);
            }

            @Override
            public void onDeleteItem(int position) {
                mAdapter.deleteItemRefresh(mMembers.get(position));
                mMembers.remove(mMembers.get(position));
            }

            @Override
            public void onItemDetail(GroupMemberBean bean) {
//                Intent intent = new Intent(EditGroupActivity.this, CustomerActivity.class);
//                intent.putExtra(RequestConstant.COMMENT_USER_ID, bean.id);
//                startActivity(intent);
                CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(EditGroupActivity.this, bean.id, ConstantResources.INTENT_TYPE_MANAGER, false);
            }
        });
        mGroupSaveEditSubscription = RxBus.getInstance().toObservable(AddGroupResult.class).subscribe(
                addResult -> {
                    if (addResult.statusCode == 200) {
                        makeShortToast(addResult.msg);
                        this.finish();
                    } else {
                        makeShortToast(addResult.msg);
                    }
                }
        );
        mGroupDeleteResultSubscription = RxBus.getInstance().toObservable(DeleteGroupResult.class).subscribe(
                deleteResult -> {
                    if (deleteResult.statusCode == 200) {
                        makeShortToast(deleteResult.msg);
                        this.finish();
                    } else {
                        makeShortToast(deleteResult.msg);
                    }
                }
        );
        if (Utils.isNotEmpty(mGroupId)) {
            initEditGroupView();
        } else {
            initAddGroupView();
        }

    }

    private void initEditGroupView() {
        mGroupDetailSubscription = RxBus.getInstance().toObservable(UserGroupDetailListResult.class).subscribe(
                userGroupDetailListResult -> handlerGroupDetailList(userGroupDetailListResult)
        );
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_GROUP_DETAILS, mGroupId);
        btnDeleteGroup.setText(ResourceUtils.getString(R.string.delete_group));
    }

    private void initAddGroupView() {
        mMembers.clear();
        groupMemberRecyclerView.setAdapter(mAdapter);
        mAdapter.refreshDataSet(mMembers);
    }

    private void handlerGroupDetailList(UserGroupDetailListResult result) {
        if (Utils.isNotEmpty(result.respData.group.name)) {
            groupNameEdit.setText(result.respData.group.name);
            mGroupName = result.respData.group.name;
        }
        if (Utils.isNotEmpty(result.respData.group.description)) {
            groupRemarkEdit.setText(result.respData.group.description);
            mGroupDescription = result.respData.group.description;
        }
        mMembers.clear();
        mMembers.addAll(result.respData.details);
        groupMemberRecyclerView.setAdapter(mAdapter);
        mAdapter.refreshDataSet(mMembers);
        initUserIds = getUserIds();
        mSmoothScrollView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_delete_group)
    public void onDeleteGroupClick() {
        new AlertDialogBuilder(EditGroupActivity.this).setTitle("温馨提示").setMessage("确认删除该分组").setCancelable(true).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).setPositiveButton("删除", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGroup();
            }
        }).show();

    }

    @Override
    public void onClick(View v) {
        if (toolbarRightText.getText().toString().equals(ResourceUtils.getString(R.string.btn_save))) {
            saveGroup();
        } else {
            toolbarRightText.setText(ResourceUtils.getString(R.string.btn_save));
            mAdapter.refreshDataDeleterView(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FIRST_USER) {
            if (data == null || "".equals(data)) {
                return;
            } else {
                mMembers.addAll((ArrayList<GroupMemberBean>) data.getSerializableExtra(SelectGroupCustomerActivity.SELECT_CUSTOMER_RESULT));
                mAdapter.refreshDataSet(mMembers);

            }
        }
    }

    private void saveGroup() {
        String groupName = groupNameEdit.getText().toString();
        if (Utils.isEmpty(groupName)) {
            makeShortToast(ResourceUtils.getString(R.string.group_edit_name_null));
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_GROUP_DESCRIPTION, groupRemarkEdit.getText().toString());
        params.put(RequestConstant.KEY_GROUP_ID, mGroupId);
        params.put(RequestConstant.KEY_GROUP_NAME, groupName);
        params.put(RequestConstant.KEY_GROUP_USER_ID, getUserIds());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_GROUP_SAVE, params);
    }

    private void deleteGroup() {
        if (Utils.isNotEmpty(mGroupId)) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_GROUP_DELETE, mGroupId);
        }
    }

    private String getUserIds() {
        userIds = "";
        if (mMembers.size() == 0) {
            userIds = "";
        } else if (mMembers.size() == 1) {
            userIds = mMembers.get(0).id;
        } else if (mMembers.size() > 1) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < mMembers.size(); i++) {
                buffer.append(mMembers.get(i).id + ",");
            }
            userIds = buffer.toString();

        }
        return userIds;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mGroupDetailSubscription) {
            RxBus.getInstance().unsubscribe(mGroupDetailSubscription, mGroupSaveEditSubscription, mGroupDeleteResultSubscription);
        } else {
            RxBus.getInstance().unsubscribe(mGroupSaveEditSubscription, mGroupDeleteResultSubscription);
        }
    }

    View.OnClickListener leftClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!getUserIds().equals(initUserIds) || !mGroupName.equals(groupNameEdit.getText().toString()) || !mGroupDescription.equals(groupRemarkEdit.getText().toString())) {
                new AlertDialogBuilder(EditGroupActivity.this).setTitle("温馨提示").setMessage("您编辑了分组尚未保存").setCancelable(true).setNegativeButton("离开", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).setPositiveButton("保存", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveGroup();
                    }
                }).show();
            } else {
                finish();
            }

        }
    };
}
