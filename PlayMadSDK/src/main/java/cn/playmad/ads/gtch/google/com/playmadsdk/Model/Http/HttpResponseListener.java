package cn.playmad.ads.gtch.google.com.playmadsdk.Model.Http;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/11/15.
 */

public interface HttpResponseListener {

    void onHttpResponse(int statusCode, Map<String, List<String>> header, InputStream body);
}
