package com.xmd.black;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.crazyman.library.PermissionTool;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.CharacterParser;
import com.xmd.black.adapter.CellPhoneContactAdapter;
import com.xmd.black.bean.PhoneContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lhj on 17-7-25.
 */

public class CellphoneContactListActivity extends BaseActivity {

    private Context mContext;
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    private List<PhoneContact> contactList = new ArrayList<>();
    private PhoneContact mPhoneContact;
    private RecyclerView contactListView;
    private CellPhoneContactAdapter adapter;
    private static final int PHONES_NUMBER_INDEX = 1;
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    protected LinearLayoutManager mLayoutManager;
    private CharacterParser characterParser;
    private PinyinContactUtil pinyinContactUtil;

    private static final int REQUEST_CODE_CONTACTS_PERMISSION = 0x0001;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contact_list);
        mContext = this;
        setTitle("联系人列表");
        setBackVisible(true);
        characterParser = CharacterParser.getInstance();
        mLayoutManager = new LinearLayoutManager(this);
        contactListView = (RecyclerView) findViewById(R.id.contact_list);
        contactListView.setLayoutManager(mLayoutManager);
        contactListView.setItemAnimator(new DefaultItemAnimator());
        contactListView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        adapter = new CellPhoneContactAdapter(mContext, contactList, new CellPhoneContactAdapter.AddContactListener() {
            @Override
            public void addToContact(String name, String phone) {
                Intent intent = new Intent();
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                setResult(RESULT_OK, intent);
                CellphoneContactListActivity.this.finish();
            }
        });
        contactListView.setAdapter(adapter);
        PermissionTool.requestPermission(this, new String[]{Manifest.permission.READ_CONTACTS}, new String[]{"读取联系人"}, REQUEST_CODE_CONTACTS_PERMISSION);
    }

    private List<PhoneContact> getPhoneContacts() {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                mPhoneContact = new PhoneContact(contactName, phoneNumber);
                contactList.add(mPhoneContact);
            }
        }
        phoneCursor.close();
        return contactList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTACTS_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                ContentResolver resolver = CellphoneContactListActivity.this.getContentResolver();
                Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
                if (phoneCursor != null) {
                    getPhoneContacts();
                    for (PhoneContact contact : contactList) {
                        if (characterParser.getSelling(contact.name.substring(0, 1)).toUpperCase().substring(0, 1).matches("[A-Z]")) {
                            contact.sortLetters = characterParser.getSelling(contact.name.substring(0, 1)).toUpperCase().substring(0, 1);
                        } else {
                            contact.sortLetters = "#";
                        }
                    }
                    pinyinContactUtil = new PinyinContactUtil();
                    Collections.sort(contactList, pinyinContactUtil);
                    adapter.setData(contactList);
                } else {
                    XToast.show("读取联系失败");
                }
            } else {
                XToast.show("读取联系人权限未开启");
            }
        }
    }


}
