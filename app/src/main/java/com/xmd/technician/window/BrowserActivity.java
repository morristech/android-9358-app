package com.xmd.technician.window;
import android.os.Bundle;

import com.xmd.technician.R;

import butterknife.ButterKnife;

public class BrowserActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        ButterKnife.bind(this);
    }

}
