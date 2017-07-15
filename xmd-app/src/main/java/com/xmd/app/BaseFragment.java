package com.xmd.app;

import android.support.v4.app.Fragment;
import android.widget.TextView;

/**
 * Created by mo on 17-6-21.
 */

public class BaseFragment extends Fragment {

    public void setTitle(String title) {
        TextView textView = (TextView) getView().findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(title);
        }
    }
}
