package com.example.guojian.weekcook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guojian.weekcook.R;
import com.example.guojian.weekcook.utils.AppConfig;
import com.example.guojian.weekcook.utils.DataCleanManager;
import com.example.guojian.weekcook.utils.FileUtil;
import com.example.guojian.weekcook.utils.MethodsCompat;

import java.io.File;
import java.util.Properties;

public class MySettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MySettingsActivity";
    //------****** 缓存相关****----------
    private final int CLEAN_SUC = 1001;
    private final int CLEAN_FAIL = 1002;
    private TextView mMyCacheSize;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CLEAN_FAIL:
                    Toast.makeText(MySettingsActivity.this, "清除失败", Toast.LENGTH_SHORT).show();
                    //ToastUtils.show(SxApplication.getInstance(),"清除失败");
                    break;
                case CLEAN_SUC:
                    Toast.makeText(MySettingsActivity.this, "清除成功", Toast.LENGTH_SHORT).show();
                    //ToastUtils.show(SxApplication.getInstance(),"清除成功");
                    break;
            }
        }
    };


    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);
        TextView mBackToFirstHome = (TextView) findViewById(R.id.tv_back_to_first_home);
        LinearLayout mBack = (LinearLayout) findViewById(R.id.ll_back_me_home);
        LinearLayout mHelpCenter = (LinearLayout) findViewById(R.id.ll_help_center);
        LinearLayout mGiveScore = (LinearLayout) findViewById(R.id.ll__settings_give_app_score);
        LinearLayout mCleanCache = (LinearLayout) findViewById(R.id.ll_settings_clean_cache);
        mMyCacheSize = (TextView) findViewById(R.id.tv_setting_my_cache_size);
        if (mBack != null) mBack.setOnClickListener(this);
        if (mBackToFirstHome != null) mBackToFirstHome.setOnClickListener(this);
        if (mHelpCenter != null) mHelpCenter.setOnClickListener(this);
        if (mGiveScore != null) mGiveScore.setOnClickListener(this);
        if (mCleanCache != null) mCleanCache.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        //mMyCacheSize.setText("");
        calculateCacheSize();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back_me_home:
                finish();
                break;
            case R.id.tv_back_to_first_home:
                Intent intent = new Intent(MySettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ll_help_center:

                break;
            case R.id.ll__settings_give_app_score:

                break;
            case R.id.ll_settings_clean_cache:
                onClickCleanCache();
                break;
            default:
                break;
        }
    }

    private void onClickCleanCache() {
        View view = getLayoutInflater().inflate(R.layout.alert_dialog_view_clean_cache,null);
        AlertDialog builder = new AlertDialog.Builder(MySettingsActivity.this).create();
        builder.setView(view);
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "清除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearAppCache();
                mMyCacheSize.setText("0KB");
            }
        });
        builder.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
        Button pButton= builder.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        pButton.setTextColor(getResources().getColor(R.color.red_theme));
        Button nButton= builder.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        nButton.setTextColor(getResources().getColor(R.color.gray));
        /*builder.setView(view);
        builder.setPositiveButton("清除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearAppCache();
                mMyCacheSize.setText("0KB");
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();*/
    }

    /**
     * 计算缓存的大小
     */
    private void calculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        //File filesDir = this.getFilesDir();
        File cacheDir = this.getCacheDir();
        //fileSize += FileUtil.getDirSize(filesDir);
        Log.i("fileSize====", "" + fileSize);
        Log.i("fileSize====", "" + FileUtil.formatFileSize(fileSize));
        fileSize += FileUtil.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat.getExternalCacheDir(this);
            fileSize += FileUtil.getDirSize(externalCacheDir);
            /*fileSize += FileUtil.getDirSize(new File(
                    org.kymjs.kjframe.utils.FileUtils.getSDCardPath()
                            + File.separator + "KJLibrary/cache"));*/
        }
        if (fileSize > 0)
            cacheSize = FileUtil.formatFileSize(fileSize);
        Log.i("fileSize====", "" + cacheSize);
        mMyCacheSize.setText(cacheSize);
    }

    /**
     * 清除app缓存
     */
    public void myClearAppCache() {
        //DataCleanManager.cleanApplicationData(this,Environment.getExternalStorageDirectory() + "/Cooking/myHeadImg/");
        // 清除数据缓存
        DataCleanManager.cleanFiles(this);
        //DataCleanManager.cleanDatabases(this);
        DataCleanManager.cleanInternalCache(this);
        DataCleanManager.cleanExternalCache(this);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        /*if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            //DataCleanManager.cleanCustomCache(getExternalCacheDir());
            File externalCacheDir = getExternalCacheDir();//"<sdcard>/Android/data/<package_name>/cache/"
            //fileSize += getDirSize(externalCacheDir);
        }*/
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
    }

    /**
     * 清除保存的缓存
     */
    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    myClearAppCache();
                    msg.what = CLEAN_SUC;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = CLEAN_FAIL;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
}
