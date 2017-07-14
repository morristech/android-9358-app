package com.xmd.manager.window;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.image_tool.ImageTool;
import com.xmd.manager.R;
import com.xmd.manager.adapter.CouponListAdapter;
import com.xmd.manager.adapter.GroupListAdapter;
import com.xmd.manager.beans.FavourableActivityBean;
import com.xmd.manager.beans.GroupBean;
import com.xmd.manager.common.FileSizeUtil;
import com.xmd.manager.common.ImageLoader;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.AddGroupResult;
import com.xmd.manager.service.response.AlbumUploadResult;
import com.xmd.manager.service.response.FavourableActivityListResult;
import com.xmd.manager.service.response.GroupInfoResult;
import com.xmd.manager.service.response.GroupListResult;
import com.xmd.manager.service.response.SendGroupMessageResult;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.CircularBeadImageView;
import com.xmd.manager.widget.LoadingDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/9/26.
 */
public class SendGroupMessageActivity extends BaseActivity implements TextWatcher {
    public static final int REQUEST_CODE_LOCAL_PICTURE = 1;

    @BindView(R.id.selected_coupon)
    TextView mSelectedCoupon;
    @BindView(R.id.btn_select_coupon)
    ImageView btnSelectCoupon;
    @BindView(R.id.btn_reset)
    Button btnReset;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.edit_content)
    EditText mEditContent;
    @BindView(R.id.editable_amount)
    TextView mEditAbleAmount;
    @BindView(R.id.total_send_amount)
    TextView limitSendAmount;
    @BindView(R.id.group_all)
    TextView groupAll;
    @BindView(R.id.group_active)
    TextView groupActive;
    @BindView(R.id.group_valid)
    TextView groupValid;
    @BindView(R.id.rl_select_coupon_btn)
    RelativeLayout rlSelectCouponBtn;
    @BindView(R.id.total_send_time_limit)
    TextView totalSendTimeLimit;
    @BindView(R.id.group_add_pic)
    CircularBeadImageView groupAddPic;
    @BindView(R.id.group_list)
    RecyclerView groupListView;
    @BindView(R.id.limit_image_size)
    TextView limitImageSize;
    @BindView(R.id.image_delete)
    ImageView imageDelete;
    @BindView(R.id.image_groups)
    ImageView imageGroups;
    @BindView(R.id.rl_groups)
    RelativeLayout rlGroups;

    private ImageTool mImageTool = new ImageTool();

    private ListView mListView;
    private List<FavourableActivityBean> mList = new ArrayList<>();
    private List<GroupBean> mGroupList = new ArrayList<>();
    private List<GroupBean> mInitialGroupList = new ArrayList<>();

    private CouponListAdapter mAdapter;
    private PopupWindow mPopupWindow;
    private int popupWindowHeight = 300;


    private int mLimitAmount;
    private int mLimitImageSize;

    private String currentGroupType;
    private String selectedCouponActId = "-1";
    private String selectedCouponName;
    private String mCouponContent;
    private String mMessageContent;
    private String mCurrentMessageType;
    private String mLimitTime;

    private Subscription mGetGroupMessageSubscription;
    private Subscription mGetGroupInfoSubscription;
    private Subscription mSendGroupMessageResultSubscription;
    private Subscription mGroupListSubscription;
    private Subscription mGroupMessageAlbumUpload;
    private Subscription mGroupSaveEditSubscription;
    private boolean isScrollToBottom;


    private String imageUrl;
    private String imageId;
    private GroupListAdapter mGroupAdapter;
    private List<String> groupIds;
    private String selectGroupIds;
    private LoadingDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_group_message);
        setRightVisible(true, ResourceUtils.getString(R.string.send_group_detail), view -> startActivity(new Intent(this, SendCouponDetailActivity.class)));
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        groupIds = new ArrayList<>();
        mEditContent.addTextChangedListener(this);
        groupActive.setSelected(true);
        initData();
        initRecyclerView();
    }

    private void initListView() {
        mListView = new ListView(this);
        mListView.setBackgroundResource(R.drawable.listview_background);
        mAdapter = new CouponListAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedCoupon.setText(mList.get(position).name);
                selectedCouponName = mList.get(position).name;
                selectedCouponActId = mList.get(position).actId;
                mCouponContent = mList.get(position).msg;
                mCurrentMessageType = mList.get(position).msgType;
                mPopupWindow.dismiss();
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SendGroupMessageActivity.this);
        groupListView.setHasFixedSize(true);
        groupListView.setNestedScrollingEnabled(true);
        groupListView.setLayoutManager(linearLayoutManager);
        groupListView.setItemAnimator(new DefaultItemAnimator());
        mGroupAdapter = new GroupListAdapter(this);
        mGroupAdapter.setItemClickedListener(
                new GroupListAdapter.OnItemClickedListener() {
                    @Override
                    public void onGroupItemClicked(GroupBean ben, int position) {
                        if (position == mGroupList.size()) {
                            EditGroupActivity.starEditGroupActivity(SendGroupMessageActivity.this, "");
                        } else {
                            if (groupIds.contains(ben.id)) {
                                groupIds.remove(ben.id);
                                mGroupList.set(position, new GroupBean(ben.id, ben.name, ben.description, ben.totalCount, false));
                            } else {
                                groupIds.add(ben.id);
                                mGroupList.set(position, new GroupBean(ben.id, ben.name, ben.description, ben.totalCount, true));
                                selectCustomer(null);
                            }
                            mGroupAdapter.notifyItemChanged(position);
                        }

                    }

                });
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ClUB_GROUP_LIST);
    }

    private void showCouponList() {
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mListView, mSelectedCoupon.getWidth(), popupWindowHeight);
        }
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAsDropDown(mSelectedCoupon, 0, 0);
    }

    private void initData() {
        currentGroupType = RequestConstant.VALUE_TYPE_ACTIVE;

        mGetGroupInfoSubscription = RxBus.getInstance().toObservable(GroupInfoResult.class).subscribe(
                groupInfoResult -> handlerGroupInfoResult(groupInfoResult));

        mGetGroupMessageSubscription = RxBus.getInstance().toObservable(FavourableActivityListResult.class).subscribe(
                activityResult -> handleFavourableActivityResult(activityResult));

        mSendGroupMessageResultSubscription = RxBus.getInstance().toObservable(SendGroupMessageResult.class).subscribe(
                sendResult -> handlerSendGroupMessageResult(sendResult));

        mGroupListSubscription = RxBus.getInstance().toObservable(GroupListResult.class).subscribe(
                groupListResult -> handlerGroupList(groupListResult));

        mGroupMessageAlbumUpload = RxBus.getInstance().toObservable(AlbumUploadResult.class).subscribe(
                uploadResult -> handleAlbumUploadResult(uploadResult)
        );
        mGroupSaveEditSubscription = RxBus.getInstance().toObservable(AddGroupResult.class).subscribe(
                addResult -> {
                    if (addResult.statusCode == 200) {
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ClUB_GROUP_LIST);
                        isScrollToBottom = true;
                    }
                }
        );

        initListView();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_GROUP_INFO);
        getFavourableActivityList();

    }


    private void handlerGroupList(GroupListResult groupListResult) {
        if (groupListResult.statusCode == 200) {
            mInitialGroupList.clear();
            mGroupList.clear();
            mInitialGroupList.addAll(groupListResult.respData);
            for (int i = 0; i < groupListResult.respData.size(); i++) {
                if (groupIds.contains(groupListResult.respData.get(i).id)) {
                    mGroupList.add(new GroupBean(groupListResult.respData.get(i).id, groupListResult.respData.get(i).name, groupListResult.respData.get(i).description,
                            groupListResult.respData.get(i).totalCount, true));
                } else {
                    mGroupList.add(new GroupBean(groupListResult.respData.get(i).id, groupListResult.respData.get(i).name, groupListResult.respData.get(i).description,
                            groupListResult.respData.get(i).totalCount, false));
                }

            }
            mGroupAdapter.setGroupData(mGroupList);
            groupListView.setAdapter(mGroupAdapter);
            if (isScrollToBottom) {
                groupListView.smoothScrollToPosition(mGroupList.size());
            }

        }
    }

    private void handleFavourableActivityResult(FavourableActivityListResult result) {
        if (result.statusCode == 200) {
            //mList.addAll(result.respData);
            mList.add(0, new FavourableActivityBean(ResourceUtils.getString(R.string.no_data), "-1"));
        }
    }

    private void handlerSendGroupMessageResult(SendGroupMessageResult sendResult) {

        if (sendResult.statusCode == 200) {
            makeShortToast(ResourceUtils.getString(R.string.send_group_message_success));
            this.finish();
        } else {
            makeShortToast(sendResult.msg);
        }

    }

    private void handlerGroupInfoResult(GroupInfoResult result) {
        if (result.statusCode == 200) {
            mLimitAmount = result.respData.limitNumber;
            mLimitTime = result.respData.sendInterval;
            mLimitImageSize = result.respData.imageSize;
            String s = "*   本月剩余发送次数  " + String.valueOf(mLimitAmount) + " 次";
            limitSendAmount.setText(Utils.changeColor(s, ResourceUtils.getColor(R.color.colorMain), 12, s.length() - 1));
            if (Utils.isNotEmpty(mLimitTime)) {
                String lt = "(两次群发消息之间需要间隔" + mLimitTime + "小时)";
                totalSendTimeLimit.setText(lt);
            } else {
                totalSendTimeLimit.setText(ResourceUtils.getString(R.string.text_send_group_time_limit));
            }
            limitImageSize.setText(String.format("上传图片大小需小于%sM", String.valueOf(mLimitImageSize)));

        }

    }

    private void handleAlbumUploadResult(AlbumUploadResult uploadResult) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (uploadResult.statusCode == 200) {
            imageId = uploadResult.respData.imageId;
            sendGroupMessage();
        }
    }


    @OnClick({R.id.group_all, R.id.group_active, R.id.group_valid, R.id.rl_select_coupon, R.id.btn_send, R.id.btn_reset, R.id.group_add_pic, R.id.image_delete, R.id.rl_groups})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_select_coupon:
                showCouponList();
                break;
            case R.id.btn_send:
                if (Utils.isEmpty(mMessageContent) && selectedCouponActId.equals("-1") && Utils.isEmpty(imageUrl)) {
                    Utils.makeShortToast(this, ResourceUtils.getString(R.string.send_group_message_alert));
                    return;
                } else if (selectedCouponActId.equals("-1")) {
                    mCouponContent = "";
                } else if (Utils.isEmpty(imageUrl)) {
                    imageUrl = "";
                }
                selectGroupIds = Utils.listToString(groupIds);

                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_GROUP_INFO);
                ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                    @Override
                    public void run() {
                        if (mLimitAmount > 0) {
                            if (Utils.isEmpty(imageUrl)) {
                                sendGroupMessage();
                            } else {
                                try {

                                    if (FileSizeUtil.getFileOrFilesSize(imageUrl, FileSizeUtil.SIZE_TYPE_KB) > 1024 * mLimitImageSize) {
                                        new AlertDialogBuilder(SendGroupMessageActivity.this).setTitle("温馨提示").setMessage("图片大小超过" + mLimitImageSize + "M,继续发送将压缩该图片发送。是否确认发送？").setCancelable(true).setNegativeButton("取消", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        }).setPositiveButton("发送", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mDialog = new LoadingDialog(SendGroupMessageActivity.this);
                                                mDialog.show("正在上传图片");
                                                try {
                                                    uploadImage(imageUrl);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    makeShortToast("图片上传失败，请重新选择");
                                                    mDialog.dismiss();
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).show();
                                    } else {
                                        mDialog = new LoadingDialog(SendGroupMessageActivity.this);
                                        mDialog.show("正在上传图片");
                                        uploadImage(imageUrl);
                                    }

                                } catch (IOException e) {
                                    makeShortToast("图片上传失败，请重新选择");
                                    mDialog.dismiss();
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            Utils.makeShortToast(SendGroupMessageActivity.this, ResourceUtils.getString(R.string.send_group_message_alert_enough));
                        }
                    }
                }, 1000);

                break;
            case R.id.btn_reset:
                restView();
                break;
            case R.id.group_all:
                selectCustomer(v);
                break;
            case R.id.group_active:
                selectCustomer(v);
                break;
            case R.id.group_valid:
                selectCustomer(v);
                break;
            case R.id.group_add_pic:
                mImageTool.onlyPick(true).start(this, new ImageTool.ResultListener() {
                    @Override
                    public void onResult(String s, Uri uri, Bitmap bitmap) {
                        if (s == null && uri != null) {
                            imageUrl = uri.getPath();
                            Glide.with(SendGroupMessageActivity.this).load(imageUrl).into(groupAddPic);
                            imageDelete.setVisibility(View.VISIBLE);
                        }
                    }
                });
                break;
            case R.id.image_delete:
                Glide.with(SendGroupMessageActivity.this).load(R.drawable.img_group_add_img).into(groupAddPic);
                imageDelete.setVisibility(View.GONE);
                imageId = "";
                break;
            case R.id.rl_groups:
                if (groupListView.getVisibility() == View.VISIBLE) {
                    groupListView.setVisibility(View.GONE);
                    imageGroups.setBackgroundResource(R.drawable.icon_down);
                } else {
                    groupListView.setVisibility(View.VISIBLE);
                    imageGroups.setBackgroundResource(R.drawable.icon_up);
                }
                break;
        }
    }

    private void selectCustomer(View v) {
        if (null != v) {
            switch (v.getId()) {
                case R.id.group_all:
                    currentGroupType = "";
                    groupAll.setSelected(true);
                    groupActive.setSelected(false);
                    groupValid.setSelected(false);
                    break;
                case R.id.group_active:
                    currentGroupType = RequestConstant.VALUE_TYPE_ACTIVE;
                    groupAll.setSelected(false);
                    groupActive.setSelected(true);
                    groupValid.setSelected(false);
                    break;
                case R.id.group_valid:
                    currentGroupType = RequestConstant.VALUE_TYPE_UNATIVE;
                    groupAll.setSelected(false);
                    groupActive.setSelected(false);
                    groupValid.setSelected(true);
                    break;
            }
            groupIds.clear();
            mGroupList.clear();
            mGroupList.addAll(mInitialGroupList);
            mGroupAdapter.setGroupData(mGroupList);
            groupListView.scrollToPosition(0);
        } else {
            currentGroupType = RequestConstant.VALUE_TYPE_SPECIFIED;
            groupAll.setSelected(false);
            groupActive.setSelected(false);
            groupValid.setSelected(false);
        }
    }

    private void getFavourableActivityList() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, "");
        params.put(RequestConstant.KEY_PAGE_SIZE, "100");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ClUB_FAVOURABLE_ACTIVITY, params);
    }

    private void restView() {
        selectCustomer(groupActive);
        currentGroupType = RequestConstant.GROUP_ACTIVE_TYPE;
        mEditContent.setText("");
        selectedCouponActId = "-1";
        selectedCouponName = "";
        mSelectedCoupon.setText(ResourceUtils.getString(R.string.no_data));
        if (imageDelete.getVisibility() == View.VISIBLE) {
            Glide.with(SendGroupMessageActivity.this).load(R.drawable.img_group_add_img).into(groupAddPic);
            imageDelete.setVisibility(View.GONE);
            imageId = "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetGroupMessageSubscription, mGetGroupInfoSubscription, mSendGroupMessageResultSubscription,
                mGroupListSubscription, mGroupMessageAlbumUpload, mGroupSaveEditSubscription);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mEditContent.getText().toString().length() >= 100) {
            mEditContent.setSelected(false);
            mEditAbleAmount.setText("0");

        } else {
            if ((100 - s.length()) >= 0) {
                mEditAbleAmount.setText(100 - s.length() + "");
            }
            if (Utils.isNotEmpty(mEditContent.getText().toString())) {
                mEditContent.setSelected(true);
            }
        }
        mMessageContent = mEditContent.getText().toString();
    }


    private void sendGroupMessage() {
        if (selectedCouponActId.equals("-1")) {
            selectedCouponActId = "-1";
            selectedCouponName = "";
            mCouponContent = "";
        }
        if (groupIds.size() > 0) {
            currentGroupType = RequestConstant.VALUE_TYPE_SPECIFIED;
        }
        if (Utils.isEmpty(imageId)) {
            imageId = "";
        }

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_GROUP_ACT_ID, selectedCouponActId);
        params.put(RequestConstant.KEY_GROUP_ACT_NAME, selectedCouponName);
        params.put(RequestConstant.KEY_GROUP_COUPON_CONTENT, mCouponContent);
        params.put(RequestConstant.KEY_GROUP_IDS, selectGroupIds);
        params.put(RequestConstant.KEY_GROUP_IMAGE_ID, imageId);
        params.put(RequestConstant.KEY_GROUP_MESSAGE_CONTENT, mMessageContent);
        params.put(RequestConstant.KEY_GROUP_USER_GROUP_TYPE, currentGroupType);
        params.put(RequestConstant.KEY_GROUP_MESSAGE_TYEP, mCurrentMessageType);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GROUP_MESSAGE_SEND, params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageTool.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(String imageUrl) throws IOException {
        String base = ImageLoader.encodeFileToBase64(imageUrl);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_GROUP_MESSAGE_ALBUM_UPLOAD, base);
    }
}
