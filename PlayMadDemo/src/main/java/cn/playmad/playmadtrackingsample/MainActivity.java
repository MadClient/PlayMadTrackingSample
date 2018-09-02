package cn.playmad.playmadtrackingsample;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.UUID.V2.ChannelInfo;
import cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.UUID.WalleChannelReader;
import cn.playmad.ads.gtch.google.com.playmadsdk.PlayMad;

import static cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.AudienceTrackHelper.getUUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("----------MainActivity----------");
        System.out.println("PlayMad---------->hash code:" + PlayMad.getInstance().hashCode());
        PlayMad.getInstance().init(this).sendOpenEvent();
        getProcessName(this);
        TextView tv = (TextView) findViewById(R.id.uuid);
        tv.setText(getUUID(this) != null ? getUUID(this) : "Null");
        Button btn1 = findViewById(R.id.button1);
        Button btn2 = findViewById(R.id.button2);
        Button btn3 = findViewById(R.id.button3);
        Button btn4 = findViewById(R.id.button4);
        Button btn5 = findViewById(R.id.button5);
        Button btn6 = findViewById(R.id.button6);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMad.getInstance().sendOpenEvent();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMad.getInstance().sendSignupEvent();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMad.getInstance().sendSigninEvent();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMad.getInstance().sendTransactEvent("IAP_USD", 18.0);
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMad.getInstance().sendCustomEvnet("App", "Button", "Click");
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMad.getInstance().sendCustomEvnet("App", "Button", "Click", 10.5);
            }
        });
    }

    private String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    System.out.println("---------->Process Name:" + proInfo.processName);
                    return proInfo.processName;
                }
            }
        }
        return null;
    }

    private void readChannel() {
        final long startTime = System.currentTimeMillis();
        final ChannelInfo channelInfo = WalleChannelReader.getChannelInfo(this
                .getApplicationContext());
        StringBuilder tmp = new StringBuilder();
        if (channelInfo != null) {
            for (Map.Entry<String, String> entry : channelInfo.getExtraInfo().entrySet()) {
                tmp.append("|Key:").append(entry.getKey()).append("Value:").append(entry.getValue());
            }
            Toast.makeText(this, "ChannelReader takes :" + channelInfo.getChannel() + "-" + tmp
                    .toString() + ":" +
                    (System.currentTimeMillis() - startTime) + " milliseconds", Toast
                    .LENGTH_SHORT).show();
        }

    }

    private String getUUIDV1(Context context) {
        File apkFile = new File(context.getPackageResourcePath());
        try {
            FileInputStream fileInputStream = new FileInputStream(apkFile);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream
                    (fileInputStream));
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if (entryName.contains("_PLAYMAD_UUID_")) {
                    return entryName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
