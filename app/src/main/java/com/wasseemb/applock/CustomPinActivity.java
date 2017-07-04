package com.wasseemb.applock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;
import com.github.orangegangsters.lollipin.lib.managers.LockManager;
import com.github.orangegangsters.lollipin.lib.views.KeyboardButtonView;
import com.github.orangegangsters.lollipin.lib.views.KeyboardView;
import com.github.orangegangsters.lollipin.lib.views.PinCodeRoundView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Set;


/**
 * Created by Wasseem on 11/05/2016.
 */
public class CustomPinActivity extends AppLockActivity {
    Helper mHelper = null;
    Set<String> nSet = null;


    @Override
    public void showForgotDialog() {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_pin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new Helper(getApplicationContext());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getContentView());
        initLayout(getIntent());

        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_id));

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("ED3EA9BA2FE370F4B3679A7779F7E5FC")
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initLayout(Intent intent) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            //Animate if greater than 2.3.3
            overridePendingTransition(R.anim.nothing, R.anim.nothing);
        }
        Intent mIntent = intent;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            mType = extras.getInt(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
        }

        mLockManager = LockManager.getInstance();
        mPinCode = "";
        mOldPinCode = "";

        enableAppLockerIfDoesNotExist();
        mLockManager.getAppLock().setPinChallengeCancelled(false);

        mStepTextView = (TextView) this.findViewById(R.id.pin_code_step_textview);
        mPinCodeRoundView = (PinCodeRoundView) this.findViewById(R.id.pin_code_round_view);
        mPinCodeRoundView.setPinLength(this.getPinLength());
        mForgotTextView = (TextView) this.findViewById(R.id.pin_code_forgot_textview);
        mForgotTextView.setOnClickListener(this);
        mKeyboardView = (KeyboardView) this.findViewById(R.id.pin_code_keyboard_view);
        mKeyboardView.setKeyboardButtonClickedListener(this);
        int logoId = mLockManager.getAppLock().getLogoId();
        ImageView logoImage = ((ImageView) findViewById(R.id.pin_code_logo_imageview));
        if (logoId != AppLock.LOGO_ID_NONE) {
            logoImage.setVisibility(View.VISIBLE);
            logoImage.setImageResource(logoId);
        }
        if (mIntent != null && mIntent.getStringExtra(SettingsKeys.PREVIOUS_ACTIVITY) != null && mIntent.getStringExtra(SettingsKeys.PREVIOUS_ACTIVITY).equals(SettingsKeys.EXTERNAL_ACTIVITY)) {
            String appName = mIntent.getStringExtra(SettingsKeys.APP_LAUNCH);
            try {

                Drawable icon = getPackageManager().getApplicationIcon(appName);
                logoImage.setImageDrawable(icon);
                if (mHelper.getSharedPrefBol(SettingsKeys.DYNAMIC_PREFERENCE_KEY)) {

                    Bitmap mBitmap = Helper.drawableToBitmap(icon);
                    RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
                    int intColor = Helper.getDominantColor(mBitmap);

                    if (mHelper.blackOrWhite(icon) == Color.DKGRAY) {
                        for (int i = 0; i < SettingsKeys.PIN_CODE_BUTTON.length; i++) {
                            KeyboardButtonView mKeyBoardButtonView = (KeyboardButtonView) mKeyboardView.findViewById(SettingsKeys.PIN_CODE_BUTTON[i]);
                            ((TextView) mKeyBoardButtonView.findViewById(R.id.keyboard_button_textview)).setTextColor(Color.DKGRAY);
                        }
                        KeyboardButtonView mKeyBoardButtonView = (KeyboardButtonView) mKeyboardView.findViewById(R.id.pin_code_button_clear);
                        ImageView mImageViewX = ((ImageView) mKeyBoardButtonView.findViewById(R.id.keyboard_button_imageview));
                        Drawable image = mImageViewX.getDrawable();
                        ColorFilter filter = new LightingColorFilter(Color.DKGRAY, Color.DKGRAY);
                        image.setColorFilter(filter);
                        mImageViewX.setImageDrawable(image);
                        mForgotTextView.setTextColor(Color.DKGRAY);
                        mStepTextView.setTextColor(Color.DKGRAY);
                        Drawable drawable = getResources().getDrawable(R.drawable.pin_code_round_full);
                        drawable.setColorFilter(mHelper.darkenColor(intColor), PorterDuff.Mode.SRC_ATOP);
                        mPinCodeRoundView.setFullDotDrawable(drawable);

                    } else {

                        for (int i = 0; i < SettingsKeys.PIN_CODE_BUTTON.length; i++) {
                            KeyboardButtonView mKeyBoardButtonView = (KeyboardButtonView) mKeyboardView.findViewById(SettingsKeys.PIN_CODE_BUTTON[i]);
                            ((TextView) mKeyBoardButtonView.findViewById(R.id.keyboard_button_textview)).setTextColor(Color.WHITE);
                        }
                        KeyboardButtonView mKeyBoardButtonView = (KeyboardButtonView) mKeyboardView.findViewById(R.id.pin_code_button_clear);
                        ImageView mImageViewX = ((ImageView) mKeyBoardButtonView.findViewById(R.id.keyboard_button_imageview));
                        Drawable image = mImageViewX.getDrawable();
                        ColorFilter filter = new LightingColorFilter(Color.WHITE, Color.WHITE);
                        image.setColorFilter(filter);
                        mImageViewX.setImageDrawable(image);
                        mForgotTextView.setTextColor(Color.WHITE);
                        mStepTextView.setTextColor(Color.WHITE);
                        Drawable drawable = getResources().getDrawable(R.drawable.pin_code_round_full);
                        drawable.setColorFilter(mHelper.darkenColor(intColor), PorterDuff.Mode.SRC_ATOP);
                        mPinCodeRoundView.setFullDotDrawable(drawable);

                    }


                    mRelativeLayout.setBackgroundColor(intColor);
                }
            } catch (Exception e) {

            }


        }


        mForgotTextView.setText(getForgotText());
        mForgotTextView.setVisibility(mLockManager.getAppLock().shouldShowForgot() ? View.VISIBLE : View.GONE);
        setStepText();


    }

    @SuppressWarnings("unchecked")
    private void enableAppLockerIfDoesNotExist() {
        try {
            if (mLockManager.getAppLock() == null) {
                mLockManager.enableAppLock(this, getCustomAppLockActivityClass());
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void setStepText() {
        mStepTextView.setText(getStepText(mType));
    }


    @Override
    public void onPinFailure(int attempts) {
    }

    @Override
    public void onPinSuccess(int attempts) {
        mHelper = new Helper(getApplicationContext());

        Intent mIntent = getIntent();

        if (mIntent != null && mIntent.getStringExtra(SettingsKeys.PREVIOUS_ACTIVITY) != null && mIntent.getStringExtra(SettingsKeys.PREVIOUS_ACTIVITY).equals(SettingsKeys.EXTERNAL_ACTIVITY)) {
            String appName = mIntent.getStringExtra(SettingsKeys.APP_LAUNCH);
            Log.i("PinSuccess", "Boom");
            mHelper.editSharedPref(SettingsKeys.TEMP_UNLOCK, true);
            Log.i("TEMP", "Temporary unlock is enabled");


            mHelper.setCurrentActivity(appName);
            mHelper.startActivity(appName);


//        Log.d("PinSuccess",appName);

        }


    }

}
