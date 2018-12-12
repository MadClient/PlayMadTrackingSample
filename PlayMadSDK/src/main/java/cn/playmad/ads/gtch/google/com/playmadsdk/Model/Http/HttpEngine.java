package cn.playmad.ads.gtch.google.com.playmadsdk.Model.Http;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/12/5.
 */

public class HttpEngine implements HttpRequest {

    /**
     * Member variables
     */
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private final Collection<HttpResponseListener> listeners = new HashSet<>();

    /**
     * Construct
     */
    public HttpEngine() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
    }

    /**
     * Get request for http
     *
     * @param url           get request url for http
     * @param requestHeader get request header field for http
     * @param listener      callback listener
     */
    @Override
    public void httpGetRequest(String url, Map<String, List<String>> requestHeader, HttpResponseListener listener) {
        setListeners(listener);
        issueHttp(HttpRequest.HTTP_METHOD_GET, url, requestHeader, null);
    }

    /**
     * Post request for http
     *
     * @param url           post request url for http
     * @param requestHeader post request header field for http
     * @param requestBody   post request body field for http
     * @param listener      callback listener
     */
    @Override
    public void httpPostRequest(String url, Map<String, List<String>> requestHeader, byte[] requestBody,
                                HttpResponseListener listener) {
        setListeners(listener);
        issueHttp(HttpRequest.HTTP_METHOD_POST, url, requestHeader, requestBody);
    }

    /**
     * Get request for https
     *
     * @param url           get request url for https
     * @param requestHeader get request header field for https
     * @param listener      callback listener
     */
    @Override
    public void httpsGetRequest(String url, Map<String, List<String>> requestHeader, HttpResponseListener listener) {
        setListeners(listener);
        issueHttp(HttpRequest.HTTP_METHOD_GET, url, requestHeader, null);
    }

    /**
     * Post request for https
     *
     * @param url           post request url for https
     * @param requestHeader post request header field for https
     * @param requestBody   post request body field for https
     * @param listener      callback listener
     */
    @Override
    public void httpsPostRequest(String url, Map<String, List<String>> requestHeader, byte[] requestBody,
                                 HttpResponseListener listener) {
        setListeners(listener);
        issueHttps(HttpRequest.HTTP_METHOD_POST, url, requestHeader, requestBody);
    }

    /**
     * Setting listener
     *
     * @param listener callback listener
     */
    private void setListeners(HttpResponseListener listener) {
        if (listener != null) {
            synchronized (this.listeners) {
                this.listeners.add(listener);
            }
        }
    }

    /**
     * Issue http request
     *
     * @param url    common request url for http
     * @param header common request header field for http
     * @param body   common request body field for http
     */
    private void issueHttp(final String method, final String url, final Map<String, List<String>> header, final
    byte[] body) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpConn = null;
                try {
                    URL requestURL = new URL(url);
                    httpConn = (HttpURLConnection) requestURL.openConnection();
                    httpConn.setRequestMethod(method);
                    httpConn.setConnectTimeout(HttpRequest.CONNECT_TIMEOUT);
                    httpConn.setReadTimeout(HttpRequest.READ_TIMEOUT);
                    if (header != null) {
                        for (Map.Entry<String, List<String>> entry : header.entrySet()) {
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                httpConn.addRequestProperty(entry.getKey(), entry.getValue().get(i));
                            }
                        }
                    }
                    if (method.equals(HttpRequest.HTTP_METHOD_POST) && body != null && body.length != 0) {
                        httpConn.setDoOutput(true);
                        OutputStream outStream = httpConn.getOutputStream();
                        outStream.write(body);
                        outStream.flush();
                        outStream.close();
                    }
                    if (httpConn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                        httpResponseCallback(httpConn.getResponseCode(), httpConn.getHeaderFields(),
                                httpConn.getInputStream());
                    } else {
                        httpResponseCallback(httpConn.getResponseCode(), httpConn.getHeaderFields(),
                                httpConn.getErrorStream());
                    }
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } finally {
                    if (httpConn != null) {
                        httpConn.disconnect();
                    }
                }
            }

        });
    }

    /**
     * Issue https request
     *
     * @param url    common request url for https
     * @param header common request header field for https
     * @param body   common request body field for https
     */
    private void issueHttps(final String method, final String url, final Map<String, List<String>> header, final byte[] body) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    /**
     * Response callback
     *
     * @param responseStatus callback response status
     * @param responseHeader callback response header field
     * @param responseBody   callback response body field
     */
    private void httpResponseCallback(final int responseStatus, final Map<String, List<String>> responseHeader, final
    InputStream responseBody) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                HttpResponseListener[] array = listeners.toArray(new HttpResponseListener[0]);
                for (HttpResponseListener listener : array) {
                    listener.onHttpResponse(responseStatus, responseHeader, responseBody);
                }
            }
        });
    }
}
