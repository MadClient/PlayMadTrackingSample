package cn.playmad.ads.gtch.google.com.playmadsdk.Model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Database.DatabaseHelper;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Database.DatabaseListener;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Http.HttpEngine;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Http.HttpResponseListener;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.AdvertisingIdClient;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.AdvertisingIdListener;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.AdvertisingIdManager;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.AudienceTrackHelper;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Utils.SecurityHelper;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/11/06.
 */

public class EventTaskModelImpl implements EventTaskModel<ContentValues>, AdvertisingIdListener,
        HttpResponseListener, DatabaseListener {

    /**
     * Constant
     */
    private static final String PREF_NAME = "cn.playmad.pref";
    private static final String DB_NAME = "cn.playmad.db";
    private static final int DB_VERSION = 1;
    private static final String EVENTS_TABLE = "events";
    private static final String PRIMARY_KEY_NAME = "_id";
    private static final String[] DB_TABLES = {"create table " + EVENTS_TABLE + "(" + PRIMARY_KEY_NAME + " integer " +
            "primary key " +
            "autoincrement,sid varchar(64),cat varchar(64),act varchar(64),lab varchar(64),val numeric(16,3),utc " +
            "varchar(20))"};
    private static final String PAGING_QUERY_NAMBER = "10";


    /**
     * Member variables
     */
    private Map<String, String> mAudienceInfo;
    private EventTaskListener mSendEventListener;
    private Context mContext;
    private DatabaseHelper dbh;
    private HttpEngine httpEngine;
    private static ConcurrentLinkedQueue<ContentValues> mActionEventQueue;

    /**
     * Construtor
     */
    public EventTaskModelImpl(Context context) {
        mContext = context;
        mAudienceInfo = new HashMap<>();
        new AdvertisingIdManager(this).execute(context);
        mActionEventQueue = new ConcurrentLinkedQueue<>();
        dbh = new DatabaseHelper(context, DB_NAME, DB_VERSION, DB_TABLES);
        dbh.query(this, EVENTS_TABLE, null, null, null, null, null, null, PAGING_QUERY_NAMBER);
        setAudienceInfo(context, mAudienceInfo);
        httpEngine = new HttpEngine();
    }

    /**
     * Send action event via http engine
     *
     * @param url           http request url address
     * @param requestHeader http request header
     * @param listener      http response listener
     */
    @Override
    public void sendEvent(String url, Map<String, List<String>> requestHeader, EventTaskListener listener) {
        this.mSendEventListener = listener;
        httpEngine.httpGetRequest(url, requestHeader, this);
    }

    /**
     * Add action event element to cache queue
     *
     * @param contentValues action event base on <code>ContentValues</code>
     */
    @Override
    public void addActionEventToCache(ContentValues contentValues) {
        mActionEventQueue.offer(contentValues);
        dbh.insert(this, EVENTS_TABLE, null, contentValues);
    }

    /**
     * Get action event element from cache queue
     */
    @Override
    public ContentValues getActionEventFromCache() {
        return mActionEventQueue.poll();
    }

    /**
     * Removes a single instance of the specified element from this queue, if it is present.
     *
     * @param actionEvent instance of the specified action event element
     * @return true if removed  a single instance of the specified element from this queue, otherwise false.
     */
    @Override
    public boolean removeActionEventFromCache(ContentValues actionEvent) {
        return mActionEventQueue.remove(actionEvent);
    }

    /**
     * Clear all action event element from cache queue
     */
    @Override
    public void clearActionEventFromCache() {
        mActionEventQueue.clear();
    }

    /**
     * http response callback
     *
     * @param statusCode response status code
     * @param header     response header base on List collection
     * @param body       response body base on stream
     */
    @Override
    public void onHttpResponse(int statusCode, Map<String, List<String>> header, InputStream body) {
        System.out.println("onHttpResponse:Thread is Main? - " + (Looper.getMainLooper() == Looper.myLooper()));
        if (mSendEventListener != null) {
            mSendEventListener.onSendEventFinish(statusCode, header, body);
        }
    }

    /**
     * Set audience information
     *
     * @param context      a Context object used to access application assets
     * @param audienceInfo Audience information base on <code>Map</code>
     */
    @Override
    public void setAudienceInfo(Context context, Map<String, String> audienceInfo) {
        if (audienceInfo != null) {
            if (audienceInfo.isEmpty()) {
                audienceInfo.put("aid", "");
                audienceInfo.put("av", AudienceTrackHelper.getAppVersion(context));
                audienceInfo.put("bid", AudienceTrackHelper.getBundleIdentifier(context));
                audienceInfo.put("bss", AudienceTrackHelper.getWifiBSSID(context));
                audienceInfo.put("cn", AudienceTrackHelper.getCarrierName(context));
                audienceInfo.put("de", AudienceTrackHelper.getDeviceEmulator(context));
                audienceInfo.put("did", AudienceTrackHelper.getDID(context));
                audienceInfo.put("jb", AudienceTrackHelper.getJailbreakStatus());
                audienceInfo.put("lng", AudienceTrackHelper.getLanguageAndCountry());
                audienceInfo.put("mod", AudienceTrackHelper.getMod());
                audienceInfo.put("nt", AudienceTrackHelper.getNetworkType(context));
                audienceInfo.put("osv", AudienceTrackHelper.getOSVersion());
                audienceInfo.put("sid", AudienceTrackHelper.getSystemId(context));
                audienceInfo.put("uuid", AudienceTrackHelper.getUUID(context));
                audienceInfo.put("wma", AudienceTrackHelper.getWifiMACAddress(context));
            } else {
                audienceInfo.put("bss", AudienceTrackHelper.getWifiBSSID(context));
                audienceInfo.put("cn", AudienceTrackHelper.getCarrierName(context));
                audienceInfo.put("de", AudienceTrackHelper.getDeviceEmulator(context));
                audienceInfo.put("jb", AudienceTrackHelper.getJailbreakStatus());
                audienceInfo.put("lng", AudienceTrackHelper.getLanguageAndCountry());
                audienceInfo.put("nt", AudienceTrackHelper.getNetworkType(context));
            }
        }
    }

    /**
     * Get audience information collection
     *
     * @return Audience information base on <code>Map</code>
     */
    @Override
    public Map<String, String> getAudienceInfo() {
        return mAudienceInfo;
    }

    /**
     * Check permissings granted
     *
     * @param context     a Context object used to access application assets
     * @param permissions permission array
     * @return If permissions granted are true, otherwise false .
     */
    @Override
    public boolean checkPermissionsGranted(Context context, String[] permissions) {
        for (String p : permissions) {
            if (!SecurityHelper.checkPermissionGranted(context, p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Application is activated
     *
     * @return activation status, already activated is true, otherwise false.
     */
    @Override
    public boolean isActivated() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isActivated", false)) {
            sharedPreferences.edit().putBoolean("isActivated", true).apply();
            return true;
        }
        return false;
    }

    /**
     * AdvertisingId Callback
     *
     * @param adInfo <code>AdInfo</code> object
     */
    @Override
    public void onAdvertisingIdObtainFinish(AdvertisingIdClient.AdInfo adInfo) {
        System.out.println("onAdvertisingIdObtainFinish----->" + adInfo.getId());
        mAudienceInfo.put("aid", adInfo.getId());
    }

    /**
     * Database operation result callback
     *
     * @param results Database operation result by ContentValues
     */
    @Override
    public void onDatabaseOperationResult(DatabaseHelper.OpsType opsTypes, ContentValues[] results, long rowID) {
        switch (opsTypes) {
            case INSERT:
                System.out.println("onDatabaseOperationResult---->opsTypes:" + opsTypes + "---->ContentValues:" +
                        rowID);
                dbh.delete(this, EVENTS_TABLE, "_id IN(?,?,?)", new String[]{String.valueOf(rowID), String.valueOf
                        (1), String.valueOf(2)});
                break;
            case QUERY:
                if (results != null && results.length != 0) {
                    System.out.println("onDatabaseOperationResult---->opsTypes:" + opsTypes + "---->ContentValues:" +
                            results.length);
                    for (ContentValues contentValue : results) {
                        // Remove primary key
                        if (contentValue.containsKey(PRIMARY_KEY_NAME)) {
                            contentValue.remove(PRIMARY_KEY_NAME);
                        }
                        mActionEventQueue.offer(contentValue);
                    }
                }
                break;
            case DELETE:
                System.out.println("onDatabaseOperationResult---->opsTypes:" + opsTypes + "---->ContentValues:" +
                        rowID);
                break;
        }
    }
}
