package net.fkm.tencentcloudpush.utils;


import net.fkm.tencentcloudpush.PushApplication;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 推流拉流地址拼接地址
 */
public class PushUrlToken {

    public static String getUrlToken() {

        String pushKey = ShareUtils.getString(PushApplication.getInstance(), "PushKey", "");
        String urlNO = ShareUtils.getString(PushApplication.getInstance(), "UrlNO", "");

        String urlToken = getSafeUrl(pushKey, urlNO, dateToStamp());
        return urlToken;
    }

    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /*
     * KEY+ streamName + txTime
     */
    private static String getSafeUrl(String key, String streamName, long txTime) {
        String input = new StringBuilder().
                append(key).
                append(streamName).
                append(Long.toHexString(txTime).toUpperCase()).toString();

        String txSecret = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            txSecret = byteArrayToHexString(
                    messageDigest.digest(input.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return txSecret == null ? "" :
                new StringBuilder().
                        append("txSecret=").
                        append(txSecret).
                        append("&").
                        append("txTime=").
                        append(Long.toHexString(txTime).toUpperCase()).
                        toString();
    }

    private static String byteArrayToHexString(byte[] data) {
        char[] out = new char[data.length << 1];
        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }

    /**
     * 十位数的时间戳
     *
     * @return
     * @throws ParseException
     */
    private static long dateToStamp() {
        Long time = System.currentTimeMillis();
        time += 30 * 1000 * 60;
        String res = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
            long ts = date.getTime();
            ts = ts / 1000;
            res = String.valueOf(ts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Long.valueOf(res).longValue();
    }

}
