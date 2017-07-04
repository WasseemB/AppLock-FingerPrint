package com.wasseemb.applock;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Wasseem on 10/05/2016.
 */
public class ApplicationService extends AccessibilityService {
    Set<String> mSet;
    Helper mHelper = null;
    private static final int REQUEST_CODE_ENABLE = 11;
    ArrayList<String> keyboardList= null;



    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        keyboardList = new ArrayList<>();
        // Get Keyboardlist
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final List<InputMethodInfo> imis = imm.getInputMethodList();
        for (int i = 0; i < imis.size(); ++i) {
            final InputMethodInfo imi = imis.get(i);
            List<InputMethodSubtype> submethods = imm
                    .getEnabledInputMethodSubtypeList(imi, true);
            for (InputMethodSubtype submethod : submethods) {
                if (submethod.getMode().equals("keyboard")) {
                    keyboardList.add(imi.getPackageName());
                }
            }

        }

        mHelper = new Helper(getApplicationContext());
        String currentActivity = mHelper.getCurrentActivity();

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            ComponentName componentName = new ComponentName(
                    event.getPackageName().toString(),
                    event.getClassName().toString()

            );
            Log.d("Current Package",event.getPackageName().toString());

            //Keyboard activity is not returned thus the ""
            //Application that i do not want applock to activate
            if (!"".equals(componentName.getPackageName()) && !componentName.getPackageName().equals("com.android.systemui") && !"com.wasseemb.applock".equals(componentName.getPackageName())) {

                // No longer unlocked
                if (currentActivity != null && !currentActivity.equals(componentName.getPackageName())) {
                    mHelper.editSharedPref(SettingsKeys.TEMP_UNLOCK, false);


                }
                //Set the current activity
                if (componentName.getPackageName() != null && !keyboardList.contains(componentName.getPackageName())) {

                    mHelper.setCurrentActivity(componentName.getPackageName());

                }

                mSet = mHelper.getHashSet(SettingsKeys.LOCKED_APPS);
                boolean value = mHelper.getSharedPrefBol(SettingsKeys.TEMP_UNLOCK);

                if (!keyboardList.contains(componentName.getPackageName())) {
                    if (value) {

                    } else if (mSet.contains(componentName.getPackageName())) {

                        Intent mIntent = new Intent(this, CustomPinActivity.class);
                        mIntent.putExtra(SettingsKeys.PREVIOUS_ACTIVITY, SettingsKeys.EXTERNAL_ACTIVITY);
                        mIntent.putExtra(SettingsKeys.APP_LAUNCH, componentName.getPackageName());
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                       mHelper.setLogoId(R.mipmap.ic_launcher);
                        startActivity(mIntent);

                    }
                }


            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }
}
