package com.xmd.technician.window;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.xmd.technician.R;

/**
 * Created by sdcm on 16-3-23.
 */
public class BaseFragment extends Fragment {

    private TextView mTitleView;

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

    protected void initTitleView(String title){
        mTitleView = ((TextView)getView().findViewById(R.id.toolbar_title));
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
    }

    /**                         Interface                           **/
    public interface IFragmentCallback {
        //void handleBackPressed();
    }


}
