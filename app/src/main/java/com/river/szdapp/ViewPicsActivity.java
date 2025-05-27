package com.river.szdapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


import static android.os.Environment.getExternalStorageDirectory;


public class ViewPicsActivity extends Activity implements View.OnClickListener{

    private Button pre,next,delete,back;
    private ImageView imageView;
    private ArrayList<File> images;
    private int i = 0;
    private String imagePathString="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_pics);
        setTitle("查看图片");
        Intent intent=getIntent();
        imagePathString=intent.getStringExtra("path");


        this.images = getPath();    //获取本地图片集合
        init();
    }


    /**
     * 对布局文件进行初始化
     * */
    private void init(){
        pre = (Button) findViewById(R.id.pre);
        pre.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);

        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.imageview);
        showImage(0);
    }


    /**
     * 为按键添加监听事件
     * 实际上就是控制ArrayList集合中指针的数据来显示图片
     * 速度较慢，每次都需要重新读取。
     * */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.pre:
                i --;
                if(i < 0) {
                    i = 0;
                    Toast.makeText(this, "已经是第一张了", Toast.LENGTH_SHORT).show();
                    break;
                }
                showImage(i);
                break;
            case R.id.next:
                i ++;
                if(i >= images.size()) {
                    i = images.size() - 1;
                    Toast.makeText(this, "已经是最后一张了", Toast.LENGTH_SHORT).show();
                    break;
                }
                showImage(i);
                break;

            case R.id.delete:
//                i ++;
//                if(i >= images.size()) {
//                    i = images.size() - 1;
//                    Toast.makeText(this, "已经是最后一张了", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//                showImage(i);
//                break;
                //先删除
                File tobeDelete=this.images.get(i);
                tobeDelete.delete();
                this.images = getPath();
                if(this.images.isEmpty())
                {
                    ViewPicsActivity.this.finish();
                }
                else
                {
                    showImage(0);
                    break;
                }

            case R.id.back:
                //Toast.makeText(this, "i am back", Toast.LENGTH_SHORT).show();
                this.finish();
                break;

        }
    }

    /**
     * 通过文件获取流，将流转化为Bitmap对象
     * */
    private Bitmap getBMP(File file){
        BufferedInputStream in = null;
        Bitmap BMP = null;
        try{
            in = new BufferedInputStream(new FileInputStream(file));
            BMP = BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "程序异常！", Toast.LENGTH_SHORT).show();
        } finally {
            if(in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return BMP;
    }

    /**
     * 将指定目录下的指定格式的文件存到入集合中。
     * */
    private ArrayList<File> getPath(){
        ArrayList<File> al = new ArrayList<File>();
//        File myphotos = new File(Environment.getExternalStorageDirectory()
//                + "/szd/3_4_2424/");
        File myphotos = new File(this.imagePathString);
        File[] files = myphotos.listFiles();
        for (File file : files) {
            if(file.exists() && file.isFile() && isImage(file)){
                al.add(file);
            }
        }
        return al;
    }


    /**
     * 设置文件过滤器，只需要指定格式的文件
     * */
    private boolean isImage(File file){
        String[] strs = {".jpg",".png,"};
        for (String str : strs) {
            return file.getName().endsWith(str);
        }
        return false;
    }

    /**
     * 设置文件的
     * */
    private void showImage(int i){
        imageView.setImageBitmap(getBMP(this.images.get(i)));
    }
}


