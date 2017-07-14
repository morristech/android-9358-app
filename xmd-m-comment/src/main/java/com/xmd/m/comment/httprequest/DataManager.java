package com.xmd.m.comment.httprequest;


import com.xmd.m.comment.bean.AddToBlacklistResult;
import com.xmd.m.comment.bean.ClubEmployeeDetailResult;
import com.xmd.m.comment.bean.CommentListResult;
import com.xmd.m.comment.bean.CommentStatusResult;
import com.xmd.m.comment.bean.ConsumeListResult;
import com.xmd.m.comment.bean.ContactPermissionResult;
import com.xmd.m.comment.bean.DeleteCustomerResult;
import com.xmd.m.comment.bean.EditCustomerResult;
import com.xmd.m.comment.bean.InBlacklistResult;
import com.xmd.m.comment.bean.ManagerUserDetailResult;
import com.xmd.m.comment.bean.MarkResult;
import com.xmd.m.comment.bean.RemoveFromBlacklistResult;
import com.xmd.m.comment.bean.RewardListResult;
import com.xmd.m.comment.bean.TechConsumeListResult;
import com.xmd.m.comment.bean.TechListResult;
import com.xmd.m.comment.bean.TechRewardListResult;
import com.xmd.m.comment.bean.TechUserDetailResult;
import com.xmd.m.comment.bean.TechVisitorListResult;
import com.xmd.m.comment.bean.UserEditGroupResult;
import com.xmd.m.comment.bean.VisitorListResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.Map;

import rx.Subscription;

/**
 * Created by Lhj on 17-7-1.
 */

public class DataManager {

    private static final DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    public Subscription mLoadCommentList;
    public Subscription mTechList;
    public Subscription mUpdateCommentStatus;
    public Subscription mManagerUserDetail;
    public Subscription mConsumeList;
    public Subscription mRewardList;
    public Subscription mVisitorList;
    public Subscription mTechUserDetail;
    public Subscription mTechConsumeList;
    public Subscription mTechRewardList;
    public Subscription mTechVisitorList;
    public Subscription mClubEmployeeDetail;
    public Subscription mBooleanInBlackList;
    public Subscription mRemoveBlackList;
    public Subscription mAddBlackList;
    public Subscription mContactPermission;
    public Subscription mDeleteCustomer;
    public Subscription mImpression;
    public Subscription mEditOrAddCustomer;
    public Subscription mEditUserGroup;

    //评论列表
    public void loadCommentList(Map<String, String> params, NetworkSubscriber<CommentListResult> listener) {
        mLoadCommentList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getCommentList(params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE), params.get(RequestConstant.KEY_TECH_ID), params.get(RequestConstant.KEY_TYPE), params.get(RequestConstant.KEY_USER_NAME), params.get(RequestConstant.KEY_COMMENT_TYPE)), listener);
    }

    public void loadTechList(NetworkSubscriber<TechListResult> listener) {
        mTechList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getTechList(), listener);
    }

    public void updateCommentStatus(String commentId, String status, NetworkSubscriber<CommentStatusResult> listener) {
        mUpdateCommentStatus = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).updateCommentStatus(commentId, status), listener);
    }

    public void loadManagerUserDetail(String userId, NetworkSubscriber<ManagerUserDetailResult> listener) {
        mManagerUserDetail = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getManagerUserDetail(userId), listener);
    }

    public void loadConsumeList(String page, String pageSize, String userId, NetworkSubscriber<ConsumeListResult> listener) {
        mConsumeList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getConsumeList(page, pageSize, userId), listener);
    }

    public void loadVisitorList(String page, String pageSize, String userId, NetworkSubscriber<VisitorListResult> listener) {
        mVisitorList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getVisitorList(page, pageSize, userId), listener);
    }

    public void loadRewardList(String page, String pageSize, String userId, NetworkSubscriber<RewardListResult> listener) {
        mRewardList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getRewardList(page, pageSize, userId), listener);
    }

    public void loadTechUserDetail(String userId, NetworkSubscriber<TechUserDetailResult> listener) {
        mTechUserDetail = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getTechUserDetail(userId, userId), listener);
    }

    public void loadTechConsumeList(String page, String pageSize, String userId, NetworkSubscriber<TechConsumeListResult> listener) {
        mTechConsumeList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getTechConsumeList(page, pageSize, userId), listener);
    }

    public void loadTechVisitorList(String page, String pageSize, String userId, NetworkSubscriber<TechVisitorListResult> listener) {
        mTechVisitorList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getTechVisitorList(page, pageSize, userId), listener);
    }

    public void loadTechRewardList(String page, String pageSize, String userId, NetworkSubscriber<TechRewardListResult> listener) {
        mTechRewardList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getTechRewardList(page, pageSize, userId), listener);
    }

    public void loadClubEmployeeDetail(String uerId, NetworkSubscriber<ClubEmployeeDetailResult> listener) {
        mClubEmployeeDetail = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).clubEmployeeDetail(uerId), listener);
    }

    public void loadBooleanInBlackList(String friendId, NetworkSubscriber<InBlacklistResult> listener) {
        mBooleanInBlackList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).inBlacklist(friendId), listener);
    }

    public void removeFromBlackList(String friendId, NetworkSubscriber<RemoveFromBlacklistResult> listener) {
        mRemoveBlackList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).removeFromBlacklist(friendId), listener);
    }

    public void addToBlackList(String friendId, NetworkSubscriber<AddToBlacklistResult> listener) {
        mAddBlackList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).addToBlacklist(friendId), listener);
    }

    public void loadContactPermission(String id, NetworkSubscriber<ContactPermissionResult> listener) {
        mContactPermission = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getContactPermission(id, "customer"), listener);
    }

    public void deleteCustomer(String id, NetworkSubscriber<DeleteCustomerResult> listener) {
        mDeleteCustomer = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).doDeleteContact(id), listener);
    }

    public void getImpressionDetail(NetworkSubscriber<MarkResult> listener) {
        mImpression = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getContactMark(), listener);
    }

    public void SaveCustomerRemark(String id, String phoneNum, String remark, String noteName, String imPression, NetworkSubscriber<EditCustomerResult> listener) {
        mEditOrAddCustomer = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).addOrEditCustomer(id, phoneNum, remark, noteName, imPression), listener);
    }

    public void editUserGroup(String userId, NetworkSubscriber<UserEditGroupResult> listener) {
        mEditUserGroup = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).userEditGroup(userId), listener);
    }
}
