package com.xmd.technician.window;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xmd.technician.R;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_AUTO_CHECK_UPGRADE);
    }
}
