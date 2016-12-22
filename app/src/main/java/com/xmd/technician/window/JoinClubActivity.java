package com.xmd.technician.window;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.KeyEvent;

import com.xmd.technician.R;
import com.xmd.technician.contract.JoinClubContract;
import com.xmd.technician.databinding.ActivityJoinClubBinding;
import com.xmd.technician.presenter.JoinClubPresenter;

public class JoinClubActivity extends BaseActivity implements JoinClubContract.View {
    private JoinClubContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJoinClubBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_join_club);
        mPresenter = new JoinClubPresenter(this, this, binding);
        mPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mPresenter.onClickBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
