package net.fkm.tencentcloudpush;

import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.multidex.MultiDexApplication;


public class PushApplication extends MultiDexApplication {

    private static PushApplication pushApplication;

    public static PushApplication getInstance() {
        return pushApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pushApplication = this;
        initTextSize();
    }

    /**
     * 使其系统更改字体大小无效
     */
    private void initTextSize() {
        Resources res = getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

}
