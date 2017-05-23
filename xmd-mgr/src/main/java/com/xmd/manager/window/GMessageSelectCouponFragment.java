package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.manager.R;
import com.xmd.manager.beans.FavourableActivityBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.FavourableActivityListResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sdcm on 17-5-22.
 */
public class GMessageSelectCouponFragment extends BaseFragment {

    private List<FavourableActivityBean> mList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_select_coupon, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {

    }

    @OnClick({R.id.btn_next_step, R.id.btn_previous_step})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous_step:
                ((GroupMessageCustomerActivity) getActivity()).gotoCustomerFragment();
                break;
            case R.id.btn_next_step:
                ((GroupMessageCustomerActivity) getActivity()).gotoEditContentFragment();
                break;
        }
    }

    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, "");
        params.put(RequestConstant.KEY_PAGE_SIZE, "100");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ClUB_FAVOURABLE_ACTIVITY, params);
    }

    public void handleFavourableActivityResult(FavourableActivityListResult result) {
        if (result.statusCode == 200) {
            mList.addAll(result.respData);
            mList.add(0, new FavourableActivityBean(ResourceUtils.getString(R.string.no_data), "-1"));
            //onGetListSucceeded(result.pageCount, result.respData);
        } else {
            //onGetListFailed(result.msg);
        }
    }
}
