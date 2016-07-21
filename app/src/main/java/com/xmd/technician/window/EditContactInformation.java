package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.bean.AddOrEditResult;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;


/**
 * Created by Administrator on 2016/7/8.
 */
public class EditContactInformation extends BaseActivity implements TextWatcher{

    @Bind(R.id.et_remark_name)
    EditText mRemarkName;
    @Bind(R.id.ed_remark_message)
    EditText mRemarkMessage;
    @Bind(R.id.btn_save_edit)
    Button mSaveEdit;
    @Bind(R.id.text_remark_num)
    TextView remarkNum;
    private String remarkName;
    private String remarkMessage;
    private String remarkId;
    private String remarkPhone;
    private Subscription getEditResultSubscription;
    private String nickName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_customer_activity);
        ButterKnife.bind(this);
        initView();
    }
    private void initView(){
        setTitle(R.string.change_remark_message);
        setBackVisible(true);
        Intent intent = getIntent();
        remarkName =intent.getStringExtra(RequestConstant.KEY_NOTE_NAME);
        remarkMessage =intent.getStringExtra(RequestConstant.KEY_REMARK);
        remarkId = intent.getStringExtra(RequestConstant.KEY_ID);
        remarkPhone = intent.getStringExtra(RequestConstant.KEY_PHONE_NUMBER);
        nickName = intent.getStringExtra(RequestConstant.KEY_USERNAME);

        mRemarkName.setText(remarkName);
        if(!TextUtils.isEmpty(remarkName)){
            mSaveEdit.setEnabled(true);
        }
        if(!remarkMessage.equals(ResourceUtils.getString(R.string.customer_remark_empty))){
            mRemarkMessage.setText(remarkMessage);
        }

        mRemarkName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>8){
                    makeShortToast(ResourceUtils.getString(R.string.limit_input_text_remark));
                }
            }
        });
        mRemarkMessage.addTextChangedListener(this);
        getEditResultSubscription = RxBus.getInstance().toObservable(AddOrEditResult.class).subscribe(result ->{
            handlerEditResult(result);
        });
    }
    @OnClick(R.id.btn_save_edit)
   public   void saveEdit(){
        remarkName = mRemarkName.getText().toString();
        remarkMessage = mRemarkMessage.getText().toString();

        Map<String,String> params = new HashMap<>();
        params.put(RequestConstant.KEY_ID,remarkId);
        params.put(RequestConstant.KEY_NOTE_NAME,remarkName);
        params.put(RequestConstant.KEY_REMARK,remarkMessage);
        params.put(RequestConstant.KEY_PHONE_NUMBER,remarkPhone);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_ADD_OR_EDIT_CUSTOMER,params);
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length()>30){
            makeShortToast(ResourceUtils.getString(R.string.limit_input_text));
            mSaveEdit.setEnabled(false);
        }else{
            if((30-s.length())>0){
                remarkNum.setText(30-s.length()+"");
            }
            if(Utils.isNotEmpty(mRemarkName.getText().toString())){
                mSaveEdit.setEnabled(true);
            }
        }


    }
    private void  handlerEditResult(AddOrEditResult result){
        if(result.resultcode==200){
            makeShortToast(ResourceUtils.getString(R.string.edit_remark_success));

            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.putExtra(RequestConstant.KEY_NOTE_NAME,mRemarkName.getText().toString());
                    intent.putExtra(RequestConstant.KEY_REMARK,mRemarkMessage.getText().toString());
                    setResult(RESULT_OK,intent);
                    EditContactInformation.this.finish();
                }
            }, 1000);
        }else{
            makeShortToast(result.msg.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(getEditResultSubscription!=null){
            RxBus.getInstance().unsubscribe(getEditResultSubscription);
        }
    }
}
