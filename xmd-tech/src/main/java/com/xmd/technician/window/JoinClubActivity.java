package com.xmd.technician.window;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.xmd.technician.R;
import com.xmd.technician.contract.JoinClubContract;
import com.xmd.technician.databinding.ActivityJoinClubBinding;
import com.xmd.technician.http.gson.RoleListResult;
import com.xmd.technician.presenter.JoinClubPresenter;

import java.util.ArrayList;
import java.util.List;

public class JoinClubActivity extends BaseActivity implements JoinClubContract.View {
    private JoinClubContract.Presenter mPresenter;
    private ActivityJoinClubBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_join_club);
        mPresenter = new JoinClubPresenter(this, this, mBinding);
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

    @Override
    public void showRoleList(List<RoleListResult.Item> roles, int selectPosition) {
        List<String> roleNameList = new ArrayList<>();
        for (RoleListResult.Item item : roles) {
            roleNameList.add(item.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spinner.setAdapter(adapter);
        mBinding.spinner.setSelection(selectPosition);

        mBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.onSelectRole(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
