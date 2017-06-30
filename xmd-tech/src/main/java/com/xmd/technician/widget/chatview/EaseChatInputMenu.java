package com.xmd.technician.widget.chatview;


import android.Manifest;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.xmd.chat.ChatConstants;
import com.xmd.technician.Adapter.ChatGridViewAdapter;
import com.xmd.technician.R;
import com.xmd.technician.chat.DefaultEmojiconDatas;
import com.xmd.technician.chat.Emojicon;
import com.xmd.technician.chat.utils.SmileUtils;
import com.xmd.technician.common.CommonMsgOnClickInterface;
import com.xmd.technician.common.Utils;
import com.xmd.technician.permission.CheckBusinessPermission;
import com.xmd.technician.permission.PermissionConstants;
import com.xmd.technician.widget.ClearableEditText;
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
    private ClearableEditText mEditText;
    private View buttonSetModeKeyBoard;
    private LinearLayout editTextLayout;
    private View buttonSetModeVoice, buttonSend, buttonPressToSpeak;
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
    private List<MoreMenuItem> moreMenuList = new ArrayList<>();
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
        mEditText = (ClearableEditText) mView.findViewById(R.id.edit_message);
        buttonSend = mView.findViewById(R.id.btn_send);
        editTextLayout = (LinearLayout) mView.findViewById(R.id.edit_text_layout);
        emojiconMenu = (EmojiconMenu) mView.findViewById(R.id.emojicon_menu_container);


        mCommonMsgLayout = findViewById(R.id.common_msg_layout);
        commentMessageView = (ViewPager) findViewById(R.id.comment_msg_view);
        indicatorRight = (ImageView) findViewById(R.id.round_indicator_right);
        indicatorLeft = (ImageView) findViewById(R.id.round_indicator_left);

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

        inputImagePhoto.setVisibility(GONE);
        inputImageFace.setVisibility(GONE);
        inputImageCommonMsg.setVisibility(GONE);
        inputImageCoupon.setVisibility(GONE);
        inputImageAppointment.setVisibility(GONE);
        inputImageMore.setVisibility(GONE);

        buttonSetModeVoice.setOnClickListener(this);
        buttonSetModeKeyBoard.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
        mEditText.setOnClickListener(this);


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

    public void initWidthChatRole(String role) {
        switch (role) {
            case ChatConstants.CHAT_ROLE_MGR:
                initMenuPicture();
                initMenuEmoji();
                break;
            case ChatConstants.CHAT_ROLE_TECH:
                initMenuPicture();
                initMenuEmoji();
                initMenuCreditGames();
                break;
            default:
                initMenuPicture();
                initMenuEmoji();
                initMenuFastReply();
                initMenuCoupon();
                initMenuOrder();
                initMenuOrderRequst();
                initMenuRewardRequest();
                initMenuActivity();
                initMenuJournal();
                initMenuMall();
                initMenuCreditGames();
                initMenuLocation();
                break;
        }
        initMoreMenu();
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_PICTURE)
    public void initMenuPicture() {
        inputImagePhoto.setVisibility(VISIBLE);
        inputImagePhoto.setOnClickListener(this);
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_EMOJI)
    public void initMenuEmoji() {
        inputImageFace.setVisibility(VISIBLE);
        inputImageFace.setOnClickListener(this);
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

    @CheckBusinessPermission(PermissionConstants.MESSAGE_FAST_REPLY)
    public void initMenuFastReply() {
        inputImageCommonMsg.setVisibility(VISIBLE);
        inputImageCommonMsg.setOnClickListener(this);
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_COUPON)
    public void initMenuCoupon() {
        inputImageCoupon.setVisibility(VISIBLE);
        inputImageCoupon.setOnClickListener(this);
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_ORDER)
    public void initMenuOrder() {
        inputImageAppointment.setVisibility(VISIBLE);
        inputImageAppointment.setOnClickListener(this);
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_ORDER_REQUEST)
    public void initMenuOrderRequst() {
        moreMenuList.add(new MoreMenuItem("求预约", R.drawable.ic_order_request, new OnClickListener() {
            @Override
            public void onClick(View v) {
                specialListener.onAppointmentRequestClicked();
            }
        }));
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_REWARD)
    public void initMenuRewardRequest() {
        moreMenuList.add(new MoreMenuItem("求打赏", R.drawable.chat_pay_icon_bg, new OnClickListener() {
            @Override
            public void onClick(View v) {
                specialListener.onBegRewordClicked();
            }
        }));
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_ACTIVITY)
    public void initMenuActivity() {
        moreMenuList.add(new MoreMenuItem("营销活动", R.drawable.chat_market_icon_bg, new OnClickListener() {
            @Override
            public void onClick(View v) {
                specialListener.onActivityClicked();
            }
        }));
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_JOURNAL)
    public void initMenuJournal() {
        moreMenuList.add(new MoreMenuItem("电子期刊", R.drawable.chat_periodical_icon_bg, new OnClickListener() {
            @Override
            public void onClick(View v) {
                specialListener.sendJournalClicked();
            }
        }));
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_MALL_INFO)
    public void initMenuMall() {
        moreMenuList.add(new MoreMenuItem("特惠商城", R.drawable.chat_preference_icon_bg, new OnClickListener() {
            @Override
            public void onClick(View v) {
                specialListener.onSendMallInfoClicked();
            }
        }));
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_PLAY_CREDIT_GAME)
    public void initMenuCreditGames() {
        moreMenuList.add(new MoreMenuItem("积分游戏", R.drawable.chat_game_icon_bg, new OnClickListener() {
            @Override
            public void onClick(View v) {
                specialListener.onGameClicked();
            }
        }));
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_LOCATION)
    public void initMenuLocation() {
        moreMenuList.add(new MoreMenuItem("会所位置", R.drawable.chat_position_icon_bg, new OnClickListener() {
            @Override
            public void onClick(View v) {
                specialListener.onLocationClicked();
            }
        }));
    }


    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public static class MoreMenuItem {
        public String name;
        public int iconId;
        public OnClickListener listener;

        public MoreMenuItem(String name, int iconId, OnClickListener listener) {
            this.name = name;
            this.iconId = iconId;
            this.listener = listener;
        }
    }

    public void initMoreMenu() {
        inputImageMore.setOnClickListener(this);
        if (moreMenuList.size() == 0) {
            inputImageMore.setVisibility(View.GONE);
        } else {
            inputImageMore.setVisibility(View.VISIBLE);
            mGridViewAdapter = new ChatGridViewAdapter(mContext, moreMenuList);
            chatGridView.setAdapter(mGridViewAdapter);
        }
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (inputMenuListener != null) {
                    String s = mEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(s)) {
                        Utils.makeShortToast(mContext, "发送内容不能为空");
                    } else {
                        inputMenuListener.onSendMessage(s);
                        mEditText.setText("");
                    }
                }
                break;
            case R.id.btn_set_mode_voice:
                setModeVoice();
                break;
            case R.id.btn_set_mode_keyboard:
                setModeKeyboard();
                break;
            case R.id.edit_message:
                showSubMenu(null);
                break;
            case R.id.input_img_photo:
                if (specialListener != null) {
                    specialListener.onPickPictureClicked();
                }
                showSubMenu(inputImagePhoto);
                break;
            case R.id.input_img_face:
                showSubMenu(inputImageFace);
                break;
            case R.id.input_img_common_msg:
                showSubMenu(inputImageCommonMsg);
                break;
            case R.id.input_img_coupon:
                if (specialListener != null) {
                    specialListener.onCouponClicked();
                }
                showSubMenu(inputImageCoupon);
                break;
            case R.id.input_img_appointment:
                if (specialListener != null) {
                    specialListener.onAppointmentClicked();
                }
                break;
            case R.id.input_img_more:
                showSubMenu(inputImageMore);
                break;

        }

    }

    public void showSubMenu(View menuView) {
        if (menuView == null) {
            for (int i = 0; i < imageList.size(); i++) {
                imageList.get(i).setSelected(false);
            }
            emojiconMenu.setVisibility(View.GONE);
            chatGridView.setVisibility(View.GONE);
            mCommonMsgLayout.setVisibility(View.GONE);
            return;
        }
        hideKeyboard();
        if (menuView.isSelected()) {
            for (int i = 0; i < imageList.size(); i++) {
                imageList.get(i).setSelected(false);
            }
            menuView.setSelected(false);
        } else {
            for (int i = 0; i < imageList.size(); i++) {
                imageList.get(i).setSelected(false);
            }
            menuView.setSelected(true);
        }

        switch (menuView.getId()) {
            case R.id.input_img_photo:
                emojiconMenu.setVisibility(View.GONE);
                chatGridView.setVisibility(View.GONE);
                mCommonMsgLayout.setVisibility(View.GONE);
                break;
            case R.id.input_img_face:
                chatGridView.setVisibility(View.GONE);
                mCommonMsgLayout.setVisibility(View.GONE);
                if (menuView.isSelected()) {
                    emojiconMenu.setVisibility(View.VISIBLE);
                } else {
                    emojiconMenu.setVisibility(View.GONE);
                }
                break;
            case R.id.input_img_common_msg:
                hideKeyboard();
                emojiconMenu.setVisibility(View.GONE);
                chatGridView.setVisibility(View.GONE);
                if (menuView.isSelected()) {
                    mCommonMsgLayout.setVisibility(View.VISIBLE);
                    initCommentMessageView(inputImageCommonMsg);
                } else {
                    mCommonMsgLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.input_img_coupon:
                emojiconMenu.setVisibility(View.GONE);
                chatGridView.setVisibility(View.GONE);
                mCommonMsgLayout.setVisibility(View.GONE);
                break;
            case R.id.input_img_more:
                emojiconMenu.setVisibility(View.GONE);
                mCommonMsgLayout.setVisibility(View.GONE);
                if (menuView.isSelected()) {
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
        showSubMenu(null);
        editTextLayout.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.GONE);
        buttonSetModeKeyBoard.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
    }

    protected void setModeKeyboard() {
        showSubMenu(null);
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
        void onActivityClicked();

        /**
         * 电子期刊
         */
        void sendJournalClicked();

        /**
         * 个人名片(不用)
         */
        void onCardClicked();

        /**
         * 特惠商城
         */
        void onSendMallInfoClicked();

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

