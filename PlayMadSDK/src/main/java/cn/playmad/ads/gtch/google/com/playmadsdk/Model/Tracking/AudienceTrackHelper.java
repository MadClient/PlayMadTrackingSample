package cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.UUID.V1.MCPTool;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.UUID.V2.ChannelInfo;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.UUID.WalleChannelReader;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Utils.SecurityHelper;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/11/08.
 */

public final class AudienceTrackHelper {

    /**
     * Constructor
     */
    private AudienceTrackHelper() {
        throw new UnsupportedOperationException("AudienceTrackHelper is a helper class, can't be " +
                "initiated");
    }

    /**
     * Get unique device ID
     *
     * @param context a Context object used to access application assets
     * @return String device imei
     */
    @SuppressWarnings("deprecation")
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDID(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                if (SecurityHelper.checkPermissionGranted(context, Manifest.permission.READ_PHONE_STATE)) {
                    return tm.getDeviceId();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get 802.11 Wifi MAC address
     *
     * @param context a Context object used to access application assets
     * @return String MAC address by formate
     */
    public static String getWifiMACAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context
                    .WIFI_SERVICE);
            if (wifiManager != null) {
                if (SecurityHelper.checkPermissionGranted(context, Manifest.permission
                        .ACCESS_WIFI_STATE)) {
                    @SuppressLint("HardwareIds") String address = wifiManager.getConnectionInfo().getMacAddress();
                    if (address != null) {
                        return address.replaceAll(":", "");
                    } else {
                        return null;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get Wifi device Basic Service Set identifier
     *
     * @param context a Context object used to access application assets
     * @return String Wifi BSSID by formate
     */
    public static String getWifiBSSID(Context context) {
        try {
            String bss = null;
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService
                    (Context.WIFI_SERVICE);
            if (wifi != null && SecurityHelper.checkPermissionGranted(context, Manifest.permission
                    .ACCESS_WIFI_STATE)) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info == null) {
                    return null;
                }
                String bssId = info.getBSSID();
                if (bssId != null) {
                    bss = bssId.replace(":", "");
                }
            }
            return bss;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get system identifier(Android ID/SSAID)
     *
     * @param context a Context object used to access application assets
     * @return String System identifier - Android ID(SSAID)
     */
    @SuppressLint("HardwareIds")
    public static String getSystemId(Context context) {
        String sid;
        try {
            sid = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
            if (sid == null || sid.length() == 0 || sid.equals("9774d56d682e549c")) {
                sid = null;
            }
        } catch (Exception e) {
            sid = null;
        }
        return sid;
    }

    /**
     * Get device brand and model
     *
     * @return String device brand+space+model
     */

    public static String getMod() {
        return Build.BRAND + " " + Build.MODEL;
    }

    /**
     * Get Network Type
     *
     * @param context a Context object used to access application assets
     * @return String Custom network type code (-4 Not permission granted, -1 Not network)
     */
    public static String getNetworkType(Context context) {
        try {
            if (SecurityHelper.checkPermissionGranted(context, Manifest.permission
                    .ACCESS_NETWORK_STATE)) {
                ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context
                        .CONNECTIVITY_SERVICE);
                if (connectivity != null) {
                    NetworkInfo info = connectivity.getActiveNetworkInfo();
                    if (info != null && info.isConnected()) {
                        switch (info.getType()) {
                            case ConnectivityManager.TYPE_MOBILE:
                            case ConnectivityManager.TYPE_MOBILE_DUN:
                            case 5: // TYPE_MOBILE_HIPRI
                            case 2: // TYPE_MOBILE_MMS
                            case 3: // TYPE_MOBILE_SUPL
                            {
                                TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context
                                        .TELEPHONY_SERVICE);
                                if (telephony != null) {
                                    switch (telephony.getNetworkType()) {
                                        case TelephonyManager.NETWORK_TYPE_UNKNOWN:// 0
                                            return "1"; // WWAN or UNKNOWN Network
                                        case TelephonyManager.NETWORK_TYPE_GPRS:// 1
                                        case TelephonyManager.NETWORK_TYPE_EDGE:// 2
                                        case TelephonyManager.NETWORK_TYPE_1xRTT:// 7
                                        case TelephonyManager.NETWORK_TYPE_CDMA:// 4
                                        case TelephonyManager.NETWORK_TYPE_IDEN:// api<8 : replace by 11
                                        case 16:// NETWORK_TYPE_GSM
                                            return "2"; // Cellular 2G
                                        case TelephonyManager.NETWORK_TYPE_UMTS:// 3
                                        case TelephonyManager.NETWORK_TYPE_HSDPA:// 8
                                        case TelephonyManager.NETWORK_TYPE_HSUPA:// 9
                                        case TelephonyManager.NETWORK_TYPE_EVDO_0:// 5
                                        case TelephonyManager.NETWORK_TYPE_EVDO_A:// 6
                                        case 12:// NETWORK_TYPE_EVDO_B (api<9:replace by 14)
                                        case 14:// NETWORK_TYPE_EHRPD (api<11:replace by 12)
                                        case TelephonyManager.NETWORK_TYPE_HSPA:// 10
                                        case 15:// NETWORK_TYPE_HSPAP (api<13:replace by 15)
                                        case 17:// NETWORK_TYPE_TD_SCDMA
                                            return "3"; // Cellular 3G
                                        case 13:// NETWORK_TYPE_LTE (api<11:replace by 13)
                                        case 19:// NETWORK_TYPE_LTE_CA
                                            return "4"; // Cellular 4G
                                        case 18:// NETWORK_TYPE_IWLAN
                                            return "0"; // Wi-Fi
                                    }
                                }
                                break;
                            }
                            case ConnectivityManager.TYPE_WIFI:
                            case ConnectivityManager.TYPE_WIMAX:
                            case 17:// TYPE_VPN
                            case 7:// TYPE_BLUETOOTH
                            case 8:// TYPE_DUMMY
                            case 9:// TYPE_ETHERNET
                            case 15:
                                return "0"; // Wi-Fi
                        }
                    }
                }
                return "-1"; // Network no connection
            }
            return "-4";
        } catch (Exception e) {
            return "-1"; // Network no connection
        }
    }

    /**
     * @param context a Context object used to access application assets
     * @return String Mobile network carrier name
     */
    public static String getCarrierName(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String cn = null;
            if (tm != null) {
                cn = tm.getNetworkOperatorName();
                if ("Android".equals(cn)) {
                    cn = null;
                }
            }
            return cn;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get OS version
     *
     * @return String OS version
     */
    public static String getOSVersion() {

        return String.valueOf(Build.VERSION.RELEASE);
    }

    /**
     * Get language and country locale info
     *
     * @return String current language and country
     */
    public static String getLanguageAndCountry() {
        Locale loc = Locale.getDefault();
        return loc.getLanguage() + "_" + loc.getCountry();
    }

    /**
     * Get jailbreak status
     *
     * @return String Jailbreak Status (root 1, unroot 0, error -1)
     */
    public static String getJailbreakStatus() {
        try {
            File binSu = new File("/system/bin/su");
            File xbinSu = new File("/system/xbin/su");
            if (binSu.exists() || xbinSu.exists()) {
                return "1";
            } else {
                return "0";
            }

        } catch (Exception e) {
            return "-1";
        }
    }

    /**
     * Check if the app is running in the device or emulator
     *
     * @param context a Context object used to access application assets
     * @return String device or emulator (device 0, emulator 1)
     */
    public static String getDeviceEmulator(Context context) {
        try {
            String url = "tel:" + "12345678910";
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));
            intent.setAction(Intent.ACTION_DIAL);
            // 是否可以处理跳转到拨号的 Intent
            boolean canResolveIntent = intent.resolveActivity(context.getPackageManager()) != null;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context
                    .TELEPHONY_SERVICE);
            boolean non = false;
            if (tm != null) {
                non = tm.getNetworkOperatorName().toLowerCase().equals("android");
            }
            String serialNumber = getSerialNumber(context);
            @SuppressLint("HardwareIds") boolean result = Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.toLowerCase().contains("vbox")
                    || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || serialNumber.equalsIgnoreCase("unknown")
                    || serialNumber.equalsIgnoreCase("android")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                    || "google_sdk".equals(Build.PRODUCT)
                    || non
                    || !canResolveIntent;
            return result ? "1" : "0";
        } catch (Exception e) {
            return "-1";
        }
    }

