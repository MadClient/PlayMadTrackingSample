package cn.playmad.ads.gtch.google.com.playmadsdk.Model.Http;

import java.util.List;
import java.util.Map;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/11/15.
 */

public interface HttpRequest {

    public static final int CONNECT_TIMEOUT = 10000;

    public static final int READ_TIMEOUT = 10000;

    public static final String HTTP_METHOD_GET = "GET";

    public static final String HTTP_METHOD_POST = "POST";

    void httpGetRequest(String url, Map<String, List<String>> requestHeader, HttpResponseListener
            listener);

    void httpPostRequest(String url, Map<String, List<String>> requestHeader, byte[] requestBody,
                         HttpResponseListener listener);

    void httpsGetRequest(String url, Map<String, List<String>> requestHeader,
                         HttpResponseListener listener);

    void httpsPostRequest(String url, Map<String, List<String>> requestHeader, byte[]
            requestBody, HttpResponseListener listener);
}
