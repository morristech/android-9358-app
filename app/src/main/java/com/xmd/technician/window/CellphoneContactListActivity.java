package com.xmd.technician.window;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xmd.technician.Adapter.CellPhoneContactAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.PhoneContactor;
import com.xmd.technician.common.CharacterParser;
import com.xmd.technician.common.PinyinContactUtil;
import com.xmd.technician.widget.DividerItemDecoration;
import com.xmd.technician.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/4.
 */
public class CellphoneContactListActivity extends BaseActivity {
    TextView contentDialog;
    SideBar contactSidebar;
    private Context mContext;
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    private List<PhoneContactor> contactList = new ArrayList<PhoneContactor>();
    private PhoneContactor mPhoneContactor;
    private RecyclerView contactorList;
    private CellPhoneContactAdapter adapter;
    private static final int PHONES_NUMBER_INDEX = 1;
    private CellPhoneContactAdapter.AddClieckedListener clieckedListener;
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    private int mSelectPosition =-1;
    protected LinearLayoutManager mLayoutManager;
    private CharacterParser characterParser;
    private PinyinContactUtil pinyinContactUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contact);
        mContext = this;
        setTitle(R.string.customer_telephone_list);
        setBackVisible(true);
        characterParser = CharacterParser.getInstance();
        getPhoneContacts();
        for (PhoneContactor contactor : contactList){
            if(characterParser.getSelling(contactor.name.substring(0,1)).toUpperCase().substring(0,1).matches("[A-Z]")){
                contactor.sortLetters = characterParser.getSelling(contactor.name.substring(0,1)).toUpperCase().substring(0,1);
            }else{
                contactor.sortLetters = "#";
            }
        }
        pinyinContactUtil = new PinyinContactUtil();
        Collections.sort(contactList,pinyinContactUtil);
        contactSidebar = (SideBar) findViewById(R.id.contact_sidebar);
        contentDialog = (TextView) findViewById(R.id.content_dialog);
        mLayoutManager = new LinearLayoutManager(this);
        contactorList = (RecyclerView) findViewById(R.id.contactor_list);
        contactorList.setLayoutManager(mLayoutManager);
        contactorList.setItemAnimator(new DefaultItemAnimator());
        contactorList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));


        adapter = new CellPhoneContactAdapter(mContext, contactList, new CellPhoneContactAdapter.AddClieckedListener() {
            @Override
            public void addToContactor(String name, String phone) {
                Intent intent = new Intent();
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                setResult(RESULT_OK, intent);
                CellphoneContactListActivity.this.finish();
            }
        });
        contactorList.setAdapter(adapter);
        contactSidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                for (int i = 0; i <contactList.size() ; i++) {
                    PhoneContactor contactor = contactList.get(i);
              String firstLetter =   characterParser.getSelling(contactor.name).substring(0,1).toUpperCase();
                    if(TextUtils.equals(firstLetter,s)){
                        mSelectPosition = i;
                        mLayoutManager.scrollToPositionWithOffset(i>0?i:0,0);


                    }

                }
            }
        });
    }

    private List<PhoneContactor> getPhoneContacts() {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                mPhoneContactor = new PhoneContactor(contactName, phoneNumber);
                contactList.add(mPhoneContactor);
            }
        }
        phoneCursor.close();
        return contactList;
    }

    private void showText(String letter){
        contentDialog.setVisibility(View.VISIBLE);
        contentDialog.setText(letter);

    }



}
