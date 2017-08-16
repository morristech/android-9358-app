package com.xmd.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.xmd.app.BaseFragment;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.app.widget.DropDownMenuDialog;
import com.xmd.app.widget.RoundImageView;
import com.xmd.black.AddFriendActivity;
import com.xmd.black.BlackListActivity;
import com.xmd.contact.httprequest.ConstantResources;


/**
 * Created by Lhj on 17-7-26.
 */

public class ContactFragment extends BaseFragment {

    private RelativeLayout rlRightMore;

    public static ContactFragment newInstance(String appType) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantResources.INTENT_APP_TYPE, appType);
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private boolean isFromManager;
    private TechContactFragment mTechContactFragment;
    private ManagerContactFragment mManagerContactFragment;
    private View view;
    private RoundImageView mRoundImageHead;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact, container, false);
        isFromManager = getArguments().getString(ConstantResources.INTENT_APP_TYPE).equals(ConstantResources.APP_TYPE_MANAGER) ? true : false;
        rlRightMore = (RelativeLayout) view.findViewById(R.id.rl_toolbar_right);
        mRoundImageHead = (RoundImageView) view.findViewById(R.id.img_toolbar_right_back);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        setTitle("联系人");
        setBackVisible(false);
        setRightVisible(true, R.drawable.contact_icon_more);
        if(!isFromManager){
            Glide.with(getActivity()).load(UserInfoServiceImpl.getInstance().getCurrentUser().getAvatar()).into(mRoundImageHead);
        }
        initFragmentView();
    }

    private void initFragmentView() {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (isFromManager) {
            mManagerContactFragment = new ManagerContactFragment();
            ft.replace(R.id.fm_contact_frame_layout, mManagerContactFragment);
        } else {
            mTechContactFragment = new TechContactFragment();
            ft.replace(R.id.fm_contact_frame_layout, mTechContactFragment);
        }
        ft.commit();
    }

    @Override
    protected void onRightImageClickedListener() {
        final String[] items  = new String[2];
        if(isFromManager){
            items[0] = "黑名单管理";
            items[1] = "分组管理";
        }else{
            items[0] = "添加客户";
            items[1] = "黑名单管理";
        }
        DropDownMenuDialog dialog = DropDownMenuDialog.getDropDownMenuDialog(getActivity(), items, new DropDownMenuDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                switch (index) {
                    case 0:
                        if(isFromManager){
                            BlackListActivity.startBlackListActivity(getActivity(),isFromManager);
                        }else{
                            AddFriendActivity.startAddFriendActivity(getActivity(),isFromManager);
                        }

                        break;
                    case 1:
                        if(isFromManager){
                            //分组管理
                            Intent intent = new Intent();
                            intent.setClassName(getActivity(),"com.xmd.manager.window.CustomerGroupListActivity");
                            startActivity(intent);
                        }else {
                            BlackListActivity.startBlackListActivity(getActivity(),isFromManager);
                        }

                        break;
                    case 2:

                        break;
                }
            }
        });
        dialog.show(rlRightMore);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
