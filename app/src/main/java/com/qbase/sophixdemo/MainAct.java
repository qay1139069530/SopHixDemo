package com.qbase.sophixdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.taobao.sophix.SophixManager;

public class MainAct extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 0;

    private TextView mTvStatus;
    private String mStatusStr = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        mTvStatus = findViewById(R.id.tv_status);
        if (TextUtils.isEmpty(mStatusStr)) {
            mStatusStr = "第一次没有数据";
        }

        Stu stu = new Stu("张三");
        mStatusStr +=stu.toString();
        updateConsole(mStatusStr);

        if (Build.VERSION.SDK_INT >= 23) {
            requestExternalStoragePermission();
        }
    }


    /**
     * 如果本地补丁放在了外部存储卡中, 6.0以上需要申请读外部存储卡权限才能够使用. 应用内部存储则不受影响
     */

    private void requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    updateConsole("local external storage patch is invalid as not read external storage permission");
                }
                break;
            default:
        }
    }

    /**
     * 更新监控台的输出信息
     *
     * @param content 更新内容
     */
    private void updateConsole(String content) {
        mStatusStr += content + "\n";
        if (mTvStatus != null) {
            mTvStatus.setText(mStatusStr);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.serverFix:
                SophixManager.getInstance().queryAndLoadNewPatch();
                break;
            case R.id.clear:
                SophixManager.getInstance().cleanPatches();
                updateConsole("clean patches");
                break;
            default:
                break;
        }
    }
}