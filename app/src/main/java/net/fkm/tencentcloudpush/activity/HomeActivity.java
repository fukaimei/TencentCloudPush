package net.fkm.tencentcloudpush.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import androidx.core.app.ActivityCompat;

import net.fkm.tencentcloudpush.R;
import net.fkm.tencentcloudpush.utils.CheckNetwork;
import net.fkm.tencentcloudpush.utils.ShareUtils;
import net.fkm.tencentcloudpush.utils.ToastUtil;
import net.fkm.tencentcloudpush.widget.ClearEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    public static HomeActivity instance;
    private static final int REQUEST_STATE_CODE = 1020;


    @BindView(R.id.etPushDomain)
    ClearEditText etPushDomain;
    @BindView(R.id.etLiveDomain)
    ClearEditText etLiveDomain;
    @BindView(R.id.etPushKey)
    ClearEditText etPushKey;

    @BindView(R.id.etLicenceURL)
    ClearEditText etLicenceURL;
    @BindView(R.id.etLicenceKey)
    ClearEditText etLicenceKey;
    @BindView(R.id.etUrlNO)
    ClearEditText etUrlNO;

    @Override
    protected int getLayoutId() {
        instance = this;
        return R.layout.activity_home_layout;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.btnTest})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTest:
                //  判断是否为Android 6.0 以上的系统版本，如果是，需要动态添加权限
                if (Build.VERSION.SDK_INT >= 23) {
                    showPermissions();
                } else {
                    startPushActivity();
                }
                break;
            default:
                break;
        }
    }

    public void startPushActivity() {

        String pushDomain = etPushDomain.getText().toString().trim();
        String liveDomain = etLiveDomain.getText().toString().trim();
        String pushKey = etPushKey.getText().toString().trim();

        String licenceURL = etLicenceURL.getText().toString().trim();
        String licenceKey = etLicenceKey.getText().toString().trim();
        String urlNO = etUrlNO.getText().toString().trim();

        if (TextUtils.isEmpty(pushDomain) || TextUtils.isEmpty(liveDomain)
                || TextUtils.isEmpty(pushKey)) {
            ToastUtil.showToastLong("推流拉流域名或Key值不能为空");
            return;
        }

        if (TextUtils.isEmpty(licenceURL) || TextUtils.isEmpty(licenceKey)) {
            ToastUtil.showToast("licenceURL 和l icenceKey 不能为空");
            return;
        }

        if (TextUtils.isEmpty(urlNO)) {
            ToastUtil.showToast("推拉流房间号不能为空");
            return;
        }

        if (!CheckNetwork.isNetworkConnected(this)) {
            ToastUtil.showToastLong(getString(R.string.network_unavailable));
            return;
        }

        ShareUtils.putString(this, "PushDomain", pushDomain);
        ShareUtils.putString(this, "LiveDomain", liveDomain);
        ShareUtils.putString(this, "PushKey", pushKey);

        ShareUtils.putString(this, "LicenceURL", licenceURL);
        ShareUtils.putString(this, "LicenceKey", licenceKey);
        ShareUtils.putString(this, "UrlNO", urlNO);


        startActivity(new Intent(this, CameraPusherActivity.class));

        finish();

    }

    /**
     * Android 6.0 以上的版本的定位方法
     */
    public void showPermissions() {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
        ) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_STATE_CODE);
        } else {
            startPushActivity();
        }
    }

    //Android 6.0 以上的版本申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case REQUEST_STATE_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPushActivity();
                } else {
                    // 没有获取到权限，做特殊处理
                    ToastUtil.showToastLong("获取相关权限失败，请手动开启方可使用相应的功能");
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
