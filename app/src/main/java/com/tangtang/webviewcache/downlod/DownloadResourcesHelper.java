package com.tangtang.webviewcache.downlod;

import android.content.Context;
import android.util.Log;

import androidx.annotation.DrawableRes;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.tangtang.webviewcache.FileUtils;
import com.tangtang.webviewcache.bean.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2021/9/14
 * Description:资源下载帮助类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/9/14      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class DownloadResourcesHelper implements DownloadResourcesInterface<Resource> {
    private static final String TAG = DownloadResourcesHelper.class.getSimpleName();
    private List<BaseDownloadTask> mDownloadTaskList;
    private FileDownloadQueueSet mFileDownloadQueueSet;

    /**
     * 完成个数，包含失败
     */
    AtomicInteger completeAtomicInteger = new AtomicInteger();

    /**
     * 失败的资源数
     */
    AtomicInteger failedAtomicInteger = new AtomicInteger();
    /**
     * 下载方式{@link DownloadResourcesManager#DOWNLOAD_TYPE_MULTI_PARALLEL}
     */
    private int downloadType;
    private final Context context;
    //一些提示信息，如下载中
    private @DrawableRes int dialogImgRes;
    private String dialogContent;

    private ResourceDownloadListener downloadListener;
    
    private DownloadResourcesHelper(Context context, int downloadType,
                                    @DrawableRes int img, String content){
        mDownloadTaskList = new ArrayList<>();
        this.context = context;
        this.downloadType = downloadType;
        this.dialogImgRes = img;
        this.dialogContent = content;
    }

    public static final class DownloadResourcesHelperFactory implements DownloadResourcesFactory {
        @Override
        public DownloadResourcesHelper build(Context context, int type,
                                                                                int img, String content) {
            return new DownloadResourcesHelper(context, type, img, content);
        }
    }

    @Override
    public void analytics() {

    }

    @Override
    public void downloadResources(List<Resource> resources, ResourceDownloadListener listener) {
        clearTaskListData();
        downloadListener = listener;
        updateDownloadTaskList(resources);
        startingDownloadResource();
    }

    @Override
    public void downloadResources(List<Resource> resources, boolean isShowDialog, ResourceDownloadListener listener) {
        clearTaskListData();
        downloadListener = listener;
        updateDownloadTaskList(resources);
        startingDownloadResource();
    }

    @Override
    public void downloadResource(Resource resources, ResourceDownloadListener listener) {
        clearTaskListData();
        downloadListener = listener;
        if (!mDownloadTaskList.isEmpty()){
            FileDownloader.getImpl().pauseAll();
        }
        updateDownloadTaskList(resources);
        startingDownloadResource();
    }

    @Override
    public void downloadResource(Resource resource, boolean isShowDialog, ResourceDownloadListener listener) {
        clearTaskListData();
        downloadListener = listener;
        if (!mDownloadTaskList.isEmpty()){
            FileDownloader.getImpl().pauseAll();
        }
        updateDownloadTaskList(resource);
        startingDownloadResource();
    }

    @Override
    public void pauseDownloadResource() {
        FileDownloader.getImpl().pauseAll();
    }

    @Override
    public void onDestroy() {
        FileDownloader.getImpl().pauseAll();
        clearTaskListData();
    }

    /**
     * 创建任务列表
     */
    private void clearTaskListData(){
        if (!mDownloadTaskList.isEmpty()){
            mDownloadTaskList.clear();
        }
        mFileDownloadQueueSet = null;
        completeAtomicInteger.set(0); //初始化
        failedAtomicInteger.set(0);
    }

    /**
     * 开始下载资源
     */
    private void startingDownloadResource(){
        if (mFileDownloadQueueSet != null){
            FileDownloader.getImpl().pauseAll();
            mFileDownloadQueueSet = null;
        }
        mFileDownloadQueueSet = new FileDownloadQueueSet(createDownloadListener);
        mFileDownloadQueueSet.setCallbackProgressTimes(3);
        setDownloadType();
        mFileDownloadQueueSet.start();
        //通知开始下载
        notificationDownloadProgress(0, 0, mDownloadTaskList.size(), "", -1);
//        if (mFileDownloadQueueSet == null){
//            mFileDownloadQueueSet = new FileDownloadQueueSet(createDownloadListener);
//            mFileDownloadQueueSet.setCallbackProgressTimes(3);
//            setDownloadType();
//            mFileDownloadQueueSet.start();
//        }else{
//            setDownloadType();
//            mFileDownloadQueueSet.reuseAndStart();
//        }
    }

    private void setDownloadType(){
        //并行多任务下载
        if (downloadType == DownloadResourcesManager.DOWNLOAD_TYPE_MULTI_PARALLEL){
            mFileDownloadQueueSet.downloadTogether(mDownloadTaskList);
        }else{
            mFileDownloadQueueSet.downloadSequentially(mDownloadTaskList);
        }
    }

    private void updateDownloadTaskList(List<Resource> resources){
        if (!mDownloadTaskList.isEmpty()){
            FileDownloader.getImpl().pauseAll();
        }
        if (resources != null && !resources.isEmpty()){
            for (Resource resource : resources) {
                updateDownloadTaskList(resource);
            }
        }
    }

    private void updateDownloadTaskList(Resource resource){
        if (resource != null){
            String url = resource.url();
            String name = FileUtils.getFileName(url);
            mDownloadTaskList.add(
                    FileDownloader.getImpl()
                            .create(url)
                            .setPath(resource.getLocal())
                            .setAutoRetryTimes(3)
                            .setTag(name)
            );
        }
    }

    /**
     * 下载回调信息
     */
    private FileDownloadListener createDownloadListener = new FileDownloadListener() {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.d(TAG, "链接：" + task.getUrl() +" 资源下载pending");
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            super.connected(task, etag, isContinue, soFarBytes, totalBytes);
            Log.d(TAG, "链接：" + task.getUrl() +" 资源下载connected");
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
            super.retry(task, ex, retryingTimes, soFarBytes);
            Log.d(TAG, "链接：" + task.getUrl() +" 资源下载retry,尝试次数为：" + retryingTimes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            Log.d(TAG, "链接：" + task.getUrl() +" 资源下载completed" + " path:" + task.getPath());
            checkAllDownloadTaskStatus(task.getUrl(), 0);
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.d(TAG, "链接：" + task.getUrl() +" 资源下载paused");
            int count = failedAtomicInteger.get() + 1;
            failedAtomicInteger.set(count);
            checkAllDownloadTaskStatus(task.getUrl(), 2);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            Log.d(TAG, "链接：" + task.getUrl() +" 资源下载error,错误信息为： " + e.getMessage());
            //更新失败数量
            int count = failedAtomicInteger.get() + 1;
            failedAtomicInteger.set(count);
            checkAllDownloadTaskStatus(task.getUrl(), 1);
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Log.d(TAG, "链接：" + task.getUrl() +" 资源下载warn ");
            checkAllDownloadTaskStatus(task.getUrl(), 3);
        }
    };

    /**
     *检测所有任务状态
     */
    private void checkAllDownloadTaskStatus(String url, int status){
        int completeCount = completeAtomicInteger.get() + 1;
        int failedCount = failedAtomicInteger.get();
        int totalCount = mDownloadTaskList.size();

        Log.d(TAG, "当前下载成功资源数：" + completeCount);
        notificationDownloadProgress(completeCount, failedCount, totalCount, url, status);
        if (completeCount >= totalCount){
            Log.d(TAG, "全部素材下载完成，发送广播通知");
            clearTaskListData();
            return;
        }
        completeAtomicInteger.set(completeCount);
    }

    /**
     * 通知下载进度
     * @param completeCount 完成数量（包括失败、暂停、警告）
     * @param failedCount 失败数
     * @param total  总下载书
     * @param url    下载链接
     * @param status 当前链接下载状态
     */
    private void notificationDownloadProgress(int completeCount, int failedCount, int total, String url, int status){
        if (downloadListener != null){
            downloadListener.onDownloadProgress(completeCount, failedCount, total, url, status);
        }
    }
}
