package net.fkm.tencentcloudpush.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import net.fkm.tencentcloudpush.PushApplication;


/**
 * 基础工具类
 */
public class BaseUtils {

    /**
     * @return 当前应用的版本Code
     */
    public static int getVersionCode() {
        try {
            PackageManager packageManager = PushApplication.getInstance().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    PushApplication.getInstance().getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
