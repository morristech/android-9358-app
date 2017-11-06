package com.xmd.technician.window;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.image_tool.ImageTool;
import com.xmd.technician.Adapter.HelloTemplateAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.common.ImageUploader;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.HelloSaveTemplateResult;
import com.xmd.technician.http.gson.HelloSysTemplateResult;
import com.xmd.technician.http.gson.HelloUploadImgResult;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by ZR on 17-2-27.
 * 打招呼设置
 */

public class HelloSettingActivity extends BaseActivity {
    public static final int REQUEST_CODE_LOCAL_PICTURE_HELLO = 0x01;

    @BindView(R.id.rc_template_list)
    RecyclerView mTemplateListView; //系统模版列表
    @BindView(R.id.cb_custom_text_enable)
    CheckBox mCustomCheck;  // 选择自定义招呼内容
    @BindView(R.id.et_custom_text_content)
    EditText mCustomText;   // 自定义内容
    @BindView(R.id.layout_image)
    View mLayoutImage;
    @BindView(R.id.img_need_upload)
    ImageView mHelloImageView;
    @BindView(R.id.btn_add_image)
    Button mAddImgBtn;      // 添加图片
    @BindView(R.id.btn_template_confirm)
    Button mTempConfirmBtn; // 确定保存

    private HelloTemplateAdapter mAdapter;
    private HelloSettingManager mHelloSettingManager;

    private String mImageId;
    private Bitmap mHelloBitmap;  //缩略图
    private String mTemplateContent;
    private String mTemplateId;

    private Subscription mGetSysTemplateListSubscription;
    private Subscription mUploadTemplateImageSubscription;
    private Subscription mSaveHelloTemplateSubscription;

