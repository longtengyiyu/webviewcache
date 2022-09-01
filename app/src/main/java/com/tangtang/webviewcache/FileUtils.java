package com.tangtang.webviewcache;

import android.util.Log;

import com.tangtang.webviewcache.bean.Resource;

import java.io.File;
import java.util.List;

public class FileUtils {

    public static boolean isVideo(String url){
        String name = getFileName(url);
        return hitSuffix(name, ".mp4", ".avi", ".flv", ".mov", ".rmvb", ".3pg",
                ".rm", ".rtsp", ".rtmp", ".qt", ".asf", ".mpeg", ".mpg");
    }

    public static boolean isPicture(String url){
        String name = getFileName(url);
        return hitSuffix(name, ".jpg", ".jpeg", ".png", ".bmp");
    }

    public static boolean isFileExist(String filePath){
        return filePath != null && !filePath.isEmpty() && new File(filePath).exists();
    }

    public static boolean isFolderExists(String folder) {
        File file = new File(folder);
        return file.exists() || file.mkdirs();
    }

    public static String getLocalPath(String name){
        return Contacts.APP_ROOT_PATH + name;
    }

    public static String getFileName(String url) {
        int nameIndex = url.lastIndexOf("/");
        return url.substring(nameIndex + 1);
    }

    private static boolean hitSuffix(String suf, String... suffixes) {
        for (String line : suffixes) {
            if (suf.endsWith(line)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isContainsUrl(List<Resource>resourceList , String url){
        if (resourceList == null || resourceList.isEmpty()){
            return false;
        }
        for (Resource resource :resourceList) {
            if (resource.getUrl().equals(url)){
                return true;
            }
        }
        return false;
    }
}