    /**
     * @param context a Context object used to access application assets
     * @return serial number of phone
     */
    @SuppressWarnings("deprecation")
    @SuppressLint({"MissingPermission", "HardwareIds"})
    private static String getSerialNumber(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return Build.SERIAL;
        } else {
            if (SecurityHelper.checkPermissionGranted(context, Manifest.permission.READ_PHONE_STATE)) {
                return Build.getSerial();
            }
            return "";
        }
    }

    /**
     * Get app package name
     *
     * @param context a Context object used to access application assets
     * @return String app package name - bundle identifier
     */
    public static String getBundleIdentifier(Context context) {
        return context.getPackageName();
    }

    /**
     * Get app version name and code
     *
     * @param context a Context object used to access application assets
     * @return String app version name and code combination
     */
    public static String getAppVersion(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(), PackageManager
                    .GET_CONFIGURATIONS);
            return pinfo.versionName + "(" + pinfo.versionCode + ")";
        } catch (NameNotFoundException e) {
            return null;
        }
    }

//    /**
//     * @param context a Context object used to access application assets
//     * @return
//     */
//    public static String getUUIDV1(Context context) {
//        File apkFile = new File(context.getPackageResourcePath());
//        try {
//            FileInputStream fileInputStream = new FileInputStream(apkFile);
//            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream
//                    (fileInputStream));
//            ZipEntry zipEntry;
//            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
//                String entryName = zipEntry.getName();
//                if (entryName.contains(Constants.UUID_FLAG)) {
//                    return entryName;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * Get UUID string from APK
     *
     * @param context a Context object used to access application assets
     * @return UUID string
     */
    public static String getUUID(Context context) {
//        String channel = WalleChannelReader.getChannel(context);
        String TAG = "_PLAYMAD_UUID_";
        // MeiTuan Walle Mode
        final ChannelInfo channelInfo = WalleChannelReader.getChannelInfo(context);
        if (channelInfo != null) {
            for (Map.Entry<String, String> entry : channelInfo.getExtraInfo().entrySet()) {
                if (entry.getKey().contains(TAG)) {
                    return entry.getValue();
                }
            }
        }
        String uuid = MCPTool.getChannelId(context, null, null);
        if (uuid != null && uuid.contains(TAG)) {
            // +1 -> "=" string length
            return uuid.substring(uuid.indexOf(TAG) + TAG.length() + 1);
        }
        return uuid;
    }

    /**
     * Get device current UTC timestamp
     *
     * @return timestamp
     */
    public static String getUTC() {
        return String.valueOf(System.currentTimeMillis());
    }
}
