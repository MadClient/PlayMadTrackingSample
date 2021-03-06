package cn.playmad.ads.gtch.google.com.playmadsdk.Presenter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.playmad.ads.gtch.google.com.playmadsdk.Constants;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.EventTaskListener;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.EventTaskModel;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.EventTaskModelImpl;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.AudienceTrackHelper;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Utils.SecurityHelper;
import cn.playmad.ads.gtch.google.com.playmadsdk.View.EventTaskView;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/11/08.
 */

public class EventTaskPresenterImpl implements EventTaskPresenter, EventTaskListener {

    /**
     * Constant
     */
    private static final String VERSION = "1.0.1.0322";
    //    private static final String SERVER_API = "http://tracking.playmad.cn/api/madplay/tracking/event";
//    private static final String SERVER_API = "http://172.16.26.217:8080/api/playmad/engine/tracking/event";
    private static final String SERVER_API = "http://tracking.playmad.cn/api/playmad/engine/tracking/event";
    private static final int MAX_REQUEST_URL = 7000;
    private static final String LABEL_FIRSTOPEN = "FIRSTOPEN";
    private static final String SESSIONID = "sessionId";
    private static final String TIMESTAMP = "Timestamp";
    private static final String MAXAGE = "Max-Age";


    /**
     * Member object
     */
    private EventTaskModel<ContentValues> model;
    private EventTaskView view;
    private Context mContext;
    private Map<String, String> mLifeCycle;

    /**
     * Event elements define by enum
     */
    private enum EventElements {
        SESSION("sid"),
        CATEGORY("cat"),
        ACTION("act"),
        LABEL("lab"),
        VALUE("val"),
        TIMESTAMP("utc");

        private final String text;

        EventElements(final String text) {
            this.text = text;
        }

        public String alias() {
            return text;
        }
    }

    /**
     * Construtor
     */
    public EventTaskPresenterImpl(Context context, EventTaskView view) {
        this.view = view;
        this.mContext = context;
        model = new EventTaskModelImpl(context);

        // test
//        test(new String[]{"test1", "test2", "test3"}, null, new String[]{"item1", EventElements.CATEGORY.alias(),
//                null});
//        String[][] str = {{"test1"}, {"test2", "test3"}};
//        test(str);
//        test1(str, "test", new String[]{"test1", "test2", "test3"}, null);
        test3(context, "cn.playmad.playmadtrackingsample");
    }

    /**
     * Add events for all in one
     *
     * @param category Category of tracking object
     * @param action   Action of Tracking Object
     * @param label    Label of action
     * @param value    value of action
     */
    @Override
    public void addEvents(String category, String action, String label, Number value) {
        System.out.println("Advertising Id---------->" + model.getAudienceInfo().get("aid"));
        // First open or application is activated
        if (action.equals(Constants.EventActions.OPEN.name()) && model.isActivated()) {
            label = LABEL_FIRSTOPEN;
        }
        // Add action events to cache mechanism
        model.addActionEventToCache(generateActionEvent(getSessionId(), category, action, label, value));
        sendActionEventToServer();
    }

