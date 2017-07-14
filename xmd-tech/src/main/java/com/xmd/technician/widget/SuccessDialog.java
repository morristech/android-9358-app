package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.xmd.technician.R;
import com.xmd.technician.common.Utils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by LHJ on 2016/9/8.
 */
public class SuccessDialog extends Dialog {

    @BindView(R.id.icon_success)
    ImageView iconSuccess;

    private TextView text;

    private  Button btnSure;
    boolean showBtn;
    private String mTipInfo;

    public SuccessDialog(Context context) {
        this(context, R.style.default_dialog_style);
    }

    public SuccessDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public  SuccessDialog(Context context,String msg,boolean showBtn){
        this(context, R.style.success_dialog_style);
        this.mTipInfo = msg;
        this.showBtn = showBtn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success_dialog);
        text = (TextView) findViewById(R.id.text);
        btnSure = (Button) findViewById(R.id.btn_sure);

        if(Utils.isNotEmpty(mTipInfo)){
            text.setText(mTipInfo);
        }
        if(showBtn){
            btnSure.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_sure)
    public void onConfirmClick(){
        dismiss();
    }
}
