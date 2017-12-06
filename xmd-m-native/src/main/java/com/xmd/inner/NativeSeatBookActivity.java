package com.xmd.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.widget.ClearableEditText;
import com.xmd.app.widget.DateTimePickDialog;
import com.xmd.inner.bean.SeatInfo;
import com.xmd.inner.event.UpdateRoomEvent;
import com.xmd.inner.event.UpdateSeatEvent;
import com.xmd.inner.httprequest.DataManager;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by zr on 17-12-5.
 */

public class NativeSeatBookActivity extends BaseActivity {
    public static final String EXTRA_SEAT_INFO = "seat_info";

    @BindView(R2.id.tv_book_seat)
    TextView mBookSeatText;
    @BindView(R2.id.tv_book_phone)
    ClearableEditText mBookPhoneEdit;
    @BindView(R2.id.tv_book_date)
    TextView mBookDateText;
    @BindView(R2.id.tv_book_time)
    TextView mBookTimeText;

    private Subscription mSaveBookSeatSubscription;

    private SeatInfo mSeatInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSeatInfo = (SeatInfo) getIntent().getSerializableExtra(EXTRA_SEAT_INFO);
        setContentView(R.layout.activity_native_seat_book);
        ButterKnife.bind(this);
        mBookSeatText.setText(mSeatInfo.name);
        mBookPhoneEdit.setHint("请输入手机号码");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSaveBookSeatSubscription != null) {
            mSaveBookSeatSubscription.unsubscribe();
        }
    }

    @OnClick(R2.id.btn_book_confirm)
    public void onBookConfirm() {
        
    }

    @OnClick(R2.id.tv_book_date)
    public void onDateSelect() {
        DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(this, mBookDateText.getText().toString());
        dataPickDialogStr.dateTimePicKDialog(mBookDateText);
    }

    @OnClick(R2.id.tv_book_time)
    public void onTimeSelect() {
        DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(this, mBookDateText.getText().toString());
        dataPickDialogStr.dateTimePicKDialog(mBookDateText);
    }

    private void saveBook(String roomId, String seatId, String telephone, String appointTime) {
        showLoading();
        mSaveBookSeatSubscription = DataManager.getInstance().saveRoomSeatBook(roomId, seatId, telephone, appointTime, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                hideLoading();
                XToast.show("预订成功");
                EventBus.getDefault().post(new UpdateRoomEvent());
                EventBus.getDefault().post(new UpdateSeatEvent());
                finish();
            }

            @Override
            public void onCallbackError(Throwable e) {
                hideLoading();
                XToast.show(e.getLocalizedMessage());
            }
        });
    }
}
