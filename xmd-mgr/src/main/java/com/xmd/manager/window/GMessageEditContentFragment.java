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
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.response.GroupInfoResult;
import com.xmd.manager.widget.CircularBeadImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sdcm on 17-5-22.
 */
public class GMessageEditContentFragment extends BaseFragment implements TextWatcher {

    @Bind(R.id.group_add_pic)
    CircularBeadImageView groupAddPic;
    @Bind(R.id.image_delete)
    ImageView imageDelete;
    @Bind(R.id.limit_image_size)
    TextView limitImageSize;
    @Bind(R.id.edit_content)
    EditText mEditContent;
    @Bind(R.id.editable_amount)
    TextView mEditAbleAmount;

    private ImageTool mImageTool = new ImageTool();
    private String imageUrl;
    private String imageId;
    private int mLimitImageSize;
    private String mMessageContent;

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
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_GROUP_INFO);
    }

    @OnClick({R.id.group_add_pic, R.id.image_delete, R.id.btn_previous_step, R.id.btn_preview})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_add_pic:
                mImageTool.onlyPick(true).start(getActivity(), (s, uri, bitmap) -> {
                    if (s == null && uri != null) {
                        imageUrl = uri.getPath();
                        Glide.with(getActivity()).load(imageUrl).into(groupAddPic);
                        imageDelete.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case R.id.image_delete:
                Glide.with(getActivity()).load(R.drawable.img_group_add_img).into(groupAddPic);
                imageDelete.setVisibility(View.GONE);
                imageId = "";
                break;
            case R.id.btn_previous_step:
                ((GroupMessageCustomerActivity) getActivity()).gotoCouponFragment();
                break;
            case R.id.btn_preview:
                if (Utils.isEmpty(imageUrl) && Utils.isEmpty(mMessageContent)) {
                    Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.send_group_message_alert));
                    return;
                }
                ((GroupMessageCustomerActivity) getActivity()).gotoConfirmFragment();
                break;
        }
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

    public void handlerGroupInfoResult(GroupInfoResult result) {
        if (result.statusCode == 200) {
            mLimitImageSize = result.respData.imageSize;
            limitImageSize.setText(String.format("上传图片大小需小于%sM", String.valueOf(mLimitImageSize)));
        }
    }
}
