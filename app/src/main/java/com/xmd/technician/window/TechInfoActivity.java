package com.xmd.technician.window;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.http.gson.AlbumResult;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.TechEditResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.model.AlbumInfo;
import com.xmd.technician.model.TechDetailInfo;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class TechInfoActivity extends BaseActivity {

    @Bind(R.id.grid_view_container) GridView mAlbumContainer;
    @Bind(R.id.avatar) ImageView mAvatar;
    @Bind(R.id.user_name) TextView mUserName;
    @Bind(R.id.tech_number) TextView mSerialNo;
    @Bind(R.id.native_place) TextView mNativePlace;
    @Bind(R.id.phone_number) TextView mPhoneNumber;
    @Bind(R.id.introduce) TextView mDescription;
    @Bind(R.id.button_female) RadioButton mFemale;
    @Bind(R.id.button_male) RadioButton mMale;

    private Subscription mGetTechInfoSubscription;
    private Subscription mUpdateTechInfoSubscription;
    private Subscription mUploadAvatarSubscription;
    private Subscription mUploadAlbumSubscription;
    private Subscription mDeleteAlbumSubscription;

    private List<AlbumInfo> mAlbums;
    private TechDetailInfo mTechInfo;
    private String mPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_info);

        ButterKnife.bind(this);

        setTitle(R.string.title_activity_data_edit);
        setBackVisible(true);

        mGetTechInfoSubscription = RxBus.getInstance().toObservable(TechEditResult.class).subscribe(
                techEditResult -> getTechInfoResult(techEditResult));

        mUpdateTechInfoSubscription = RxBus.getInstance().toObservable(UpdateTechInfoResult.class).subscribe(
                updateTechInfoResult -> finish());

        mUploadAlbumSubscription = RxBus.getInstance().toObservable(AlbumResult.class).subscribe();

        mDeleteAlbumSubscription = RxBus.getInstance().toObservable(AlbumResult.class).subscribe();

        mUploadAvatarSubscription = RxBus.getInstance().toObservable(AvatarResult.class).subscribe();

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_EDIT_INFO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetTechInfoSubscription);
        RxBus.getInstance().unsubscribe(mUpdateTechInfoSubscription);
        RxBus.getInstance().unsubscribe(mUploadAlbumSubscription);
        RxBus.getInstance().unsubscribe(mDeleteAlbumSubscription);
        RxBus.getInstance().unsubscribe(mUploadAvatarSubscription);
    }

    private void getTechInfoResult(TechEditResult result){
        if(result.respData != null){
            mAlbums = result.respData.albums;
            mTechInfo = result.respData.info;
            mPhoneNum = result.respData.phoneNum;
            initView();
        }
    }

    private void initView(){
        mUserName.setText(mTechInfo.name);
        mSerialNo.setText(mTechInfo.serialNo);
        mPhoneNumber.setText(mPhoneNum);
        mDescription.setText(mTechInfo.description);

        if (mTechInfo.gender.equals("male")){
            mMale.setChecked(true);
        }else {
            mFemale.setChecked(true);
        }

        Glide.with(this).load(mTechInfo.avatarUrl).into(mAvatar);
    }

    @OnClick(R.id.change_avatar_txt)
    public void changeAvatarClick(){

    }

    @OnClick(R.id.native_place)
    public void nativePlaceClick(){

    }

}
