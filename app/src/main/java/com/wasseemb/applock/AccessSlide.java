package com.wasseemb.applock;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;

public class AccessSlide extends SlideFragment {


    private Button mButton;
    private ImageView mImageView;
    public Helper mHelper;

    private boolean loggedIn = false;




    public AccessSlide() {
        // Required empty public constructor
    }

    public static AccessSlide newInstance() {
        return new AccessSlide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.access_fragment, container, false);
        mHelper = new Helper(getContext());
        mButton = (Button) root.findViewById(R.id.accButton);
        mImageView = (ImageView)root.findViewById(R.id.imView);
        mButton.setEnabled(!mHelper.isAccessibilitySettingsOn());
        mImageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent,0);

                if(mHelper.isAccessibilitySettingsOn())
                {
                    mButton.setEnabled(false);
                    mButton.setText("AppLock service accessibility is enabled");
                }

            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHelper = new Helper(getContext());
        if(mHelper.isAccessibilitySettingsOn())
        {
            mButton.setEnabled(false);
            mButton.setText("AppLock service accessibility is enabled");
        }
    }

    @Override
    public boolean canGoForward() {
        mHelper = new Helper(getContext());
        return mHelper.isAccessibilitySettingsOn();
    }


}