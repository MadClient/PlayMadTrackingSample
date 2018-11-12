package cn.playmad.ads.gtch.google.com.playmadsdk.Model.Database;

import android.content.ContentValues;

/**
 * Copyright © 2006-2018 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2018/6/22.
 */
public interface DatabaseListener {
    void onDatabaseOperationResult(DatabaseHelper.OpsType opsTypes, ContentValues[] results, long rowID);
}
