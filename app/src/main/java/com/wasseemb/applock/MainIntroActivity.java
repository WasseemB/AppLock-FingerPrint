package com.wasseemb.applock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.heinrichreimersoftware.materialintro.slide.Slide;

/**
 * Created by Wasseem on 11/08/2016.
 */
public class MainIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Standard slide (like Google's intros)
         */
        setSkipEnabled(false);
        addSlide(new SimpleSlide.Builder()
                .title("AppLock")
                .description("Lock/Unlock your application")
                .image(R.mipmap.ic_launcher)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .build());

        final Slide loginSlide;
            loginSlide = new FragmentSlide.Builder()
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .fragment(AccessSlide.newInstance())
                    .build();
            addSlide(loginSlide);
        addSlide(new SimpleSlide.Builder()
                .title("Set Pin")
                .description("Set unlock code")
                .image(R.mipmap.ic_launcher)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .buttonCtaLabel("Set Code")
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainIntroActivity.this, CustomPinActivity.class);
                        intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                        startActivityForResult(intent, 11);
                    }
                })
                .build());

//        final Slide mailSlide;
//        mailSlide = new FragmentSlide.Builder()
//                .background(R.color.colorPrimary)
//                .backgroundDark(R.color.colorPrimaryDark)
//                .fragment(MailSlide.newInstance())
//                .build();
//        addSlide(mailSlide);


        /**
         * Custom fragment slide
         */
//        addSlide(new FragmentSlide.Builder()
//                .background(R.color.colorPrimary)
//                .backgroundDark(R.color.colorPrimaryDark)
//                .fragment(R.layout.slide_into, R.style.Theme_Intro)
//                .build());
    }
    // To check if service is enabled

    @Override
    public void finish() {
        Helper mHelper = new Helper(getApplicationContext());
        mHelper.editSharedPref(SettingsKeys.INTRO_PREFRENCE_KEY,true);
        startActivity(new Intent(MainIntroActivity.this,PackageListActivity.class));
        super.finish();
    }


}
