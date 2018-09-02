package cn.playmad.ads.gtch.google.com.playmadsdk.Model;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/12/7.
 */

public interface EventTaskListener {

    void onSendEventFinish(int statusCode, Map<String, List<String>> header, InputStream body);
}
