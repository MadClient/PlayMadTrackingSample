package cn.playmad.ads.gtch.google.com.playmadsdk.Model;

import android.content.ContentValues;
import android.content.Context;

import java.util.List;
import java.util.Map;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/11/08.
 */

public interface EventTaskModel<T> {

    void sendEvent(String url, Map<String, List<String>> requestHeader, EventTaskListener listener);

    void addActionEventToCache(T t);

    ContentValues getActionEventFromCache();

    boolean removeActionEventFromCache(ContentValues actionEvent);

    void clearActionEventFromCache();

    void setAudienceInfo(Context context, Map<String, String> audienceInfo);

    Map<String, String> getAudienceInfo();

    boolean checkPermissionsGranted(Context context, String[] permissions);

    boolean isActivated();
}
