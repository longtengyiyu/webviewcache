package com.tangtang.webviewcache.downlod;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2021/9/15
 * Description: 素材下载完成监听，包含下载失败跳过的素材
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/9/15      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public interface ResourceDownloadListener {
    /**
     * 下载进度
     * @param completed  完成个数
     * @param failed     失败格式
     * @param total      下载总数
     * @param url        当前下载的链接
     * @param status     下载状态 -1开始下载 0成功 1失败 2暂停 3warn
     */
    void onDownloadProgress(int completed, int failed, int total, String url, int status);
}
