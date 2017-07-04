package com.wasseemb.applock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Wasseem on 12/05/2016.
 */
public class Helper {
    private SharedPreferences sharedPrefs;
    private Context mContext = null;
    public String TAG = "DEBUGTAG";




    public Helper(Context mContext) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.mContext = mContext;
    }



    public void editSharedPref(String key,boolean value)
    {
        sharedPrefs.edit().putBoolean(key,value).commit();
    }
    public void editSharedPref(String key,String value)
    {
        sharedPrefs.edit().putString(key,value).commit();
    }

    public boolean getSharedPrefBol(String key)
    {
        return sharedPrefs.getBoolean(key,false);
    }



    public void addToHashSet(Set<String> mSet,String appName,String key) {

        mSet = sharedPrefs.getStringSet(key,new HashSet<String>());
        Set<String> in = new HashSet<String>(mSet);
        in.add(appName);
        sharedPrefs.edit().putStringSet(key,in).commit();

    }
    public void removeFromHashSet(Set<String> mSet,String appName,String key) {

        mSet = sharedPrefs.getStringSet(key,new HashSet<String>());
        Set<String> in = new HashSet<String>(mSet);
        in.remove(appName);
        sharedPrefs.edit().putStringSet(key,in).commit();

    }


    public void setCurrentActivity(String currentActivity)
    {
        sharedPrefs.edit().putString(SettingsKeys.CURRENT_ACTIVITY,currentActivity).commit();
    }

    public String getCurrentActivity()
    {
        return sharedPrefs.getString(SettingsKeys.CURRENT_ACTIVITY,null);
    }

    public Set<String> getHashSet(String key)
    {

        return sharedPrefs.getStringSet(key,new HashSet<String>());

    }

    public void clearPreference(String key)
    {
        sharedPrefs.edit().remove(key).commit();

    }

    public void startActivity(String appName)
    {
        PackageManager packageManager;
        packageManager = mContext.getPackageManager();
        Intent intent = packageManager
                .getLaunchIntentForPackage(appName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public Drawable getIcon(String appName)
    {
        try {
            Drawable icon = mContext.getPackageManager().getApplicationIcon(appName);
            return icon;
        }
        catch (Exception e)
        {

        }

        return mContext.getResources().getDrawable(R.drawable.ic_launcher);


    }
    public void setLogoId(int logoId) {
        sharedPrefs.edit().putInt(SettingsKeys.LOGO_ID_PREFERENCE_KEY, logoId).commit();
    }
    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public int blackOrWhite (Drawable drawable) {
        Bitmap mBitmap = drawableToBitmap(drawable);
        int intColor = getDominantColor(mBitmap);
        int red = Color.red(intColor);
        int blue = Color.blue(intColor);
        int green = Color.green(intColor);

        if ((red * 0.299 + green * 0.587 + blue * 0.114) > 186)
            return Color.DKGRAY;
        else
            return Color.WHITE;

    }

    public int darkenColor (int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }
    public boolean isEnabled(String packageName, String activityName) {
        if (activityName == null) {
            String keyName = getKeyName(packageName, null, SettingsKeys.IS_ACTIVE);
            return getBoolean(keyName, true);
        } else {
            String keyName = getKeyName(packageName, activityName, SettingsKeys.IS_ACTIVE);
            return getBoolean(keyName, isEnabled(packageName, null));
        }
    }
    public static String getKeyName(String packageName, String activityName, String keyName) {
        if (activityName == null) {
            return packageName + "/" + keyName;
        } else {
            String processedActivityName = removePackageName(activityName, packageName);
            return packageName + "." + processedActivityName + "/" + keyName;
        }
    }
    public boolean getBoolean(String key, boolean defaultValue) {
        boolean returnResult = defaultValue;
        if (sharedPrefs != null)
            returnResult = sharedPrefs.getBoolean(key, defaultValue);

        return returnResult;
    }
    public String getString(String key) {
        String returnResult = "";
        if (sharedPrefs != null)
             returnResult = sharedPrefs.getString(key,"");


        return returnResult;
    }
    public static String removePackageName(String string, String packageName) {
        return string.replace(packageName + ".", "");
    }
    public boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + ApplicationService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
           // Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
           // Log.e(TAG, "Error finding setting, default accessibility to not found: "
                 //   + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
           // Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                  //  Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                     //   Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            //Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
