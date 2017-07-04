package com.wasseemb.applock;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Wasseem on 11/11/2016.
 */

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //if (Utils.hasActionBar())
        //   getActionBar().setDisplayHomeAsUpEnabled(true);
        Helper mHelper = new Helper(getApplicationContext());
        if(mHelper.getSharedPrefBol(SettingsKeys.INTRO_PREFRENCE_KEY)) {
            startActivity(new Intent(this, PackageListActivity.class));
        }
        else {
            startActivity(new Intent(this, MainIntroActivity.class));
        }


    }
}
