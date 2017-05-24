package com.xmd.app.appointment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xmd.app.Constants;
import com.xmd.app.R;
import com.xmd.app.event.EventTokenExpired;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AppointmentActivity extends AppCompatActivity {

    private AppointmentData mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        EventBus.getDefault().register(this);

        mData = (AppointmentData) getIntent().getSerializableExtra(Constants.EXTRA_DATA);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new Event(Event.CMD_HIDE, mData));
    }

    @Subscribe
    public void onTokenExpired(EventTokenExpired event) {
        finish();
    }
}
