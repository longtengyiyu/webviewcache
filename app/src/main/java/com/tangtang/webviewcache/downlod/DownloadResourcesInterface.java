package com.tangtang.webviewcache.downlod;

import com.tangtang.webviewcache.bean.Downloadable;

import java.util.List;

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
public interface DownloadResourcesInterface<T extends Downloadable> {
    /**
     * 解析素材
     */
    void analytics();

    /**
     * 开始下载多资源,默认显示dialog
     * @param resources
     */
    void downloadResources(List<T> resources, ResourceDownloadListener listener);

    /**
     * 开始下载多资源
     * @param resources 资源
     * @param isShowDialog 是否显示dialog
     */
    void downloadResources(List<T> resources, boolean isShowDialog, ResourceDownloadListener listener);

    /**
     * 开始下载单个资源
     * @param resource
     */
    void downloadResource(T resource, ResourceDownloadListener listener);


    /**
     * 开始下载单个资源
     * @param resource 默认显示dialog
     * @param isShowDialog 是否显示现在dialog
     */
    void downloadResource(T resource, boolean isShowDialog, ResourceDownloadListener listener);

    /**
     * 暂停下载资源
     */
    void pauseDownloadResource();

    /**
     * 释放资源
     */
    void onDestroy();

}
