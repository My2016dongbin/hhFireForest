package com.ruyiruyi.rylibrary.ui;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils3 {
    /*   算法/模式/填充 */
    private static final String CipherMode = "AES/ECB/PKCS5Padding";

    /*  创建密钥  */
    public static SecretKeySpec createKey(String password) {
        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuffer sb = new StringBuffer(32);
        sb.append(password);
        while (sb.length() < 32) {
            sb.append("0");
        }
        if (sb.length() > 32) {
            sb.setLength(32);
        }

        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }

    /* 加密字节数据  */
    public static byte[] encryptByKey(byte[] content, String password,SecretKeySpec key) {
        try {
            System.out.println(key);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* 加密字节数据  */
    public static String encrypt(String content, String password) {
        try {
            byte[] content_b = content.getBytes("UTF-8");
            //byte[] content_b = Base64.decode(content,Base64.DEFAULT);
            SecretKeySpec key = createKey(password);
            System.out.println(key);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result_b = cipher.doFinal(content_b);
            //            String result = new String(cipher.doFinal(result_b));
//            byte[] decrypt = decrypt(result.getBytes(), password);
//            String decryptStr = Base64.encodeToString(decrypt, Base64.DEFAULT);
//            Log.e("TAG", "encrypt: decryptStr = " + decryptStr );
            String encode = Base64.encodeToString(cipher.doFinal(result_b), Base64.DEFAULT);
            return encode.replaceAll("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "encrypt: aes e = " + e );
            Log.e("TAG", "encrypt: aes content = " + content );
        }
        return null;
    }

    /*加密(结果为16进制字符串)  */
    public static String encryptByKey(String content, String password,SecretKeySpec key) {
        byte[] data = null;
        try {
            data = content.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = encryptByKey(data, password,key);
        String result = byte2hex(data);
        return result;
    }

    /*加密(结果为16进制字符串)  */
    public static String encrypt16(String content, String password) {
        byte[] data = null;
        try {
            data = content.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = encrypt(Base64.encodeToString(data, Base64.DEFAULT), password).getBytes();
//        data = encrypt(data, password);
        String result = byte2hex(data);
        return result;
    }

    /*解密字节数组*/
    public static byte[] decrypt(byte[] content, String password) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "decrypt: aes 解密 e = " + e );
        }
        return null;
    }

    /*解密16进制的字符串为字符串  */
    public static String decrypt(String content, String password) {
        byte[] data = null;
        try {
            data = hex2byte(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = decrypt(data, password);
        if (data == null) return null;
        String result = null;
        try {
            result = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*字节数组转成16进制字符串  */
    public static String byte2hex(byte[] b) { // 一个字节的数，
        StringBuffer sb = new StringBuffer(b.length * 2);
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            // 整数转成十六进制表示
            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); // 转成大写
    }

    /*将hex字符串转换成字节数组 */
    private static byte[] hex2byte(String inputString) {
        if (inputString == null || inputString.length() < 2) {
            return new byte[0];
        }
        inputString = inputString.toLowerCase();
        int l = inputString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = inputString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }


    /**
     * RSA私钥加密
     *
     * @param data       待加密数据
     * @param privateKey 密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] privateKey) throws Exception {
        // 得到私钥
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey keyPrivate = kf.generatePrivate(keySpec);
        // 数据加密
        Cipher cipher = Cipher.getInstance("ECB_PKCS1_PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, keyPrivate);
        return cipher.doFinal(data);
    }

    /*
     * 描述：随机生成秘钥
     * @param
     * @return java.lang.String
     **/
    public static String getKey() {

        String key = "";

        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");

            kg.init(128);

            //要生成多少位，只需要修改这里即可128, 192或256
            SecretKey sk = kg.generateKey();

            byte[] b = sk.getEncoded();

            key = byteToHexString(b);
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("TAG", "没有此算法: " );
        }
        return key;
    }
    /*
     * 描述：byte数组转化为16进制字符串
     * @param bytes
     * @return java.lang.String
     **/
    public static String byteToHexString(byte[] bytes) {

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {

            String strHex=Integer.toHexString(bytes[i]);

            if(strHex.length() > 3) {

                sb.append(strHex.substring(6));
            } else {

                if(strHex.length() < 2) {

                    sb.append("0" + strHex);
                } else {

                    sb.append(strHex);
                }
            }
        }
        return sb.toString();
    }

    /**
     *  RSA加密
     * @params str 要加密的字符串
     */
    public static String EncryptRSA2(String str,String rsaKey) throws Exception {
        // base64编码的私钥
        byte[] decoded = Base64.decode(rsaKey.getBytes(), Base64.DEFAULT);
        PrivateKey rsaPrivateKey = (PrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA加密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, rsaPrivateKey);
        String outStr = Base64.encodeToString(cipher.doFinal(str.getBytes()), Base64.DEFAULT);
        Log.e("GxPlatform", "加密结果: " + outStr);
        return outStr;
    }
    /**
     *  RSA加密
     */
    public static String EncryptRSA(String str,String rsaKey) throws Exception {
        // base64编码
        byte[] decoded = Base64.decode(rsaKey.getBytes("UTF-8"), Base64.DEFAULT);
//        byte[] decoded = rsaKey.getBytes("UTF-8");

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey keyPublic = kf.generatePublic(keySpec);
        // 加密数据
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPublic);

        String outStr = Base64.encodeToString(cipher.doFinal(str.getBytes("UTF-8")), Base64.DEFAULT);
        String s = outStr.replaceAll("\n", "");
        Log.e("TAG", "EncryptRSA: " + outStr);
        Log.e("GxPlatform", "EncryptRSA 加密结果: " + s);
        return s;
    }
    /**
     *  解密
     * @params str 要解密的字符串
     */
    public static String DecryptRSA(String encryptData,String rsaKey) throws Exception {
        byte[] decode = Base64.decode(encryptData.getBytes("UTF-8"), Base64.DEFAULT);
        // base64编码的私钥
        byte[] decoded = Base64.decode(rsaKey, Base64.DEFAULT);
        RSAPrivateKey rsaPriKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, rsaPriKey);
        String outStr = new String(cipher.doFinal(decode)); // 这一步很关键
        Log.d("GxPlatform", "解密结果: " + outStr);
        return outStr;
    }
}
