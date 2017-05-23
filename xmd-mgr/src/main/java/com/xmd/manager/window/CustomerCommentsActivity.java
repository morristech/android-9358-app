package com.xmd.manager.window;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CommentDetailBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.AppCommentListResult;
import com.xmd.manager.widget.AlertDialogBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-5-25.
 */
public class CustomerCommentsActivity extends BaseListActivity<CommentDetailBean, AppCommentListResult> {

    private String mCustomerId;

    //   private Subscription mCommentDeleteSubscription;

    @Override
    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER_ID, mCustomerId);
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_APP_COMMENT_LIST, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //      RxBus.getInstance().unsubscribe(mCommentDeleteSubscription);
    }

    @Override
    protected void initOtherViews() {
        mCustomerId = getIntent().getStringExtra(Constant.PARAM_CUSTOMER_ID);

        if (Utils.isEmpty(mCustomerId)) {
            finish();
        }

//        mCommentDeleteSubscription = RxBus.getInstance().toObservable(CommentDeleteResult.class).subscribe(
//                result -> {
//                    if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
//                        makeShortToast(result.msg);
//                    } else {
//                        onRefresh();
//                    }
//                }
//        );
    }

    @Override
    public void onNegativeButtonClicked(CommentDetailBean bean) {
        new AlertDialogBuilder(this)
                .setMessage("删除用户的评论?")
                .setPositiveButton(ResourceUtils.getString(R.string.confirm), v -> doDeleteComment(bean))
                .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                .show();
    }

    private void doDeleteComment(CommentDetailBean comment) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DELETE_USE_COMMENT, comment.obj.id);
    }
}
