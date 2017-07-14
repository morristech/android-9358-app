package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.image_tool.ImageTool;
import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.widget.CircularBeadImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sdcm on 17-5-22.
 */
public class GMessageEditContentFragment extends BaseFragment implements TextWatcher {

    public final static String EXTRA_LIMIT_SIZE = "image_limit_size";
   @BindView(R.id.group_add_pic)
    CircularBeadImageView groupAddPic;
   @BindView(R.id.image_delete)
    ImageView imageDelete;
   @BindView(R.id.limit_image_size)
    TextView limitImageSize;
   @BindView(R.id.edit_content)
    EditText mEditContent;
   @BindView(R.id.editable_amount)
    TextView mEditAbleAmount;

    private ImageTool mImageTool = new ImageTool();
    private String imageUrl;
    private int mLimitImageSize;
    private String mMessageContent;
    private boolean mSelectCoupon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_edit_content, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mEditContent.addTextChangedListener(this);
        mLimitImageSize = getArguments().getInt(EXTRA_LIMIT_SIZE);
        limitImageSize.setText(String.format("上传图片大小需小于%sM", String.valueOf(mLimitImageSize)));
    }

    @OnClick({R.id.group_add_pic, R.id.image_delete, R.id.btn_previous_step, R.id.btn_preview})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_add_pic:
                mImageTool.onlyPick(true).start(this, (s, uri, bitmap) -> {
                    if (s == null && uri != null) {
                        imageUrl = uri.getPath();
                        Glide.with(getActivity()).load(imageUrl).into(groupAddPic);
                        imageDelete.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case R.id.image_delete:
                deleteImage();
                break;
            case R.id.btn_previous_step:
                deleteImage();
                clearMessageContent();
                ((GroupMessageCustomerActivity) getActivity()).gotoCouponFragment();
                break;
            case R.id.btn_preview:
                if (Utils.isEmpty(imageUrl) && Utils.isEmpty(mMessageContent) && !mSelectCoupon) {
                    Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.send_group_message_alert));
                    return;
                }
                ((GroupMessageCustomerActivity) getActivity()).gotoConfirmFragment();
                break;
        }
    }

    private void deleteImage(){
        Glide.with(getActivity()).load(R.drawable.img_group_add_img).into(groupAddPic);
        imageDelete.setVisibility(View.GONE);
        imageUrl = "";
    }

    private void clearMessageContent(){
        mEditAbleAmount.setText("100");
        mEditContent.setText("");
        mMessageContent="";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageTool.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mEditContent.getText().toString().length() >= 100) {
            mEditContent.setSelected(false);
            mEditAbleAmount.setText("0");

        } else {
            if ((100 - s.length()) >= 0) {
                mEditAbleAmount.setText(100 - s.length() + "");
            }
            if (Utils.isNotEmpty(mEditContent.getText().toString())) {
                mEditContent.setSelected(true);
            }
        }
        mMessageContent = mEditContent.getText().toString();
    }

    public void setSelectCoupon(boolean selected){
        mSelectCoupon = selected;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMessageContent() {
        return mMessageContent;
    }
}
