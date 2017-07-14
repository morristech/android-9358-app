package com.xmd.technician.window;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.PosterBean;
import com.xmd.technician.common.ImageUploader;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.SaveTechPosterResult;
import com.xmd.technician.http.gson.UploadTechPosterImageResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-6-20.
 */

public class EditTechPosterActivity extends BaseActivity implements BaseFragment.IFragmentCallback {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.fm_poster_edit)
    FrameLayout fmPosterEdit;
    @BindView(R.id.btn_next_step_edit)
    Button btnNextStepEdit;
    @BindView(R.id.btn_before_step_edit)
    Button btnBeforeStepEdit;

    private ArrayList<Fragment> mFragments;
    private FragmentTransaction ft;
    private Subscription mUploadTechPosterImageSubscription;
    private Subscription mTechPosterSaveSubscription;
    private String selectedImageId;
    private PosterBean mPosterBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tech_poster);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackVisible(true);
        mPosterBean = (PosterBean) getIntent().getParcelableExtra(Constant.KEY_CURRENT_POSTER);
        initializationFragment();
        if (Utils.isNotEmpty(mPosterBean.style)) {
            btnBeforeStepEdit.setVisibility(View.GONE);
            selectedImageId = mPosterBean.imageId;
        }
        mUploadTechPosterImageSubscription = RxBus.getInstance().toObservable(UploadTechPosterImageResult.class).subscribe(
                result -> handlerImageUploadResult(result)
        );
        mTechPosterSaveSubscription = RxBus.getInstance().toObservable(SaveTechPosterResult.class).subscribe(
                result -> handlerTechPosterSaveResult(result)
        );

    }

    private void initializationFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mFragments = new ArrayList<>();
        mFragments.add(new TechPosterChoiceModelFragment());
        mFragments.add(new TechPosterEditPosterFragment());
        ft = fragmentManager.beginTransaction();
        for (Fragment fragment : mFragments) {
            ft.add(R.id.fm_poster_edit, fragment, fragment.getClass().getName());
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.KEY_CURRENT_POSTER, (Parcelable) mPosterBean);
        mFragments.get(1).setArguments(bundle);

        if (null != mPosterBean && mPosterBean.id > 0) {
            fragmentManager(false);
        } else {
            fragmentManager(true);
        }
        ft.commit();

    }

    private void handlerTechPosterSaveResult(SaveTechPosterResult result) {
        if (result.statusCode == 200) {
            this.finish();
            makeShortToast("保存成功");
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_POSTER_LIST);
        } else {
            makeShortToast("保存失败" + result.msg);
        }
    }

    private void handlerImageUploadResult(UploadTechPosterImageResult result) {
        hideLoading();
        if (result.statusCode == 200) {
            selectedImageId = result.respData.imageId;
            creditSave();
        } else {
            makeShortToast(result.msg);
        }
    }

    @OnClick({R.id.btn_next_step_edit, R.id.btn_before_step_edit, R.id.btn_save_edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_next_step_edit:
                fragmentManager(false);
                break;
            case R.id.btn_before_step_edit:
                fragmentManager(true);
                break;
            case R.id.btn_save_edit:
                if (null == ((TechPosterEditPosterFragment) mFragments.get(1)).getPosterInfo()) {
                    return;
                }
                if (Utils.isNotEmpty(getImageUrl())) {
                    showLoading("正在上传照片...");
                    ImageUploader.getInstance().uploadByUrl(ImageUploader.TYPE_TECH_POSTER, getImageUrl());
                } else if (Utils.isNotEmpty(selectedImageId)) {
                    creditSave();
                } else {
                    makeShortToast("请先添加照片");
                }
                break;
        }
    }

    private void fragmentManager(boolean isShowChoiceModel) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ftManger = fragmentManager.beginTransaction();
        if (isShowChoiceModel) {
            ftManger.hide(mFragments.get(1));
            ftManger.show(mFragments.get(0));
            ftManger.commit();
            btnNextStepEdit.setVisibility(View.VISIBLE);
            toolbarTitle.setText(ResourceUtils.getString(R.string.tech_poster_choice_poster_model));
        } else {
            ftManger.hide(mFragments.get(0));
            ftManger.show(mFragments.get(1));
            ftManger.commit();
            btnNextStepEdit.setVisibility(View.GONE);
            toolbarTitle.setText(ResourceUtils.getString(R.string.tech_poster_edit_poster));
        }

    }

    public void setSelectedModel(int model) {
        ((TechPosterEditPosterFragment) mFragments.get(1)).setModel((model));
    }

    public String getImageUrl() {
        return ((TechPosterEditPosterFragment) mFragments.get(1)).getImageUrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mUploadTechPosterImageSubscription, mTechPosterSaveSubscription);
    }

    private void creditSave() {
        Map<String, String> requestParam = new HashMap<>();
        requestParam = ((TechPosterEditPosterFragment) mFragments.get(1)).getPosterInfo();
        if (null != requestParam && Utils.isNotEmpty(selectedImageId)) {
            requestParam.put(RequestConstant.KEY_POSTER_IMAGE_ID, selectedImageId);
        } else {
            return;
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_POSTER_SAVE, requestParam);
    }

}
