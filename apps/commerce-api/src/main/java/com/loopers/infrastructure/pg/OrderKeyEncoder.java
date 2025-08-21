package com.loopers.infrastructure.pg;


public class OrderKeyEncoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encode(Long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE62.charAt((int) (value % 62)));
            value /= 62;
        }
        while (sb.length() < 6) { // 최소 6자리 보장
            sb.append('0');
        }
        return sb.reverse().toString();
    }

    public static Long decode(String str) {
        long result = 0;
        for (int i = 0; i < str.length(); i++) {
            result = result * 62 + BASE62.indexOf(str.charAt(i));
        }
        return result;
    }
}
