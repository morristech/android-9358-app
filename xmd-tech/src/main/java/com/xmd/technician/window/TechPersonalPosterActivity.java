package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.technician.Adapter.TechPosterListAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.PosterBean;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.gson.DeleteTechPosterResult;
import com.xmd.technician.http.gson.TechPosterListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;
import com.xmd.technician.widget.RewardConfirmDialog;
import com.xmd.technician.widget.TechPosterDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-6-20.
 */

public class TechPersonalPosterActivity extends BaseActivity implements TechPosterListAdapter.PosterCallBack,TechPosterDialog.PosterShareOrSaveListener {

    @Bind(R.id.ll_tech_poster_empty_view)
    LinearLayout llTechPosterEmptyView;
    @Bind(R.id.tech_poster_recycler_view)
    RecyclerView techPosterRecyclerView;
    @Bind(R.id.toolbar_right)
    TextView toolbarRight;

    private List<PosterBean> mPosterBeanList;
    private TechPosterListAdapter mPosterListAdapter;
    private Subscription mPosterListSubscription;
    private Subscription mPosterDeleterSubscription;
    private PosterBean mPosterBean;
    private TechPosterDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_personal_poster);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackVisible(true);
        toolbarRight.setVisibility(View.VISIBLE);
        toolbarRight.setText(ResourceUtils.getString(R.string.tech_poster_new_add));
        mPosterBeanList = new ArrayList<>();
        mPosterListAdapter = new TechPosterListAdapter(TechPersonalPosterActivity.this, mPosterBeanList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        techPosterRecyclerView.setLayoutManager(layoutManager);
        techPosterRecyclerView.setAdapter(mPosterListAdapter);
        mPosterListAdapter.setPosterCallBackListener(this);
        mPosterListSubscription = RxBus.getInstance().toObservable(TechPosterListResult.class).subscribe(
                result -> handlerTechPosterListResult(result)
        );
        mPosterDeleterSubscription = RxBus.getInstance().toObservable(DeleteTechPosterResult.class).subscribe(
                result -> handlerTechPosterDeleterResult(result)
        );
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_POSTER_LIST);
    }


    private void handlerTechPosterListResult(TechPosterListResult result) {
        if (result.statusCode == 200) {
            if (null == result.respData || result.respData.list.size() == 0) {
                llTechPosterEmptyView.setVisibility(View.VISIBLE);
                return;
            }else{
                llTechPosterEmptyView.setVisibility(View.GONE);
            }

            mPosterBeanList.clear();
            for (PosterBean bean : result.respData.list) {
                bean.qrCodeUrl = result.respData.qrCodeUrl;
                mPosterBeanList.add(bean);
            }
            mPosterListAdapter.setData(mPosterBeanList);
        }
    }

    private void handlerTechPosterDeleterResult(DeleteTechPosterResult result) {
        if (result.statusCode == 200) {
            makeShortToast("删除成功");
        } else {
            makeShortToast("删除失败");
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_POSTER_LIST);
    }

    @OnClick({R.id.toolbar_right, R.id.btn_create_poster})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_right:
                createOrEditTechPoster(true);
                break;
            case R.id.btn_create_poster:
                createOrEditTechPoster(true);
                break;
        }
    }

    private void createOrEditTechPoster(boolean isCreate) {
        Intent createPosterIntent = new Intent(this, EditTechPosterActivity.class);
        if (isCreate) {
            mPosterBean = new PosterBean();
        }
        createPosterIntent.putExtra(Constant.KEY_CURRENT_POSTER, mPosterBean);
        startActivity(createPosterIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mPosterListSubscription, mPosterDeleterSubscription);
    }

    @Override
    public void itemClicked(PosterBean bean) {
        mPosterBean = bean;

        int mCurrentModel = Constant.TECH_POSTER_FLOWER_MODEL;
        switch (bean.style) {
            case Constant.TECH_POSTER_TYPE_SQUARE:
                mCurrentModel = Constant.TECH_POSTER_SQUARE_MODEL;
                break;
            case Constant.TECH_POSTER_TYPE_CIRCULAR:
                mCurrentModel = Constant.TECH_POSTER_CIRCULAR_MODEL;
                break;
            case Constant.TECH_POSTER_TYPE_FLOWER:
                mCurrentModel = Constant.TECH_POSTER_FLOWER_MODEL;
                break;
        }
        mDialog = new TechPosterDialog(this, mCurrentModel, false);
        mDialog.show();
        mDialog.setViewDate(bean.title, bean.subTitle, bean.name, bean.techNo, bean.clubName, "", bean.imageUrl);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setPosterListener(this);

    }

    @Override
    public void deleteClicked(PosterBean bean) {
        new RewardConfirmDialog(TechPersonalPosterActivity.this, getString(R.string.tech_poster_alter_message), getString(R.string.tech_poster_alter_delete_message), "",true) {
            @Override
            public void onConfirmClick() {
                super.onConfirmClick();
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_POSTER_DELETE, bean.id);
            }
        }.show();
    }

    @Override
    public void editClicked(PosterBean bean) {
        mPosterBean = bean;
        createOrEditTechPoster(false);
    }

    @Override
    public void shareClicked(PosterBean bean) {
        mPosterBean = bean;
        posterShare();
    }

    @Override
    public void posterSave(View view) {

    }

    @Override
    public void posterEdit() {
        createOrEditTechPoster(false);
    }

    @Override
    public void posterShare() {
        StringBuilder url;
        if (Utils.isEmpty(mPosterBean.shareUrl)) {
            url = new StringBuilder(SharedPreferenceHelper.getServerHost());
            url.append(String.format("/spa-manager/tech-poster/#/%s?id=%s", mPosterBean.style, mPosterBean.id));
        } else {
            url = new StringBuilder(mPosterBean.shareUrl);
        }
        ShareController.doShare(mPosterBean.imageUrl, url.toString(), mPosterBean.title, mPosterBean.subTitle, Constant.SHARE_TYPE_TECH_POSTER, "");
    }




}
