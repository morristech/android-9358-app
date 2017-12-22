package com.xmd.technician.window;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
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
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.FileUtils;
import com.xmd.technician.common.ImageLoader;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.share.ShareController;

import java.util.EnumMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 2016/7/19.
 */
public class TechShareCardActivity extends BaseActivity {


    @BindView(R.id.user_card_head)
    ImageView mCardHead;
    @BindView(R.id.user_card_name)
    TextView mCardName;
    @BindView(R.id.user_card_num)
    TextView mCardNum;
    @BindView(R.id.user_card_club)
    TextView mCardClub;
    @BindView(R.id.user_share_btn)
    Button mShareBtn;
    @BindView(R.id.user_share_img)
    ImageView mUserShareCode;
    @BindView(R.id.ll_tech_code)
    LinearLayout mTechCode;
    @BindView(R.id.user_save_btn)
    Button userSaveBtn;


    private String userHead;
    private String userName;
    private String userNum;
    private String userClubName;
    private String userShareUrl;
    private Boolean userCanShare;
    private String codeUrl;
    private Bitmap mQRBitmap;

    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

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

        Glide.with(this).load(userHead).error(R.drawable.img_default_square).into(mCardHead);
        mCardName.setText(userName);
        mCardClub.setText(userClubName);

        if (userCanShare) {
            mShareBtn.setEnabled(true);
        } else {
            mShareBtn.setEnabled(false);
        }
        if (Utils.isNotEmpty(userNum)) {
            mCardNum.setText(userNum);
        } else {
            mTechCode.setVisibility(View.GONE);
        }
        if (Utils.isNotEmpty(codeUrl)) {
            Glide.with(TechShareCardActivity.this).load(codeUrl).into(mUserShareCode);
        } else {

            mUserShareCode.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (mQRBitmap == null) {
                        try {
                            mQRBitmap = encodeAsBitmap(R.mipmap.ic_launcher, codeUrl, mUserShareCode.getWidth());
                            mUserShareCode.setImageBitmap(mQRBitmap);
                     /*         BitmapDrawable bd = (BitmapDrawable) ResourceUtils.getDrawable(R.mipmap.ic_launcher);
                            Bitmap bitmapLogo = bd.getBitmap();

                            mUserShareCode.setImageBitmap( addLogo(mQRBitmap,bitmapLogo));*/

                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            });

            //    }

        }
    }

    @OnClick(R.id.user_share_btn)
    public void shareUser() {
        ShareController.doShare(userHead, userShareUrl, SharedPreferenceHelper.getUserName() + "欢迎您", "点我聊聊，更多优惠，更好服务！", Constant.SHARE_BUSINESS_CARD, "");
    }

    private Bitmap encodeAsBitmap(int logoResourceId, String contentString, int dimension) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(contentString, BarcodeFormat.QR_CODE, dimension, dimension, hints);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
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

    @OnClick(R.id.user_save_btn)
    public void saveUserCard() {
        String filePath = Environment.getExternalStorageDirectory() + "/" + ResourceUtils.getString(R.string.save_tech_card_path) + ".jpg";
        if (FileUtils.checkFileExist(filePath, false)) {
            XToast.show(ResourceUtils.getString(R.string.had_saved_tech_card));
        } else {
            ThreadPoolManager.run(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = ImageLoader.readBitmapFromImgUrl(codeUrl);
                    ImageLoader.saveBitmapToLocal(TechShareCardActivity.this, bitmap, ResourceUtils.getString(R.string.save_tech_card_path));
                }
            });
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
