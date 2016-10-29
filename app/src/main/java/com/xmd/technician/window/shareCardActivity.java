package com.xmd.technician.window;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.TechSummaryInfo;
import com.xmd.technician.common.ImageLoader;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/19.
 */
public class shareCardActivity extends BaseActivity {


    @Bind(R.id.user_card_head)
    ImageView mCardHead;
    @Bind(R.id.user_card_name)
    TextView mCardName;
    @Bind(R.id.user_card_num)
    TextView mCardNum;
    @Bind(R.id.user_card_club)
    TextView mCardClub;
    @Bind(R.id.user_share_btn)
    Button mShareBtn;
    @Bind(R.id.user_share_img)
    ImageView mUserShareCode;
    @Bind(R.id.ll_tech_code)
    LinearLayout mTechCode;
    @Bind(R.id.img_bg)
    ImageView imgBg;


    private String userHead;
    private String userName;
    private String userNum;
    private String userClubName;
    private String userShareUrl;
    private Boolean userCanShare;
    private String codeUrl;
    private Bitmap mQRBitmap;
    private TechSummaryInfo mTechInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_card);
        ButterKnife.bind(this);
        setTitle(ResourceUtils.getString(R.string.personal_fragment_layout_user_card));
        setBackVisible(true);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        userHead = intent.getStringExtra(Constant.TECH_USER_HEAD_URL);
        userName = intent.getStringExtra(Constant.TECH_USER_NAME);
        userNum = intent.getStringExtra(Constant.TECH_USER_TECH_NUM);
        userClubName = intent.getStringExtra(Constant.TECH_USER_CLUB_NAME);
        userShareUrl = intent.getStringExtra(Constant.TECH_SHARE_URL);
        userCanShare = intent.getBooleanExtra(Constant.TECH_CAN_SHARE, false);
        codeUrl = intent.getStringExtra(Constant.TECH_ShARE_CODE_IMG);

        Glide.with(this).load(userHead).into(mCardHead);
        mCardName.setText(userName);
        mCardClub.setText(userClubName);
        ;

        if(userCanShare){
            mShareBtn.setEnabled(true);
        }else{
            mShareBtn.setEnabled(false);
        }
        if(Utils.isNotEmpty(userNum)){
            mCardNum.setText(userNum);
        }else{
            mTechCode.setVisibility(View.GONE);
        }
        if(Utils.isNotEmpty(codeUrl)){
            Glide.with(shareCardActivity.this).load(codeUrl).error(ResourceUtils.getDrawable(R.drawable.icon22)).into(mUserShareCode);
        }else {
            mUserShareCode.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if(mQRBitmap==null){
                        try {
                            mQRBitmap=encodeAsBitmap(0, codeUrl, mUserShareCode.getWidth());
                            mUserShareCode.setImageBitmap(mQRBitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            });

        }

    }

    @OnClick(R.id.user_share_btn)
    public void shareUser() {
        doShare();
    }
    private Bitmap encodeAsBitmap(int logoResourceId,String contentString,int dimension) throws WriterException {
        Map<EncodeHintType,Object> hints = new EnumMap<EncodeHintType,Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(contentString, BarcodeFormat.QR_CODE, dimension, dimension, hints);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        //TODO add logo to the bitmap
        return bitmap;
    }
    public void doShare() {
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, () -> {
            Bitmap thumbnail = ImageLoader.readBitmapFromImgUrl(SharedPreferenceHelper.getUserAvatar());
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> {
                Map<String, Object> params = new HashMap<>();
                params.put(Constant.PARAM_SHARE_THUMBNAIL, thumbnail);
                params.put(Constant.PARAM_SHARE_URL, userShareUrl);
                params.put(Constant.PARAM_SHARE_TITLE,SharedPreferenceHelper.getUserName() + "欢迎您");
                params.put(Constant.PARAM_SHARE_DESCRIPTION, "点我聊聊，更多优惠，更好服务！");
                params.put(Constant.PARAM_SHARE_TYPE,Constant.SHARE_BUSINESS_CARD);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM, params);
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
