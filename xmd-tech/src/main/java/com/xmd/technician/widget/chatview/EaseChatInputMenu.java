package com.xmd.technician.widget.chatview;


import android.Manifest;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.util.EMLog;
import com.xmd.technician.Adapter.ChatGridViewAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.CategoryBean;
import com.xmd.technician.chat.DefaultEmojiconDatas;
import com.xmd.technician.chat.Emojicon;
import com.xmd.technician.chat.utils.SmileUtils;
import com.xmd.technician.common.CommonMsgOnClickInterface;
import com.xmd.technician.common.ThreadPoolManager;
import com.xmd.technician.widget.EmojiconMenu;
import com.xmd.technician.window.CommonMsgFragmentOne;
import com.xmd.technician.window.CommonMsgFragmentTwo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lhj on 17-3-15.
 */

public class EaseChatInputMenu extends EaseChatPrimaryMenuBase implements View.OnClickListener {

    private Context mContext;
    private View mView;
    private EditText mEditText;
    private View buttonSetModeKeyBoard;
    private LinearLayout editTextLayout;
    private View buttonSetModeVoice, buttonSend, buttonPressToSpeak;
    private View btnPhoto, btnFace, btnCommonMsg, btnCommonCoupon, btnAppointment, buttonMore;
    private boolean ctrlPress = false;
    private ChatInputMenuListener inputMenuListener;
    private ChatSentSpecialListener specialListener;
    private EmojiconMenu emojiconMenu;
    private ViewPager commentMessageView;
    private FragmentManager fragmentManager;
    private CommonMsgFragmentOne mFragmentOne;
    private CommonMsgFragmentTwo mFragmentTwo;
    private ImageView indicatorLeft, indicatorRight;
    private View mCommonMsgLayout;
    private ArrayList<Fragment> fragmentsViewPagerList;
    private GridView chatGridView;
    private ChatGridViewAdapter mGridViewAdapter;

    private ImageView inputImagePhoto, inputImageFace, inputImageCommonMsg, inputImageCoupon, inputImageAppointment, inputImageMore;
    private List<CategoryBean> moreMenuList;
    private List<ImageView> imageList;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    public EaseChatInputMenu(Context context) {
        this(context, null);
        init(context, null);
    }

    public EaseChatInputMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public EaseChatInputMenu(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
        init(context, attrs);
    }


    private void init(final Context context, AttributeSet attrs) {
        mContext = context;
        imageList = new ArrayList<>();
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_ease_chat_input_menu, this);
        buttonSetModeVoice = mView.findViewById(R.id.btn_set_mode_voice);
        buttonSetModeKeyBoard = mView.findViewById(R.id.btn_set_mode_keyboard);
        buttonPressToSpeak = mView.findViewById(R.id.btn_press_to_speak);
        mEditText = (EditText) mView.findViewById(R.id.edit_message);
        buttonSend = mView.findViewById(R.id.btn_send);
        buttonMore = mView.findViewById(R.id.btn_more);
        editTextLayout = (LinearLayout) mView.findViewById(R.id.edit_text_layout);
        emojiconMenu = (EmojiconMenu) mView.findViewById(R.id.emojicon_menu_container);
        btnPhoto = mView.findViewById(R.id.btn_photo);
        btnCommonMsg = mView.findViewById(R.id.btn_common_msg);
        btnCommonCoupon = mView.findViewById(R.id.btn_common_coupon);
        btnAppointment = mView.findViewById(R.id.btn_appointment);
        btnFace = mView.findViewById(R.id.rl_face);
        commentMessageView = (ViewPager) findViewById(R.id.comment_msg_view);
        indicatorRight = (ImageView) findViewById(R.id.round_indicator_right);
        indicatorLeft = (ImageView) findViewById(R.id.round_indicator_left);
        mCommonMsgLayout = findViewById(R.id.common_msg_layout);
        chatGridView = (GridView) findViewById(R.id.chat_grid_view);
        inputImagePhoto = (ImageView) findViewById(R.id.input_img_photo);
        inputImageFace = (ImageView) findViewById(R.id.input_img_face);
        inputImageCommonMsg = (ImageView) findViewById(R.id.input_img_common_msg);
        inputImageCoupon = (ImageView) findViewById(R.id.input_img_coupon);
        inputImageAppointment = (ImageView) findViewById(R.id.input_img_appointment);
        inputImageMore = (ImageView) findViewById(R.id.input_img_more);
        imageList.clear();
        imageList.add(inputImagePhoto);
        imageList.add(inputImageFace);
        imageList.add(inputImageCommonMsg);
        imageList.add(inputImageCoupon);
        imageList.add(inputImageMore);

