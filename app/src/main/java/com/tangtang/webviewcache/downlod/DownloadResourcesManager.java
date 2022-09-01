package com.tangtang.webviewcache.downlod;

import android.content.Context;

import androidx.annotation.DrawableRes;

import com.tangtang.webviewcache.bean.Resource;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2021/9/14
 * Description: 下载管理器
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/9/14      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class DownloadResourcesManager implements DownloadResourcesInterface<Resource>{

    public static final int DOWNLOAD_TYPE_MULTI_PARALLEL = 0; //并行多任务下载方式
    public static final int DOWNLOAD_TYPE_MULTI_SERIAL = 1; //串行多任务下载方式

    private static volatile DownloadResourcesManager INSTANCE = null;
    private DownloadResourcesHelper mDownloadResourcesHelper;
    private int downloadType;
    private Context context;
    private int downloadDialogImg;
    private String downloadDialogContent;
    private AtomicBoolean isDownloading = new AtomicBoolean(false);

    private DownloadResourcesManager(DownloadResourcesManager.Builder builder){
        if (builder == null){
            throw new IllegalArgumentException("Builder cannot be null");
        }
        setDownloadTypeAndPath(builder.context, builder.downloadType, builder.downloadDialogImg, builder.downloadDialogContent);
    }

    public static DownloadResourcesManager create(DownloadResourcesManager.Builder builder){
        if (INSTANCE == null){
            synchronized (DownloadResourcesManager.class){
                if (INSTANCE == null){
                    INSTANCE = new DownloadResourcesManager(builder);
                }
            }
        }
        return INSTANCE;
    }

    public DownloadResourcesManager getInstance(){
        return INSTANCE;
    }

    private void setDownloadTypeAndPath(Context context, int type, int downloadDialogImg, String downloadDialogContent){
        this.context = context;
        this.downloadType = type;
        this.downloadDialogImg = downloadDialogImg;
        this.downloadDialogContent = downloadDialogContent;
    }

    private DownloadResourcesHelper getDownloadResourcesHelper(){
        if (mDownloadResourcesHelper == null){
            mDownloadResourcesHelper = new DownloadResourcesHelper
                    .DownloadResourcesHelperFactory()
                    .build(context, downloadType, downloadDialogImg,
                            downloadDialogContent);
        }
        return mDownloadResourcesHelper;
    }

    @Override
    public void analytics() {
        getDownloadResourcesHelper().analytics();
    }

    @Override
    public void downloadResources(List<Resource> resources, ResourceDownloadListener listener) {
        isDownloading.set(true);
        getDownloadResourcesHelper().downloadResources(resources, listener);
    }

    @Override
    public void downloadResources(List<Resource> resources, boolean isShowDialog, ResourceDownloadListener listener) {
        isDownloading.set(true);
        getDownloadResourcesHelper().downloadResources(resources, isShowDialog, listener);
    }

    @Override
    public void downloadResource(Resource downloadResources, ResourceDownloadListener listener) {
        isDownloading.set(true);
        getDownloadResourcesHelper().downloadResource(downloadResources, listener);
    }

    @Override
    public void downloadResource(Resource resource, boolean isShowDialog, ResourceDownloadListener listener) {
        isDownloading.set(true);
        getDownloadResourcesHelper().downloadResource(resource, isShowDialog, listener);
    }

    @Override
    public void pauseDownloadResource() {
        isDownloading.set(false);
        getDownloadResourcesHelper().pauseDownloadResource();
    }

    @Override
    public void onDestroy() {
        isDownloading.set(false);
        getDownloadResourcesHelper().onDestroy();
    }

    public void setDownloadResource(boolean isDownloadResource){
        isDownloading.set(isDownloadResource);
    }

    /**
     * 判断素材是否正在下载
     * @return 正在下载包含
     */
    public boolean isDownloadResource(){
        return isDownloading.get();
    }

    public static class Builder {
        /**
         * 下载方式 包括并行多任务与串行多任务
         *  在资源低的设备需要使用串行多任务下载素材
         */
        private int downloadType;

        /**
         * 上下文
         */
        private Context context;

        /**
         * 下载弹框图
         */
        private int downloadDialogImg;

        /**
         * 下载弹框文字
         */
        private String downloadDialogContent;


        public Builder(){

        }

        public Builder setContext(Context context){
            this.context = context;
            return this;
        }

        public Builder setDownloadType(int type){
            this.downloadType = type;
            return this;
        }

        public Builder setDownloadDialogImg(@DrawableRes int img){
            this.downloadDialogImg = img;
            return this;
        }

        public Builder setDownloadDialogContent(String content){
            this.downloadDialogContent = content;
            return this;
        }

        /**
         * 创建DownloadResourcesManager实例
         * @return {@link DownloadResourcesManager}
         */
        public DownloadResourcesManager build() {
            return create(this);
        }
    }

}
