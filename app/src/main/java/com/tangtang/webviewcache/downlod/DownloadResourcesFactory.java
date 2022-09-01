package com.tangtang.webviewcache.downlod;

import android.content.Context;

import androidx.annotation.DrawableRes;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2021/9/14
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/9/14      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public interface DownloadResourcesFactory {
    /**
     *
     * @param context 上下文
     * @param type
     * @param img
     * @param content
     * @return
     */
    DownloadResourcesHelper build(Context context,
                                  int type,
                                  @DrawableRes int img,
                                  String content);
}
