package com.ruyiruyi.rylibrary.utils.lyj;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
 
public class StringToFileStream {
 
    // 将String转换为InputStream
    public static InputStream stringToInputStream(String s) {
        return new ByteArrayInputStream(s.getBytes());
    }
 
    // 将String转换为FileInputStream
    public static FileInputStream stringToFileInputStream(String s, String filePath) throws Exception {
        // 将String写入文件
        FileOutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(s.getBytes());
        outputStream.close();
 
        // 返回文件的FileInputStream
        return new FileInputStream(filePath);
    }
 
    // 示例使用
    public static void main(String[] args) {
        String text = "Hello, World!";
        try {
            // 使用ByteArrayInputStream
            InputStream inputStream = stringToInputStream(text);
            // 使用FileInputStream
            FileInputStream fileInputStream = stringToFileInputStream(text, "text.txt");
            // 这里可以使用inputStream和fileInputStream进行后续操作
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}