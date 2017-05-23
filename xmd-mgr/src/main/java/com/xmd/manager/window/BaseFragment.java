package com.xmd.manager.window;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import butterknife.ButterKnife;

/**
 * Created by linms@xiaomodo.com on 16-5-17.
 */
public abstract class BaseFragment extends Fragment {

    protected IFragmentCallback mIFragmentCallback;
    protected Bundle mArguments;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof IFragmentCallback) {
            mIFragmentCallback = (IFragmentCallback) getActivity();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mArguments = getArguments();
        ButterKnife.bind(this, getView());
        initView();
    }

    protected abstract void initView();

    /**
     * Interface
     **/
    public interface IFragmentCallback {
        void setFragmentTitle(String title);
    }
}
