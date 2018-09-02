package cn.playmad.ads.gtch.google.com.playmadsdk.View;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.playmad.ads.gtch.google.com.playmadsdk.Constants.LogLevel;
import cn.playmad.ads.gtch.google.com.playmadsdk.PlayMad;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/11/22.
 */

public interface EventTaskView {

    PlayMad init(@NonNull Context context);

    void sendOpenEvent();

    void sendOpenEvent(@Nullable String label, @Nullable Number value);

    void sendSignupEvent();

    void sendSignupEvent(@Nullable String label, @Nullable Number value);

    void sendSigninEvent();

    void sendSigninEvent(@Nullable String label, @Nullable Number value);

    void sendTransactEvent();

    void sendTransactEvent(@Nullable String label, @Nullable Number value);

    void sendCustomEvnet(@NonNull String category, @NonNull String action);

    void sendCustomEvnet(@NonNull String category, @NonNull String action, @Nullable String label);

    void sendCustomEvnet(@NonNull String category, @NonNull String action, @Nullable String
            label, @Nullable Number value);

    void outputToConsole(LogLevel logLevel, String msg);

}
