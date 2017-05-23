package com.xmd.manager.journal.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.contract.TechnicianChoiceContract;
import com.xmd.manager.journal.manager.TechnicianManager;
import com.xmd.manager.journal.model.Technician;

import java.util.List;

import rx.Subscription;

/**
 * Created by heyangya on 16-10-31.
 */

public class TechnicianChoicePresenter implements TechnicianChoiceContract.Presenter {
    private TechnicianManager mTechnicianManager = TechnicianManager.getInstance();

    private Context mContext;
    private TechnicianChoiceContract.View mView;

    private Subscription loadTechnicians;

    private String mSelectedTechId;
    private int mLastSelectedViewPosition = -1;
    private ImageView mLastSelectedView;

    public TechnicianChoicePresenter(Context context, TechnicianChoiceContract.View view) {
        mView = view;
        mContext = context;
    }

    @Override
    public void onCreate() {
        mView.setOkButtonEnable(false);
        if (loadTechnicians != null) {
            loadTechnicians.unsubscribe();
        }
        mView.showLoadingAnimation();
        loadTechnicians = mTechnicianManager.loadTechnicians(new Callback<List<Technician>>() {
            @Override
            public void onResult(Throwable error, List<Technician> result) {
                mView.hideLoadingAnimation();
                if (error == null) {
                    mView.showTechnicianList(result, mView.getForbiddenTechNoListFromIntent());
                } else {
                    mView.showLoadTechnicianListFailed(error.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        mView.hideLoadingAnimation();
        if (loadTechnicians != null) {
            loadTechnicians.unsubscribe();
        }
    }

    @Override
    public String getSelectedTechId() {
        return mSelectedTechId;
    }

    @Override
    public void onSelectTechnician(Technician technician, int viewPosition, ImageView selectedView) {
        if (!TextUtils.equals(mSelectedTechId, technician.getId())) {
            mSelectedTechId = technician.getId();
            if (mLastSelectedViewPosition != -1) {
                mView.showUnChecked(mLastSelectedViewPosition, mLastSelectedView);
            }
            mLastSelectedViewPosition = viewPosition;
            mLastSelectedView = selectedView;
            mView.setOkButtonEnable(true);
        }
    }

    @Override
    public void onClickOk() {
        mView.finishSelf();
    }
}
