package com.xmd.technician.window;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.app.widget.CircleAvatarView;
import com.xmd.app.widget.GlideCircleTransform;
import com.xmd.technician.R;

/**
 * Created by sdcm on 16-3-23.
 */
public class BaseFragment extends Fragment {

    private TextView mTitleView;
    protected CircleAvatarView avatarView;

    protected IFragmentCallback mIFragmentCallback;
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof IFragmentCallback)) {
            throw new RuntimeException("Host Activity Must implement the IFragmentCallback interface");
        } else {
            mIFragmentCallback = (IFragmentCallback) getActivity();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            MobclickAgent.onPageEnd(getClass().getSimpleName());
        } else {
            MobclickAgent.onPageStart(getClass().getSimpleName());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    protected void initTitleView(String title) {
        mTitleView = ((TextView) getView().findViewById(R.id.toolbar_title));
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
    }

    public void showAvatarInTitleBar(String url) {
        avatarView = (CircleAvatarView) getView().findViewById(R.id.avatarView);
        if (!TextUtils.isEmpty(url)) {
            if (avatarView != null) {
                avatarView.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(url).transform(new GlideCircleTransform(getActivity())).error(com.xmd.app.R.drawable.img_default_avatar).into(avatarView);
            }
        } else {
            if (avatarView != null) {
                avatarView.setVisibility(View.VISIBLE);
                avatarView.setUserInfo(UserInfoServiceImpl.getInstance().getCurrentUser());
            }
        }


    }

    /**
     * Interface
     **/
    public interface IFragmentCallback {
        //void handleBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }
}
