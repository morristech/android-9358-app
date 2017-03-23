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
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.share.ShareController;
import com.xmd.technician.widget.RoundImageView;

import java.util.EnumMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/11/11.
 */
public class DynamicShareTechActivity extends BaseActivity {

    @Bind(R.id.user_card_head)
    RoundImageView userCardHead;
    @Bind(R.id.user_card_name)
    TextView userCardName;
    @Bind(R.id.user_card_num)
    TextView userCardNum;
    @Bind(R.id.ll_tech_code)
    LinearLayout llTechCode;
    @Bind(R.id.user_card_club)
    TextView userCardClub;
    @Bind(R.id.user_share_img)
    ImageView userShareImg;
    @Bind(R.id.user_share_btn)
    Button userShareBtn;


    private String userHead;
    private String userName;
    private String userNum;
    private String userClubName;
    private String userShareUrl;
    private Boolean userCanShare;
    private String codeUrl;
    private Bitmap mQRBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_share);
        ButterKnife.bind(this);
        setTitle(ResourceUtils.getString(R.string.all_recent_status_activity));
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
        Glide.with(this).load(userHead).into(userCardHead);
        userCardName.setText(userName);
        userCardClub.setText(userClubName);

    if(userCanShare){
        userShareBtn.setEnabled(true);
    }else{
        userShareBtn.setEnabled(false);
    }
    if(Utils.isNotEmpty(userNum)){
        userCardNum.setText(userNum);
    }else{
        llTechCode.setVisibility(View.GONE);
    }
    if(Utils.isNotEmpty(codeUrl)){
        Glide.with(DynamicShareTechActivity.this).load(codeUrl).error(ResourceUtils.getDrawable(R.drawable.icon22)).into(userShareImg);
    }else {
        userShareImg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if(mQRBitmap==null){
                    try {
                        mQRBitmap=encodeAsBitmap(0, codeUrl, userShareImg.getWidth());
                        userShareImg.setImageBitmap(mQRBitmap);
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
        ShareController.doShare("",userShareUrl,SharedPreferenceHelper.getUserName() + "欢迎您","点我聊聊，更多优惠，更好服务！",Constant.SHARE_BUSINESS_CARD,"");
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


}