        buttonSetModeVoice.setOnClickListener(this);
        buttonSetModeKeyBoard.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
        buttonMore.setOnClickListener(this);
        mEditText.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);
        btnCommonMsg.setOnClickListener(this);
        btnCommonCoupon.setOnClickListener(this);
        btnAppointment.setOnClickListener(this);
        btnFace.setOnClickListener(this);
        mEditText.requestFocus();
        initEmojicon();
        initChatGridView();

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EMLog.d("key", "keyCode:" + keyCode + " action:" + event.getAction());
                // test on Mac virtual machine: ctrl map to KEYCODE_UNKNOWN
                if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        ctrlPress = true;
                    } else if (event.getAction() == KeyEvent.ACTION_UP) {
                        ctrlPress = false;
                    }
                }
                return false;
            }
        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                EMLog.d("key", "keyCode:" + event.getKeyCode() + " action" + event.getAction() + " ctrl:" + ctrlPress);
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        (event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                ctrlPress == true)) {
                    String s = mEditText.getText().toString();
                    mEditText.setText("");
                    inputMenuListener.onSendMessage(s);
                    return true;
                } else {
                    return false;
                }
            }
        });
        buttonPressToSpeak.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (inputMenuListener != null) {
                    return inputMenuListener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });


    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setCommentMenuView(List<CategoryBean> commentView) {

        if (commentView.size() == 0) {
            btnPhoto.setVisibility(View.VISIBLE);
            btnFace.setVisibility(View.VISIBLE);
            buttonMore.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < commentView.size(); i++) {

                if ((commentView.get(i).constKey).equals("01")) {
                    btnPhoto.setVisibility(VISIBLE);
                }
                if ((commentView.get(i).constKey).equals("02")) {
                    btnFace.setVisibility(VISIBLE);
                }
                if ((commentView.get(i).constKey).equals("03")) {
                    btnCommonMsg.setVisibility(VISIBLE);
                }
                if ((commentView.get(i).constKey).equals("04")) {
                    btnCommonCoupon.setVisibility(VISIBLE);
                }
                if ((commentView.get(i).constKey).equals(Constant.CHAT_MENU_APPOINTMENT)) {
                    btnAppointment.setVisibility(VISIBLE);
                }
            }
        }

    }

    public void setMoreMenuView(List<CategoryBean> moreView) {
        this.moreMenuList = moreView;
        if (moreView.size() == 0) {
            buttonMore.setVisibility(View.GONE);
        } else {
            buttonMore.setVisibility(View.VISIBLE);
            this.moreMenuList = moreView;
            mGridViewAdapter.setData(moreMenuList);
        }
    }

    private void initChatGridView() {
        mGridViewAdapter = new ChatGridViewAdapter(mContext, moreMenuList);
        chatGridView.setAdapter(mGridViewAdapter);
        chatGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIconImageView(null, null);

                if ((moreMenuList.get(position).constKey).equals(Constant.CHAT_MENU_APPOINTMENT_REQUEST)) {
                    specialListener.onAppointmentRequestClicked();
                } else if ((moreMenuList.get(position).constKey).equals("05")) {
                    specialListener.onBegRewordClicked();
                } else if ((moreMenuList.get(position).constKey).equals("06")) {
                    specialListener.onMarketClicked();
                } else if ((moreMenuList.get(position).constKey).equals("07")) {
                    specialListener.periodicalClicked();
                } else if ((moreMenuList.get(position).constKey).equals("08")) {
                    specialListener.onPreferenceClicked();
                } else if ((moreMenuList.get(position).constKey).equals("09")) {
                    specialListener.onGameClicked();
                } else {
                    specialListener.onLocationClicked();
                }
            }
        });
    }

    private void initCommentMessageView(View view) {
        indicatorLeft.setEnabled(true);
        indicatorRight.setEnabled(false);

        mFragmentOne = CommonMsgFragmentOne.getInstance(new CommonMsgOnClickInterface() {
            @Override
            public void onMsgClickListener(String s) {
                view.setSelected(false);
                if (specialListener != null) {
                    specialListener.onCommonMessageClicked(s);
                }
                ThreadPoolManager.postToUIDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCommonMsgLayout.getVisibility() == View.VISIBLE) {
                            mCommonMsgLayout.setVisibility(View.GONE);
                        }
                    }
                }, 50);
            }
        });
        mFragmentTwo = CommonMsgFragmentTwo.getInstance(new CommonMsgOnClickInterface() {
            @Override
            public void onMsgClickListener(String s) {
                view.setSelected(false);
                if (specialListener != null) {
                    specialListener.onCommonMessageClicked(s);
                }
                ThreadPoolManager.postToUIDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCommonMsgLayout.getVisibility() == View.VISIBLE) {
                            mCommonMsgLayout.setVisibility(View.GONE);
                        }
                    }
                }, 50);
            }
        });

        fragmentsViewPagerList = new ArrayList<>();
        fragmentsViewPagerList.add(mFragmentOne);
        fragmentsViewPagerList.add(mFragmentTwo);
        commentMessageView = (ViewPager) findViewById(R.id.comment_msg_view);
        commentMessageView.setAdapter(new FragmentPagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                return fragmentsViewPagerList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentsViewPagerList.size();
            }
        });
        commentMessageView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    indicatorLeft.setEnabled(true);
                    indicatorRight.setEnabled(false);
                } else {
                    indicatorLeft.setEnabled(false);
                    indicatorRight.setEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initEmojicon() {
        emojiconMenu.setEmojiconMenuListener(new EmojiconMenu.EmojiconMenuListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                int index = mEditText.getSelectionStart();
                Editable edit = mEditText.getEditableText();
                edit.insert(index, SmileUtils.getSmiledText(mContext, emojicon.getEmojiText()));
            }
        });
        emojiconMenu.init(Arrays.asList(DefaultEmojiconDatas.getData()));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (inputMenuListener != null) {
                    String s = mEditText.getText().toString();
                    mEditText.setText("");
                    inputMenuListener.onSendMessage(s);
                }
                break;
            case R.id.btn_set_mode_voice:
                setModeVoice();
                break;
            case R.id.btn_set_mode_keyboard:
                setModeKeyboard();
                break;
            case R.id.edit_message:
                initIconImageView(null, null);
                break;
            case R.id.btn_photo:
                if (specialListener != null) {
                    specialListener.onPickPictureClicked();
                }
                initIconImageView(btnPhoto, inputImagePhoto);
                break;
            case R.id.rl_face:
                initIconImageView(btnFace, inputImageFace);
                break;
            case R.id.btn_common_msg:
                initIconImageView(btnCommonMsg, inputImageCommonMsg);
                break;
            case R.id.btn_common_coupon:
                if (specialListener != null) {
                    specialListener.onCouponClicked();
                }
                initIconImageView(btnCommonCoupon, inputImageCoupon);
                break;
            case R.id.btn_appointment:
                if (specialListener != null) {
                    specialListener.onAppointmentClicked();
                }
                break;
            case R.id.btn_more:
                initIconImageView(buttonMore, inputImageMore);
                break;

        }

    }

    public void initIconImageView(View parentView, View childrenView) {

        if (parentView == null || childrenView == null) {
            for (int i = 0; i < imageList.size(); i++) {
                imageList.get(i).setSelected(false);
            }
            emojiconMenu.setVisibility(View.GONE);
            chatGridView.setVisibility(View.GONE);
            mCommonMsgLayout.setVisibility(View.GONE);
            return;
        }
        hideKeyboard();
        if (childrenView.isSelected()) {
            for (int i = 0; i < imageList.size(); i++) {
                imageList.get(i).setSelected(false);
            }
        } else {
            for (int i = 0; i < imageList.size(); i++) {
                imageList.get(i).setSelected(false);
            }
            if (null != childrenView) {
                childrenView.setSelected(true);
            }
        }

        switch (parentView.getId()) {
            case R.id.btn_photo:
                emojiconMenu.setVisibility(View.GONE);
                chatGridView.setVisibility(View.GONE);
                mCommonMsgLayout.setVisibility(View.GONE);
                break;
            case R.id.rl_face:
                chatGridView.setVisibility(View.GONE);
                mCommonMsgLayout.setVisibility(View.GONE);
                if (childrenView.isSelected()) {
                    emojiconMenu.setVisibility(View.VISIBLE);
                } else {
                    emojiconMenu.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_common_msg:
                hideKeyboard();
                emojiconMenu.setVisibility(View.GONE);
                chatGridView.setVisibility(View.GONE);
                if (childrenView.isSelected()) {
                    mCommonMsgLayout.setVisibility(View.VISIBLE);
                    initCommentMessageView(inputImageCommonMsg);
                } else {
                    mCommonMsgLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_common_coupon:
                emojiconMenu.setVisibility(View.GONE);
                chatGridView.setVisibility(View.GONE);
                mCommonMsgLayout.setVisibility(View.GONE);
                break;
            case R.id.btn_more:
                emojiconMenu.setVisibility(View.GONE);
                mCommonMsgLayout.setVisibility(View.GONE);
                if (childrenView.isSelected()) {
                    chatGridView.setVisibility(View.VISIBLE);
                } else {
                    chatGridView.setVisibility(View.GONE);
                }
                break;
        }

    }

    @Override
    public void onEmojiconInputEvent(CharSequence emojiContent) {
        mEditText.append(emojiContent);
    }

    @Override
    public void onEmojiconDeleteEvent() {
        if (!TextUtils.isEmpty(mEditText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            mEditText.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onExtendMenuContainerHide() {

    }

    @Override
    public void onTextInsert(CharSequence text) {

    }

    @Override
    public EditText getEditText() {
        return null;
    }

    /**
     * show voice icon when speak bar is touched
     */
    protected void setModeVoice() {
        hideKeyboard();
        initIconImageView(null, null);
        editTextLayout.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.GONE);
        buttonSetModeKeyBoard.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
    }

    protected void setModeKeyboard() {
        initIconImageView(null, null);
        editTextLayout.setVisibility(View.VISIBLE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        buttonSetModeKeyBoard.setVisibility(View.GONE);
        buttonPressToSpeak.setVisibility(View.GONE);
        mEditText.requestFocus();
    }


    public void setChatInputMenuListener(ChatInputMenuListener listener) {
        this.inputMenuListener = listener;
    }

    public void setChatSentSpecialListener(ChatSentSpecialListener listener) {
        this.specialListener = listener;
    }

    public interface ChatInputMenuListener {
        /**
         * when send message button pressed
         *
         * @param content message content
         */
        void onSendMessage(String content);

        /**
         * when speak button is touched
         *
         * @param v
         * @param event
         * @return
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);
    }

    public interface ChatSentSpecialListener {
        /**
         * 图片
         */
        void onPickPictureClicked();

        /**
         * 常用语句
         */
        void onCommonMessageClicked(String s);


        /**
         * 发送券
         */
        void onCouponClicked();

        /**
         * 求打赏
         */
        void onBegRewordClicked();

        /**
         * 营销活动
         */
        void onMarketClicked();

        /**
         * 电子期刊
         */
        void periodicalClicked();

        /**
         * 个人名片(不用)
         */
        void onCardClicked();

        /**
         * 特惠商城
         */
        void onPreferenceClicked();

        /**
         * 游戏
         */
        void onGameClicked();

        /**
         * 位置
         */
        void onLocationClicked();

        /**
         * 预约信息
         */
        void onAppointmentClicked();

        /**
         * 求预约
         */
        void onAppointmentRequestClicked();
    }

}

