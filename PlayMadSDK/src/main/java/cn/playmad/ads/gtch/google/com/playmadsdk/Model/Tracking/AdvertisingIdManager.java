package cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/12/6.
 */

public class AdvertisingIdManager extends AsyncTask<Context, String, AdvertisingIdClient.AdInfo> {

    private AdvertisingIdListener mListener;

    public AdvertisingIdManager(AdvertisingIdListener listener) {
            mListener = listener;
    }

    @Override
    protected AdvertisingIdClient.AdInfo doInBackground(Context... contexts) {
        AdvertisingIdClient.AdInfo adInfo;
        try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(contexts[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return adInfo;
    }

    @Override
    protected void onPostExecute(AdvertisingIdClient.AdInfo adInfo) {
        super.onPostExecute(adInfo);
        if (mListener != null && adInfo != null) {
            mListener.onAdvertisingIdObtainFinish(adInfo);
        }
    }
}
