package cn.playmad.ads.gtch.google.com.playmadsdk.Model.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/11/20.
 */

public final class SecurityHelper {
    /**
     * Constructor
     */
    private SecurityHelper() {
    }

    /**
     * Check target sdk version
     *
     * @param context a Context object used to access application assets
     * @return int target sdk version
     */
    public final static int checkTargetSdkVersion(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Check permission granted status
     *
     * @param context    a Context object used to access application assets
     * @param permission check permission granted
     * @return boolean Yes or No have permission granted
     */
    public final static boolean checkPermissionGranted(Context context, String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkTargetSdkVersion(context) >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can use Context#checkSelfPermission
                result = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker
                        .PERMISSION_GRANTED;
            }
        } else {
            if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return result;
    }

    /**
     * Encoding for network transmission security
     *
     * @param date Need to secure send of data
     * @return String encoded date
     */
    public final static String transferEncode(final String date) {
        if (date == null || date.length() <= 0) {
            return null;
        }
        String encodedDate = Base64.encodeToString(date.getBytes(), Base64.NO_WRAP);
        StringBuffer newStr = new StringBuffer();
        char[] arrStr = encodedDate.toCharArray();
        for (int i = 0, j = 1; i < arrStr.length - 1 && j <= arrStr.length; i += 2, j += 2) {
            String a = String.valueOf(arrStr[i]);
            String b = String.valueOf(arrStr[j]);
            newStr.append(b);
            newStr.append(a);
        }
        if (null != arrStr && arrStr.length % 2 != 0) {
            newStr.append(String.valueOf(arrStr[arrStr.length - 1]));
        }
        return newStr.toString();
    }

    /**
     * @param urlParams
     * @param key
     * @param value
     */
    public final static void appendParams(StringBuffer urlParams, String key, String value) {
        if (value != null && value.length() > 0) {
            try {
                urlParams.append(!urlParams.toString().contains("?") ? "?" : "&").append
                        (URLEncoder.encode(key,
                                "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public final static void isFirstOpen(Context context) {
        SharedPreferences store = context.getSharedPreferences("PlayMadPrefsKey", MODE_PRIVATE);

    }
}
