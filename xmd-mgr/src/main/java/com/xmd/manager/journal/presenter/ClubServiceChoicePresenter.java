package com.xmd.manager.journal.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.xmd.manager.beans.ServiceItem;
import com.xmd.manager.beans.ServiceItemInfo;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.UINavigation;
import com.xmd.manager.journal.contract.ClubServiceChoiceContract;
import com.xmd.manager.journal.manager.ClubServiceManager;
import com.xmd.manager.widget.AlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by Administrator on 2016/11/10.
 */
public class ClubServiceChoicePresenter implements ClubServiceChoiceContract.Presenter {

    private ClubServiceManager mProjectManager = ClubServiceManager.getInstance();
    private Context mContext;
    private ClubServiceChoiceContract.View mView;
    private int mMaxSelectedSize;
    private ArrayList<ServiceItem> mSelectedServices;
    private List<ServiceItemInfo> mServiceList;

    private Subscription mSubscription;

    public ClubServiceChoicePresenter(Context context, ClubServiceChoiceContract.View view) {
        mView = view;
        mContext = context;
        mSelectedServices = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        mView.showLoadingAnimation();
        mSubscription = mProjectManager.loadService(new Callback<List<ServiceItemInfo>>() {
            @Override
            public void onResult(Throwable error, List<ServiceItemInfo> result) {
                mSubscription = null;
                mView.hideLoadingAnimation();
                if (error != null) {
                    new AlertDialogBuilder(mContext)
                            .setMessage("加载服务项目失败：" + error.getLocalizedMessage())
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mView.finishSelf();
                                }
                            })
                            .show();
                } else {
                    mServiceList = result;
                    mMaxSelectedSize = mView.getMaxSelectSize();
                    mSelectedServices = mView.getSelectedServiceItem();
                    mView.showServiceList(mServiceList);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onClickServiceItem(ServiceItem item) {
        if (mSelectedServices.contains(item)) {
            mSelectedServices.remove(item);
        } else {
            if (mMaxSelectedSize == 1) {
                mSelectedServices.clear();
            } else {
                if (mSelectedServices.size() >= mMaxSelectedSize) {
                    mView.showToast("不能选择更多项目");
                    return;
                }
            }
            mSelectedServices.add(item);
        }
        mView.setConfirmButtonEnable(mSelectedServices.size() > 0);
        mView.showServiceList(mServiceList);
    }

    @Override
    public void onClickConfirmButton() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(UINavigation.EXTRA_PARCELABLE_SELECTED_SERVICE_ITEMS, mSelectedServices);
        ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
        mView.finishSelf();
    }

    @Override
    public void onClickCancelButton() {
        mView.finishSelf();
    }

    @Override
    public boolean isServiceItemSelected(ServiceItem item) {
        return mSelectedServices.contains(item);
    }
}

