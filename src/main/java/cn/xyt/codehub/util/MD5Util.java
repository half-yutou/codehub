package cn.xyt.codehub.util;

import cn.hutool.crypto.digest.DigestUtil;

public class MD5Util {

    /**
     * 使用 MD5 对字符串进行加密
     *
     * @param plainText 明文字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String plainText) {
        return DigestUtil.md5Hex(plainText);
    }

    /**
     * 使用 MD5 对字符串加盐并加密
     *
     * @param plainText 明文字符串
     * @param salt 盐值
     * @return 加密后的字符串
     */
    public static String encryptWithSalt(String plainText, String salt) {
        return DigestUtil.md5Hex(plainText + salt);
    }
}
