package com.xmd.technician.window;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by sdcm on 16-3-23.
 */
public class BaseFragment extends Fragment {

    protected IFragmentCallback mIFragmentCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (! (getActivity() instanceof IFragmentCallback)) {
            throw new RuntimeException("Host Activity Must implement the IFragmentCallback interface");
        } else {
            mIFragmentCallback = (IFragmentCallback) getActivity();
        }

    }

    /**                         Interface                           **/

    public interface IFragmentCallback {
        void handleBackPressed();
    }


}
