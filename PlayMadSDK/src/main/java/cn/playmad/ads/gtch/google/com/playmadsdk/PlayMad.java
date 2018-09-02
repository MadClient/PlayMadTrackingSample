package cn.playmad.ads.gtch.google.com.playmadsdk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import cn.playmad.ads.gtch.google.com.playmadsdk.Constants.LogLevel;
import cn.playmad.ads.gtch.google.com.playmadsdk.Presenter.EventTaskPresenter;
import cn.playmad.ads.gtch.google.com.playmadsdk.Presenter.EventTaskPresenterImpl;
import cn.playmad.ads.gtch.google.com.playmadsdk.View.EventTaskView;

import static cn.playmad.ads.gtch.google.com.playmadsdk.Constants.LogLevel.ERROR;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by robertchow on 2017/11/08.
 */

public class PlayMad implements EventTaskView {

    /**
     * Constant
     */
    private static final String TAG = "PlayMad";
    private static final String CATEGORY_USER = "USER";


    /**
     * EventTaskPresenterImpl Object Handle
     */
    private EventTaskPresenter mPresenter;
    private volatile boolean isInitialized = false;

    /**
     * Class Constructor
     */
    private PlayMad() {
    }

    /**
     * PlayMad initialized
     *
     * @param context a Context object used to access application assets
     */
    @Override
    public PlayMad init(@NonNull Context context) {
        System.out.println("PlayMad:init---------->hash code:" + this.hashCode());
        if (!isInitialized) {
            if (mPresenter == null) {
                mPresenter = new EventTaskPresenterImpl(context.getApplicationContext(), this);
                System.out.println("presenter---------->hash code:" + mPresenter.hashCode());
            }
            if (mPresenter.checkRequiredPermission()) {
                isInitialized = true;
            } else {
                outputToConsole(ERROR, "Missing required permission! Add in AndroidManifest.xml.");
            }
        }
        return this;
    }

    /**
     * Send app first open event
     */
    @Override
    public void sendOpenEvent() {
        if (!isInitialized && mPresenter == null) {
            outputToConsole(ERROR, "initialized failed: Must first call the init(Context) method");
            return;
        }
        mPresenter.addEvents(CATEGORY_USER, Constants.EventActions.OPEN.name(), null, null);
    }

    /**
     * Send app opened event
     *
     * @param label Label of action
     * @param value Value of action
     */
    @Override
    public void sendOpenEvent(@Nullable String label, @Nullable Number value) {
        mPresenter.addEvents(CATEGORY_USER, Constants.EventActions.OPEN.name(), label, value);
    }

    /**
     * Send audience sign up event
     */
    @Override
    public void sendSignupEvent() {
        if (!isInitialized && mPresenter == null) {
            outputToConsole(ERROR, "initialized failed: Must first call the init(Context) method");
            return;
        }
        mPresenter.addEvents(CATEGORY_USER, Constants.EventActions.SIGNUP.name(), null, null);
    }

    /**
     * Send audience sign up event
     *
     * @param label Label of action
     * @param value Value of action
     */
    @Override
    public void sendSignupEvent(@Nullable String label, @Nullable Number value) {
        if (!isInitialized && mPresenter == null) {
            outputToConsole(ERROR, "initialized failed: Must first call the init(Context) method");
            return;
        }
        mPresenter.addEvents(CATEGORY_USER, Constants.EventActions.SIGNUP.name(), label, value);
    }

    /**
     * end audience sign in event
     */
    @Override
    public void sendSigninEvent() {
        if (!isInitialized && mPresenter == null) {
            outputToConsole(ERROR, "initialized failed: Must first call the init(Context) method");
            return;
        }
        mPresenter.addEvents(CATEGORY_USER, Constants.EventActions.SIGNIN.name(), null, null);
    }

    /**
     * Send audience sign in event
     *
     * @param label Label of action
     * @param value Value of action
     */
    @Override
    public void sendSigninEvent(@Nullable String label, @Nullable Number value) {
        if (!isInitialized && mPresenter == null) {
            outputToConsole(ERROR, "initialized failed: Must first call the init(Context) method");
            return;
        }
        mPresenter.addEvents(CATEGORY_USER, Constants.EventActions.SIGNIN.name(), label,
                value);
    }

    /**
     * Send audience transact event
     */
    @Override
    public void sendTransactEvent() {
        if (!isInitialized && mPresenter == null) {
            outputToConsole(ERROR, "initialized failed: Must first call the init(Context) method");
            return;
        }
        mPresenter.addEvents(CATEGORY_USER, Constants.EventActions.TRANSACT.name(), null, null);
    }

    /**
     * Send audience transact event
     *
     * @param label Label of action
     * @param value Value of action
     */
    @Override
    public void sendTransactEvent(@Nullable String label, @Nullable Number value) {
        if (!isInitialized && mPresenter == null) {
            outputToConsole(ERROR, "initialized failed: Must first call the init(Context) method");
            return;
        }
        mPresenter.addEvents(CATEGORY_USER, Constants.EventActions.TRANSACT.name(), label, value);
    }

    /**
     * Send media custom event
     *
     * @param category Category of tracking object
     * @param action   Action of category
     */
    @Override
    public void sendCustomEvnet(@NonNull String category, @NonNull String action) {
        if (!isInitialized && mPresenter == null) {
            outputToConsole(ERROR, "initialized failed: Must first call the init(Context) method");
            return;
        }
        mPresenter.addEvents(category, action, null, null);
    }

    /**
     * Send media custom event
     *
     * @param category Category of tracking object
     * @param action   Action of category
     * @param label    Label of action
     */
    @Override
    public void sendCustomEvnet(@NonNull String category, @NonNull String action, @Nullable
            String label) {
        if (!isInitialized && mPresenter == null) {
            outputToConsole(ERROR, "initialized failed: Must first call the init(Context) method");
            return;
        }
        mPresenter.addEvents(category, action, label, null);
    }

    /**
     * Send media custom event
     *
     * @param category Category of tracking object
     * @param action   Action of category
     * @param label    Label of action
     * @param value    Value of action
     */
    @Override
    public void sendCustomEvnet(@NonNull String category, @NonNull String action, @Nullable
            String label, @Nullable Number value) {
        if (!isInitialized && mPresenter == null) {
            outputToConsole(ERROR, "initialized failed: Must first call the init(Context) method");
            return;
        }
        mPresenter.addEvents(category, action, label, value);

    }

    /**
     * Outpu logs to console
     *
     * @param msg String log message
     */
    @Override
    public void outputToConsole(LogLevel logLevel, String msg) {
        switch (logLevel) {
            case VERBOSE:
                Log.v(TAG, msg);
                break;
            case DEBUG:
                Log.d(TAG, msg);
                break;
            case INFO:
                Log.i(TAG, msg);
                break;
            case WARNING:
                Log.w(TAG, msg);
                break;
            case ERROR:
                Log.e(TAG, msg);
                break;
        }
    }


    /**
     * @return PlayMadSingleton singleton instance
     */
    public static PlayMad getInstance() {
        return PlayMadSingleton.INSTANCE.getInstance();
    }

    /**
     * Singleton for Enum
     */
    private enum PlayMadSingleton {
        INSTANCE;
        private PlayMad singleton;

        /**
         * Enum Constructor
         * Only be called once by the JVM
         * Modifier 'private' is redundant for enum constructors
         */
        PlayMadSingleton() {
            singleton = new PlayMad();
        }

        /**
         * @return PlayMad singleton instance
         */
        public PlayMad getInstance() {
            return singleton;
        }
    }

}
