package com.xmd.inner;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.utils.DateUtil;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.widget.ClearableEditText;
import com.xmd.inner.bean.SeatInfo;
import com.xmd.inner.event.UpdateRoomEvent;
import com.xmd.inner.event.UpdateSeatEvent;
import com.xmd.inner.httprequest.DataManager;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;

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
    @BindView(R2.id.tv_book_time)
    TextView mBookTimeText;

    private Subscription mSaveBookSeatSubscription;

    private SeatInfo mSeatInfo;
    private String mDate;
    private TimePickerView mPickerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSeatInfo = (SeatInfo) getIntent().getSerializableExtra(EXTRA_SEAT_INFO);
        setContentView(R.layout.activity_native_seat_book);
        ButterKnife.bind(this);

        setTitle(ResourceUtils.getString(R.string.native_seat_book));
        initTimePicker();
        mBookSeatText.setText(mSeatInfo.name);
        mBookPhoneEdit.setHint("请输入手机号码");
        mDate = DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        mBookTimeText.setText(DateUtil.dateToString(new Date(), "MM月dd日 HH:mm:ss"));
    }

    private void initTimePicker() {
        Calendar currentDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.set(2050, 11, 31);
        mPickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mDate = DateUtil.dateToString(date, "yyyy-MM-dd HH:mm:ss");
                mBookTimeText.setText(DateUtil.dateToString(date, "MM月dd日 HH:mm:ss"));
            }
        })
                .setLayoutRes(R.layout.layout_pick_view, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        TextView mCancel = (TextView) v.findViewById(R.id.tv_picker_cancel);
                        TextView mConfirm = (TextView) v.findViewById(R.id.tv_picker_confirm);
                        mCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPickerView.dismiss();
                            }
                        });

                        mConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPickerView.returnData();
                                mPickerView.dismiss();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, true})
                .setRangDate(currentDate, endDate)
                .setDate(currentDate)
                .setOutSideCancelable(true)
                .setContentSize(16)
                .setCancelColor(ResourceUtils.getColor(R.color.colorText3))
                .setSubmitColor(ResourceUtils.getColor(R.color.colorPink))
                .setTextColorCenter(ResourceUtils.getColor(R.color.colorPink))
                .setDividerColor(ResourceUtils.getColor(R.color.colorPink))
                .build();
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
        String phone = mBookPhoneEdit.getText().toString().trim();
        saveBook(String.valueOf(mSeatInfo.roomId), String.valueOf(mSeatInfo.id), phone, mDate);
    }

    @OnClick(R2.id.tv_book_time)
    public void onTimeSelect() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        mPickerView.setDate(Calendar.getInstance());
        mPickerView.show();
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
