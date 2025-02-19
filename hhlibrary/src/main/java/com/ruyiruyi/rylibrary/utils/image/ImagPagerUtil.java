package com.ruyiruyi.rylibrary.utils.image;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ruyiruyi.rylibrary.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImagPagerUtil {
    private List<String> mPicList;
    private Activity mActivity;
    private Dialog dialog;
    private LinearLayout mLL_progress;
    private TextView tv_loadingmsg;
    private int screenWidth;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private TextView tv_img_current_index;
    private TextView tv_img_count;
    private TextView tv_content;
    private LazyViewPager mViewPager;

    public ImagPagerUtil(Activity activity, List<String> mPicList) {
        this.mPicList = mPicList;
        this.mActivity = activity;
        imageLoader = ImageLoader.getInstance();
        setOptions();
        init();
    }
    public ImagPagerUtil(Activity activity, String[] picarr) {
        mPicList = new ArrayList<>();
        for (int i = 0; i < picarr.length; i++) {
            mPicList.add(picarr[i]);
        }
        this.mActivity = activity;
        imageLoader = ImageLoader.getInstance();
        setOptions();
        init();
    }
    /**
     * 设置图片下方的文字
     * @param str
     */
    public void setContentText(String str) {
        if (!TextUtils.isEmpty(str)) {
            tv_content.setText(str);
        }
    }
    public void show() {
        dialog.show();
    }
    private void init() {
        dialog = new Dialog(mActivity, R.style.fullDialog);
        RelativeLayout contentView = (RelativeLayout) View.inflate(mActivity, R.layout.view_dialogpager_img, null);
        mViewPager = ((LazyViewPager) getView(contentView, R.id.view_pager));
        mLL_progress = getView(contentView, R.id.vdi_ll_progress);
        tv_loadingmsg = getView(contentView, R.id.tv_loadingmsg);
        tv_img_current_index = getView(contentView, R.id.tv_img_current_index);
        tv_img_count = getView(contentView, R.id.tv_img_count);
        tv_content = getView(contentView, R.id.tv_content);
        dialog.setContentView(contentView);
        tv_img_count.setText(mPicList.size() + "");
        tv_img_current_index.setText("1");
        int size = mPicList.size();
        ArrayList<ImageView> imageViews = new ArrayList<>();
        ZoomImageView imageView = new ZoomImageView(mActivity);
        imageView.measure(0, 0);
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(screenWidth, display.getHeight());
        imageView.setLayoutParams(marginLayoutParams);
        imageView.setOnClickListener(new View.OnClickListener() {//如果不需要点击图片关闭的需求，可以去掉这个点击事件
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        for (int i = 0; i < size; i++) {
            imageViews.add(imageView);
        }
        initViewPager(imageViews);
    }
    private void initViewPager(ArrayList<ImageView> list) {
        mViewPager.setOnPageChangeListener(new LazyViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tv_img_current_index.setText("" + (position + 1));
            }
        });
        MyImagPagerAdapter myImagPagerAdapter = new MyImagPagerAdapter(list);
        mViewPager.setAdapter(myImagPagerAdapter);
    }

    private ImageView imageView;
    class MyImagPagerAdapter extends PagerAdapter {
        ArrayList<ImageView> mList;
        public MyImagPagerAdapter(ArrayList<ImageView> mList) {
            this.mList = mList;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            imageView = mList.get(position);
            showPic(imageView, mPicList.get(position));
            container.addView(imageView);
            return imageView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }
        @Override
        public int getCount() {
            if (null == mList || mList.size() <= 0) {
                return 0;
            }
            return mList.size();
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
    protected void setOptions() {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }
    private void showPic(ImageView imageView, String url) {
        imageView.setImageBitmap(null);
        /*Glide.with(mActivity).load(url).placeholder(R.drawable.ic_jaizai)
                .error(R.drawable.ic_no_pic).into(imageView);*/
        Glide.with(mActivity).load(url)
                .error(R.drawable.ic_jaizai)
                .placeholder(R.drawable.ic_jaizai).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                String str = url;
                if(!str.contains("tmp")){
                    str += ".tmp";
                }

                final DownloadTask downloadTask = new DownloadTask(mActivity);
                downloadTask.execute(str);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
        dialog.show();
    }


    private File file = null;
    /**
     * 下载文件
     */
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    file = new File(context.getObbDir().getAbsolutePath(),
                            "pic1" + new Date().getTime() + ".jpeg");

                    if (!file.exists()) {
                        // 判断父文件夹是否存在
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                    }

                } else {
                    Toast.makeText(context, "sd卡未挂载",
                            Toast.LENGTH_LONG).show();
                }
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
            } catch (Exception e) {
                System.out.println(e.toString());
                return e.toString();

            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                //Toast.makeText(context, "您未打开SD卡权限" + result, Toast.LENGTH_LONG).show();
            } else {
                //加载图片
                Log.e("ImagePagerUtil", "onPostExecute: " + file.getPath() );
                Glide.with(context).load(file)
                        .error(R.drawable.ic_no_pic)
                        .placeholder(R.drawable.ic_jaizai).into(imageView);
            }

        }
    }

    private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            mLL_progress.setVisibility(View.GONE);
            tv_loadingmsg.setText("");
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(loadedImage);
            }
        }
    }
    @SuppressWarnings("unchecked")
    public static final <E extends View> E getView(View parent, int id) {
        try {
            return (E) parent.findViewById(id);
        } catch (ClassCastException ex) {
            Log.e("ImagPageUtil", "Could not cast View to concrete class \n" + ex.getMessage());
            throw ex;
        }
    }
}