    private ImageTool mImageTool = new ImageTool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_setting);
        ButterKnife.bind(this);
        setTitle(R.string.hello_setting_bar_title);
        setBackVisible(true);

        mHelloSettingManager = HelloSettingManager.getInstance();
        mAdapter = new HelloTemplateAdapter(this);
        mAdapter.setOnTemplateItemClickListener(info -> {
            // 选中系统模版,取消自定义模版
            mCustomCheck.setChecked(false);
            mCustomCheck.setEnabled(true);
        });
        mTemplateListView.setNestedScrollingEnabled(false);
        mTemplateListView.setLayoutManager(new GridLayoutManager(HelloSettingActivity.this, 1));
        mTemplateListView.setAdapter(mAdapter);
        initCustom();
        initImage();

        mGetSysTemplateListSubscription = RxBus.getInstance().toObservable(HelloSysTemplateResult.class).subscribe(helloSysTemplateResult -> {
            handleHelloTemplateListResult(helloSysTemplateResult);
        });
        mUploadTemplateImageSubscription = RxBus.getInstance().toObservable(HelloUploadImgResult.class).subscribe(helloUploadImgResult -> {
            handleUploadTemplateImgResult(helloUploadImgResult);
        });
        mSaveHelloTemplateSubscription = RxBus.getInstance().toObservable(HelloSaveTemplateResult.class).subscribe(result -> {
            handleSaveHelloTemplateResult(result);
        });

        mHelloImageView.setOnClickListener(v -> selectHelloPicture());

        // 获取系统模版列表
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_SYS_TEMPLATE_LIST);
    }

    // 初始化自定义模版内容
    private void initCustom() {
        if (mHelloSettingManager.getTemplateParentId() == null) {
            // 当前设置的为自定义模版
            mCustomCheck.setChecked(true);
            mCustomCheck.setEnabled(false);
            mCustomText.setText(mHelloSettingManager.getTemplateContentText());
        } else {
            // 当前设置的为系统模版
            mCustomCheck.setChecked(false);
            mCustomCheck.setEnabled(true);
        }
        mCustomCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCustomCheck.setEnabled(false);
                mAdapter.unCheckedData();
            }
        });
    }

    // 初始化招呼图片
    private void initImage() {
        if (!TextUtils.isEmpty(mHelloSettingManager.getTemplateImageId()) && !TextUtils.isEmpty(mHelloSettingManager.getTemplateImageUrl())) {
            mImageId = mHelloSettingManager.getTemplateImageId();
            mAddImgBtn.setVisibility(View.GONE);
            mLayoutImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(mHelloSettingManager.getTemplateImageUrl()).into(mHelloImageView);
        } else {
            mAddImgBtn.setVisibility(View.VISIBLE);
            mLayoutImage.setVisibility(View.GONE);
        }
    }

    // 处理系统模版列表
    private void handleHelloTemplateListResult(HelloSysTemplateResult result) {
        if (result != null && result.statusCode == 200) {
            mAdapter.setData(result.respData);
        }
    }

    // 处理模版图片
    private void handleUploadTemplateImgResult(HelloUploadImgResult result) {
        hideLoading();
        if (result != null && result.statusCode == 200 && result.respData != null) {
            // 上传成功:保存打招呼内容
            mImageId = result.respData.imageId;
            mLayoutImage.setVisibility(View.VISIBLE);
            mAddImgBtn.setVisibility(View.GONE);        //隐藏添加图片按钮
            mHelloImageView.setImageBitmap(mHelloBitmap);    //加载缩略图
        } else {
            // 上传失败
            showToast("上传失败，请稍候重试");
        }
    }

    // 保存打招呼设置
    private void handleSaveHelloTemplateResult(HelloSaveTemplateResult result) {
        hideLoading();
        if (result != null && result.statusCode == 200) {
            // 保存成功
            mHelloSettingManager.setTemplate(result.respData);
            // 缓存打招呼图片
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DOWNLOAD_HELLO_IMAGE_CACHE);
            showToast("保存成功");
            finish();
        } else {
            // 保存失败
            showToast("保存打招呼内容失败，请稍候重试");
        }
    }

    // 添加上传的图片
    public void onAddBtnClick(View view) {
        selectHelloPicture();
    }

    private void selectHelloPicture() {
        mImageTool.maxSize(Constant.ALBUM_MAX_SIZE).start(this, new ImageTool.ResultListener() {
            @Override
            public void onResult(String s, Uri uri, Bitmap bitmap) {
                if (s != null) {
                    XToast.show(s);
                } else if (bitmap != null) {
                    mHelloBitmap = bitmap;
                    uploadHelloPicture();
                }
            }
        });
    }

    private void uploadHelloPicture() {
        showLoading("正在上传图片...");
        ImageUploader.getInstance().upload(ImageUploader.TYPE_HELLO, mHelloBitmap);
    }

    @OnClick(R.id.btn_clear_image)
    public void clearImage() {
        mImageId = null;
        mAddImgBtn.setVisibility(View.VISIBLE);
        mLayoutImage.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageTool.onActivityResult(requestCode, resultCode, data);
    }


    public void onConfirmBtnClick(View view) {
        // 检查自定义招呼内容和招呼图片
        if (mCustomCheck.isChecked() && TextUtils.isEmpty(mCustomText.getText().toString().trim())) {
            showToast("请输入打招呼的内容");
            return;
        }

        // 即将保存的内容
        if (mCustomCheck.isChecked()) {
            // 自定义招呼:只需要提供内容
            mTemplateContent = mCustomText.getText().toString().trim();
            mTemplateId = null;
        } else {
            // 系统招呼:只需要提供模版ID
            mTemplateContent = null;
            mTemplateId = String.valueOf(mAdapter.getCheckId());
        }
        if (mTemplateContent == null && mTemplateId.equals("null")) {
            XToast.show("请选择模板");
            return;
        }

        showLoading("正在提交...");
        saveHelloTemplate(mTemplateContent, (mImageId != null ? mImageId : null), mTemplateId);
    }

    private void uploadHelloImage(String imageFile) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_IMG_FILE, imageFile);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_HELLO_TEMPLATE_IMG, params);
    }

    private void saveHelloTemplate(String text, String imageId, String templateId) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MSG_TYPE_TEXT, text);
        params.put(RequestConstant.KEY_HELLO_TEMPLATE_ID, templateId);
        params.put(RequestConstant.KEY_TEMPLATE_IMAGE_ID, imageId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SAVE_SET_TEMPLATE, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelloBitmap != null) {
            mHelloBitmap.recycle();
        }
        RxBus.getInstance().unsubscribe(mGetSysTemplateListSubscription,
                mUploadTemplateImageSubscription,
                mSaveHelloTemplateSubscription);
    }
}
