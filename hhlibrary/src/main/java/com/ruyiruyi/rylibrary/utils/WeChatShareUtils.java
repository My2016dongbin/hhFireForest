package com.ruyiruyi.rylibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

// todo 微信分享工具类
public class WeChatShareUtils {
//    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001; // 微信4.2以上支持
    private static final int TIMELINE_SUPPORTED_VERSION = 0x27000D00;  // 判断微信版本是否为7.0.13及以上
    private static final String id = "xxxxxx"; //小程序原始id
    private static final String pageUrl = "xxxxxx"; //兼容低版本的网页链接


    //IWXAPI是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private Context context;
    public static WeChatShareUtils weChatShareUtils;

    public static WeChatShareUtils getInstance(Context context){
        if(weChatShareUtils ==null){
            weChatShareUtils = new WeChatShareUtils();
        }
        if(weChatShareUtils.api !=null){
            weChatShareUtils.api.unregisterApp();;
        }
        weChatShareUtils.context = context;
        weChatShareUtils.regToWx();
        return weChatShareUtils;
    }

     // todo 注册应用id到微信
    private void regToWx(){
        //通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(context, Config.APPID,true);
        //将应用的appId注册到微信
        api.registerApp(Config.APPID);
    }

    /**
     *todo 分享文字到朋友圈或者好友
     * @param text 文本内容
     * @param scene 分享方式：好友还是朋友圈
     * @return
     */
    public boolean shareText(String text,int scene){
        //初始化一个WXTextObject对象，填写分享的文本对象
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;
        return share(textObject,text,scene);
    }

    /**
     * todo 分享图片到朋友圈或者好友
     * @param bmp  图片的Bitmap对象
     * @param scene 分享方式：好友 WXSceneSession 还是朋友圈 WXSceneTimeline,收藏 WXSceneFavorite
     * @return
     */
    public boolean sharePic(Bitmap bmp, int scene){
        //初始化一个WXImageObject对象
        WXImageObject imageObject = new WXImageObject();
        //设置缩略图
        Bitmap thump = Bitmap.createScaledBitmap(bmp,300,300,true);
        bmp.recycle();
        return share(imageObject,thump,scene);
    }

    /**
     * todo 分享网页到朋友圈或者好友，视频和音乐的分享和网页大同小异，只是创建的对象不同。
     * @param url 网页的url
     * @param title 显示分享网页的标题
     * @param thumb 图片的缩略图
     * @param description 对网页的描述
     * @param scene 分享方式：好友还是朋友圈
     * @return
     */
    public boolean shareUrl(String url, String title, Bitmap thumb, String description, int scene) {
        //初试话一个WXWebpageObject对象，填写url
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = url;
        //设置缩略图
        Bitmap tmb = Bitmap.createScaledBitmap(thumb,150,150,true);
        thumb.recycle();
        return share(webPage, title, tmb, description, scene);
    }

    /**
     * todo 分享小程序类型
     * @param url
     * @param title
     * @param thumb
     * @param description
     * @param scene 只支持分享给微信好友
     * @return
     */
    public boolean shareMiniProgram(String url, String title, Bitmap thumb, String description, int scene) {
        //初试话一个WXMiniProgramObject对象，填写url
        WXMiniProgramObject wxMiniProgramObject = new WXMiniProgramObject();
        wxMiniProgramObject.webpageUrl = pageUrl; //兼容低版本的网页链接
        wxMiniProgramObject.miniprogramType = WXMiniProgramObject.MINIPROGRAM_TYPE_TEST; //小程序类型，测试版
        wxMiniProgramObject.userName = id;  //小程序原始id
        wxMiniProgramObject.path = url; //小程序的path

        //设置缩略图
        Bitmap tmb = Bitmap.createScaledBitmap(thumb,300,300,true);
        thumb.recycle();
        return share(wxMiniProgramObject, title, tmb, description, scene);
    }


    private boolean share(WXMediaMessage.IMediaObject mediaObject, Bitmap thumb, int scene) {
        return share(mediaObject, null, thumb, null, scene);
    }

    private boolean share(WXMediaMessage.IMediaObject mediaObject, String description, int scene) {
        return share(mediaObject, null, null, description, scene);
    }

    private boolean share(WXMediaMessage.IMediaObject mediaObject,String title,Bitmap thumb,String description, int scene){
        WXMediaMessage msg = new WXMediaMessage(mediaObject);
        if(title !=null){
            msg.title = title;
        }
        if(description !=null){
            msg.description = description;
        }
        if(thumb !=null){
            msg.thumbData = bmpToByteArray(thumb, true);
        }
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;
        return api.sendReq(req);
    }

    //判断是否支持转发到朋友圈
    //微信4.2以上支持，如果需要检查微信版本支持API的情况， 可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈
    //微信版本：当且仅当通过 IWXAPI.getWXAppSupportAPI() 接口获取到的值 >= 0x27000D00(7.0.13以上），才能支持FileProvider的方式进行分享。
    public boolean isSupportWX(){
        int wxSdkVersion = api.getWXAppSupportAPI();
        return wxSdkVersion >= TIMELINE_SUPPORTED_VERSION;
    }

    // todo Bitmap转换为 byte数组
    private byte[] bmpToByteArray(final Bitmap bmp,final boolean needRecycle){
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        }  else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas =  new Canvas(localBitmap);

        while ( true) {
            localCanvas.drawBitmap(bmp,  new Rect(0, 0, i, j),  new Rect(0, 0,i, j),  null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream =  new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            }  catch (Exception e) {
                // F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }
}

