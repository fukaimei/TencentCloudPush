package net.fkm.tencentcloudpush.activity;

import android.view.View;
import android.widget.TextView;

import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import net.fkm.tencentcloudpush.R;
import net.fkm.tencentcloudpush.utils.L;
import net.fkm.tencentcloudpush.utils.PushUrlToken;
import net.fkm.tencentcloudpush.utils.ShareUtils;
import net.fkm.tencentcloudpush.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraPusherActivity extends BaseActivity {

    @BindView(R.id.pusher_tx_cloud_view)
    TXCloudVideoView mPusherView;
    @BindView(R.id.tv_play_url)
    TextView tvPlayUrl;

    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;

    // 推流url
    private String bsePushUrl;
    private String pushUrl;

    // 拉流url
    private String basePlayUrl;
    private String playUrl;

    private String urlNO;

    // 鉴权串信息
    private String urlToken;

    // 是否已经推流
    private boolean isPush;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera_pusher;
    }

    @Override
    protected void initView() {

        ButterKnife.bind(this);


        // 如何获取License? 请参考官网指引 https://cloud.tencent.com/document/product/454/34750
        String licenceURL = ShareUtils.getString(this, "LicenceURL", "");
        String licenceKey = ShareUtils.getString(this, "LicenceKey", "");
        TXLiveBase.getInstance().setLicence(this, licenceURL, licenceKey);

        mLivePushConfig = new TXLivePushConfig();
        // 允许双指手势放大预览画面
        mLivePushConfig.setEnableZoom(true);
        // 设置噪声抑制
        mLivePushConfig.enableAEC(true);
        // 开启硬件加速
        mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_HARDWARE);
        // 开启 MainProfile 硬编码模式
        mLivePushConfig.enableVideoHardEncoderMainProfile(true);
        mLivePusher = new TXLivePusher(this);
        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.startCameraPreview(mPusherView);

        isPush = false;

    }

    @Override
    protected void initData() {

        bsePushUrl = ShareUtils.getString(this, "PushDomain", "");
        basePlayUrl = ShareUtils.getString(this, "LiveDomain", "");
        urlNO = ShareUtils.getString(this, "UrlNO", "");
        urlToken = PushUrlToken.getUrlToken();
        pushUrl = bsePushUrl + urlNO + "?" + urlToken;
        playUrl = basePlayUrl + urlNO + "?" + urlToken;
        L.i("拉流地址：" + playUrl);
//        ToastUtil.showToastLong("拉流地址：" + playUrl);

    }

    /**
     * 开始推流
     */
    private void startPush() {

        if (isPush) {
            mLivePusher = new TXLivePusher(this);
            mLivePusher.setConfig(mLivePushConfig);
            mLivePusher.startCameraPreview(mPusherView);
        }

        int ret = mLivePusher.startPusher(pushUrl.trim());
        L.i("ret：" + ret);
        if (ret == -5) {
            L.i("startRTMPPush: license 校验失败");
            ToastUtil.showToastLong("startRTMPPush: license 校验失败");
        }

        tvPlayUrl.setText(String.format("拉流地址：%s%s", basePlayUrl, urlNO));
        L.i(String.format("拉流地址：%s%s", basePlayUrl, urlNO));

    }

    /**
     * 停止推流
     */
    private void stopPush() {

        mLivePusher.stopPusher();
        // 如果已经启动了摄像头预览，请在结束推流时将其关闭
        mLivePusher.stopCameraPreview(true);

        tvPlayUrl.setText("");

        isPush = true;

        ToastUtil.showToast("已停止推流");

    }

    /**
     * 切换摄像头
     */
    private void switchCamera() {

        mLivePusher.switchCamera();

        ToastUtil.showToast("已切换摄像头");

    }

    /**
     * 横竖屏推流切换
     *
     * @param isPortrait
     */
    private void onOrientationChange(boolean isPortrait) {
        if (isPortrait) {
            mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_DOWN);
            mLivePusher.setConfig(mLivePushConfig);
            mLivePusher.setRenderRotation(0);
        } else {
            mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT);
            mLivePusher.setConfig(mLivePushConfig);
            // 因为采集旋转了，为了保证本地渲染是正的，则设置渲染角度为90度。
            mLivePusher.setRenderRotation(90);
        }
    }


    @OnClick({R.id.btn_start_push, R.id.btn_stop_push, R.id.btn_switch_camera
            , R.id.btn_horizontal, R.id.btn_portrait})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_push:
                ToastUtil.showToast("开始推流");
                startPush();
                break;
            case R.id.btn_stop_push:
                stopPush();
                break;
            case R.id.btn_switch_camera:
                switchCamera();
                break;
            case R.id.btn_horizontal:
                // 横屏推流
                onOrientationChange(false);
                ToastUtil.showToast("已横屏推流");
                break;
            case R.id.btn_portrait:
                // 竖屏推流
                onOrientationChange(true);
                ToastUtil.showToast("已竖屏推流");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {

        stopPush();
        ToastUtil.showToast("已停止推流");

        super.onDestroy();
    }

}
