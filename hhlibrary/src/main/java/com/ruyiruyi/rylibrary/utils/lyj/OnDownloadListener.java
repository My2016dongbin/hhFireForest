package com.ruyiruyi.rylibrary.utils.lyj;

/**
 * 文件下载接口
 */
public interface OnDownloadListener {
 
    void onDownloadFailed(Exception e);
 
    void onDownloadSuccess();
 
    void onDownloading(int i);
}