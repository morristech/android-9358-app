package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.common.CommonMsgOnClickInterface;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lhj on 2016/7/30.
 */
public class CommonMsgFragmentOne extends Fragment {
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text4)
    TextView text4;
    @Bind(R.id.text5)
    TextView text5;

    private  static  CommonMsgOnClickInterface msgOnClickInterface;
    private static  CommonMsgFragmentOne commonMsgFragmentOne;

    public static CommonMsgFragmentOne getInstance(CommonMsgOnClickInterface clickInterface){
        msgOnClickInterface = clickInterface;
        if(commonMsgFragmentOne==null){
            commonMsgFragmentOne = new CommonMsgFragmentOne();
        }
        return commonMsgFragmentOne;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_view_one, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    @OnClick({R.id.text1,R.id.text2,R.id.text3,R.id.text4,R.id.text5})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.text1:
                msgOnClickInterface.onMsgClickListener(text1.getText().toString());
                break;
            case R.id.text2:
                msgOnClickInterface.onMsgClickListener(text2.getText().toString());
                break;
            case R.id.text3:
                msgOnClickInterface.onMsgClickListener(text3.getText().toString());
                break;
            case R.id.text4:
                msgOnClickInterface.onMsgClickListener(text4.getText().toString());
                break;
            case R.id.text5:
                msgOnClickInterface.onMsgClickListener(text5.getText().toString());
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
