package com.ruyiruyi.rylibrary.utils.lyj;
 
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
 
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
 
/**
 * 文件下载工具栏
 */
public class YLDownload {
    private static YLDownload ylDownload;
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
 
    private YLDownload() {
    }
 
    public static YLDownload getInstance() {
        if (ylDownload == null) {
            ylDownload = new YLDownload();
        }
        return ylDownload;
    }
 
    /**
     * 
     * @param url 下载地址
     * @param fileDir 储存下载文件的目录
     * @param fileName 文件名称
     * @param onDownloadListener 下载监听
     */
    public void download(String url, String fileDir, String fileName, OnDownloadListener onDownloadListener) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                onDownloadListener.onDownloadFailed(e);
            }
 
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                byte[] buff = new byte[2048];
                int length;
 
                // 储存下载文件的目录
                File file_dir = new File(fileDir);
                if (!file_dir.exists()) {
                    file_dir.mkdirs();
                }
                File file = new File(file_dir, fileName);
 
                try {
                    inputStream = response.body().byteStream();
                    long total = response.body().contentLength();
                    outputStream = new FileOutputStream(file);
                    length = inputStream.read(buff);
                    long sum = 0;
                    while (length != -1) {
                        outputStream.write(buff, 0, length);
                        sum += length;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中更新进度条
                        onDownloadListener.onDownloading(progress);
                    }
                    outputStream.flush();
                    // 下载完成
                    onDownloadListener.onDownloadSuccess();
                } catch (Exception e) {
                    onDownloadListener.onDownloadFailed(e);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            }
        });
    }
}