package com.xmd.manager.window;


import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xmd.manager.R;
import com.xmd.manager.beans.GroupBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.AddGroupResult;
import com.xmd.manager.service.response.DeleteGroupResult;
import com.xmd.manager.service.response.GroupListResult;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;


/**
 * Created by lhj on 2016/12/26.
 */

public class CustomerGroupListActivity extends BaseListActivity<GroupBean, GroupListResult> {
    public static final int ADD_GROUP_RESULT = 0x001;
    @BindView(R.id.group_list_add_btn)
    Button groupListAddBtn;
    @BindView(R.id.group_list_null)
    LinearLayout groupListNull;


    private Subscription groupListSubscription;
    private Subscription mGroupSaveEditSubscription;
    private Subscription mGroupDeleteResultSubscription;

    @Override
    protected void setContentViewLayout() {
        setContentView(R.layout.activity_customer_group_list);
        initView();
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ClUB_GROUP_LIST);
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.group_list));
        setRightVisible(true, ResourceUtils.getString(R.string.new_create), v -> {
            EditGroupActivity.starEditGroupActivity(this, "");
        });
        groupListSubscription = RxBus.getInstance().toObservable(GroupListResult.class).subscribe(
                listResult -> {
                    if (listResult.statusCode == 200) {
                        if (listResult.respData.size() > 0) {
                            groupListNull.setVisibility(View.GONE);
                        } else {
                            groupListNull.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
        mGroupSaveEditSubscription = RxBus.getInstance().toObservable(AddGroupResult.class).subscribe(
                addResult -> {
                    if (addResult.statusCode == 200) {
                        onRefresh();
                    }
                }
        );
        mGroupDeleteResultSubscription = RxBus.getInstance().toObservable(DeleteGroupResult.class).subscribe(
                deleteResult -> {
                    if (deleteResult.statusCode == 200) {
                        onRefresh();
                    }
                }
        );
    }

    @OnClick(R.id.group_list_add_btn)
    public void AddGroup() {
        EditGroupActivity.starEditGroupActivity(this, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == EditGroupActivity.EDIT_GROUP_RESULT_CODE) {

            }
        }
    }

    @Override
    public void onItemClicked(GroupBean bean) {
        EditGroupActivity.starEditGroupActivity(this, bean.id);
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(groupListSubscription, mGroupSaveEditSubscription, mGroupDeleteResultSubscription);
    }
}
