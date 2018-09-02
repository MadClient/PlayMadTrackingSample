package cn.playmad.ads.gtch.google.com.playmadsdk;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by robertchow on 2017/11/08.
 */

public class Constants {

    /**
     * Action event define by enum
     */
    public enum EventActions {
        /**
         * App First Open or Open Event.
         */
        OPEN,
        /**
         * In-App signup to the service/app.
         */
        SIGNUP,

        /**
         * Sign-in of returning user.
         */
        SIGNIN,

        /**
         * Complete an in-app transaction e.g. purchase item/service.
         */
        TRANSACT
    }

    /**
     * Log level define by enum
     */
    public enum LogLevel {
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR
    }
}
