package com.example.tcp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPassword {
    public static String hash(String password) {
        try {
            // 使用 SHA-256 演算法
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // 將密碼轉換為 byte 陣列
            byte[] bytes = messageDigest.digest(password.getBytes());

            // 將 byte 陣列轉換為十六進制表示
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : bytes) {
                stringBuilder.append(String.format("%02x", b));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
