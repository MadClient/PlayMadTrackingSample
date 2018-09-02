package cn.playmad.ads.gtch.google.com.playmadsdk.Presenter;

/**
 * Copyright © 2006-2017 Madhouse Inc. All Rights Reserved.
 * Created by RobertZhou on 2017/11/22.
 */

public interface EventTaskPresenter {

    void addEvents(String category, String action, String label, Number value);

    boolean checkRequiredPermission();

}
