package cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/12/6.
 */

public interface AdvertisingIdListener {
    /**
     * Called when process completed using UI Thread
     *
     * @param adInfo <code>AdInfo</code> object
     */
    void onAdvertisingIdObtainFinish(AdvertisingIdClient.AdInfo adInfo);
}