    /**
     * Check requeired permission
     *
     * @return Verify requeired permissions Configuration, true if all exist, otherwise false.
     */
    @Override
    public boolean checkRequiredPermission() {
        String[] permission = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android" +
                ".permission.ACCESS_WIFI_STATE", "android.permission.READ_PHONE_STATE"};
        return model.checkPermissionsGranted(mContext, permission);
    }

    /**
     * Callback method for sent information
     *
     * @param statusCode sent status code
     * @param header     response header
     * @param body       response body
     */
    @Override
    public void onSendEventFinish(int statusCode, Map<String, List<String>> header, InputStream body) {
        System.out.println("sendEvent:Thread is Main? - " + (Looper.getMainLooper() == Looper.myLooper()));
        System.out.println("----->HTTP Response Status Code: " + statusCode);
        // 处理不是200的问题
        try {
            // print info
            for (Map.Entry<String, List<String>> entry : header.entrySet()) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    System.out.println(entry.getKey() + ":" + entry.getValue().get(i));
                }
            }
            switch (statusCode) {
                case 200: // Success
                    setLifeCycle(header);
                    break;
                case 400: // Failure
                    break;
                case 401: // Unauthorized
                    setLifeCycle(header);
                    break;
                case 403: // Forbidden
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send action events to server
     */
    private void sendActionEventToServer() {
        StringBuffer requestURL = new StringBuffer(trackingInformation(model.getAudienceInfo()));
        SecurityHelper.appendParams(requestURL, "ae", actionEventConvertToJson(requestURL.length()));
        // send action event to server
        model.sendEvent(requestURL.toString(), null, this);
        view.outputToConsole(Constants.LogLevel.INFO, "Send action event to server by URL: " + requestURL.toString());
    }

    /**
     * Get tracking information convert sent string
     *
     * @param audienceInfo Audience information Collection
     * @return Audience tracking information string to be sent
     */
    private String trackingInformation(Map<String, String> audienceInfo) {
        if (audienceInfo != null) {
            String key;
            StringBuffer requestUrl = new StringBuffer();
            for (Map.Entry<String, String> entry : audienceInfo.entrySet()) {
                key = entry.getKey();
                // Encrypted transmission
                if (key.equals("aid") || key.equals("did") || key.equals("wma") || key.equals("sid") || key.equals
                        ("bss")) {
                    SecurityHelper.appendParams(requestUrl, key, SecurityHelper.transferEncode(entry.getValue()));
                } else {
                    SecurityHelper.appendParams(requestUrl, key, entry.getValue());
                }
            }
            SecurityHelper.appendParams(requestUrl, "pv", VERSION);
            return requestUrl.insert(0, SERVER_API).toString();
        }
        return null;
    }

    /**
     * Action event convert to JSON Format
     *
     * @param urlLength url string length
     * @return JSON format for action event
     */
    private String actionEventConvertToJson(int urlLength) {
        // Create a json object that contains the all json content.
        JSONObject collection = new JSONObject();
        // Building array structures of JSON
        JSONArray array = new JSONArray();
        try {
            do {
                ContentValues contentValues = model.getActionEventFromCache();
                if (contentValues == null) {
                    break;
                }
                // Building object structures of JSON
                JSONObject object = new JSONObject();
                // Traverse the ContentValues Collection
                for (Map.Entry<String, Object> item : contentValues.valueSet()) {
                    object.put(item.getKey(), item.getValue());
                }
                // Add to JSON array object
                array.put(object);
                urlLength = urlLength + array.toString().length();
            } while (urlLength < MAX_REQUEST_URL);
            // Add to JSON collection object
            collection.put("evt", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return collection.toString();
    }

    /**
     * Generate Action Event Object
     *
     * @param sessionid Session Identifier of tracking life cycle
     * @param category  Category of tracking object
     * @param action    Action of category
     * @param label     Label of action
     * @param value     Value of action
     * @return Action event object
     */
    private ContentValues generateActionEvent(String sessionid, String category, String action, String label, Number
            value) {
        ContentValues actionEvent = new ContentValues();
        actionEvent.put(EventElements.SESSION.alias(), sessionid);
        actionEvent.put(EventElements.CATEGORY.alias(), category);
        actionEvent.put(EventElements.ACTION.alias(), action);
        actionEvent.put(EventElements.LABEL.alias(), label);
        if (value instanceof Integer) {
            actionEvent.put(EventElements.VALUE.alias(), value.intValue());
        } else {
            actionEvent.put(EventElements.VALUE.alias(), (Double) value);
        }
        actionEvent.put(EventElements.TIMESTAMP.alias(), AudienceTrackHelper.getUTC());
        return actionEvent;
    }

    /**
     * Setting life cycle for session of cookie
     *
     * @param header response header
     */
    private void setLifeCycle(Map<String, List<String>> header) {
        if (mLifeCycle == null) {
            mLifeCycle = new HashMap<>();
        }
        List<String> cookies = header.get("Set-Cookie");
        for (String cookie : cookies) {
            if (cookie.contains(SESSIONID)) {
                System.out.println("cookie String find cookies have SESSIONID");
                // If the session doesn't exist or isn't same as the latest by server, the session period will be reset.
                if (getSessionId().isEmpty() || !cookie.contains(getSessionId())) {
                    String[] attrs = cookie.split(";");
                    for (String attr : attrs) {
                        String[] kv = attr.split("=");
                        if (kv.length > 0) {
                            mLifeCycle.put(kv[0].trim(), kv[1].trim());
                        }
                    }
                    mLifeCycle.put(TIMESTAMP, AudienceTrackHelper.getUTC());
//                    test2(mLifeCycle);
                }
            }
        }
    }

    /**
     * Return a valid session identifier
     *
     * @return a valid session identifier or empty string
     */
    private String getSessionId() {
        String sessionid, timestamp, maxage;
        if (mLifeCycle != null && !mLifeCycle.isEmpty()) {
            sessionid = mLifeCycle.get(SESSIONID);
            if (sessionid == null) {
                sessionid = "";
            } else {
                maxage = mLifeCycle.get(MAXAGE);
                timestamp = mLifeCycle.get(TIMESTAMP);
                try {
                    // Calculate whether the session times out，empty after timeout
                    if ((Long.parseLong(AudienceTrackHelper.getUTC()) - Long.parseLong(timestamp)) / 1000 > Long.parseLong(maxage)) {
                        mLifeCycle.clear();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } else {
            sessionid = "";
        }
        return sessionid;
    }

    private void test(String[]... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                for (int j = 0; j < args[i].length; j++) {
                    System.out.println("arguments" + i + " :" + args[i][j]);
                }
            } else {
                System.out.println("arguments" + i + " :" + args[i]);
            }
        }
    }

    private void test1(Object... args) {
        System.out.println("Object length: " + args.length);
        for (Object o : args) {
            if (o != null) {
                System.out.println("<----arguments Class type is: " + o.getClass() + "---->");
            } else {
                System.out.println("<----arguments is null!---->");
            }
        }
    }

    private void test2(Map<String, String> map) {
        for (String key : map.keySet()) {
            System.out.println("LifeCycle Content---->" + key + ":" + map.get(key));
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    private void test3(Context context, String pkgname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            for (Signature signature : signatures) {
                parseSignature(signature.toByteArray());
//                System.out.println(signature.toCharsString());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            cert.getVersion();
            System.out.println("signName:" + cert.getSigAlgName());
            System.out.println("pubKey:" + pubKey);
            System.out.println("signNumber:" + signNumber);
            System.out.println("subjectDN:" + cert.getSubjectDN().toString());
            System.out.println("Type:" + cert.getType());
            System.out.println("Version:" + cert.getVersion());
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }
}
