package com.example.guojian.weekcook.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guojian.weekcook.MyScrollView;
import com.example.guojian.weekcook.R;
import com.example.guojian.weekcook.adapter.MaterialAdapter;
import com.example.guojian.weekcook.adapter.ProcessAdapter;
import com.example.guojian.weekcook.bean.CookBean;
import com.example.guojian.weekcook.bean.MaterialBean;
import com.example.guojian.weekcook.bean.ProcessBean;
import com.example.guojian.weekcook.bean.StepViewPagerBean;
import com.example.guojian.weekcook.dao.DBServices;
import com.example.guojian.weekcook.dao.MyDBServiceUtils;
import com.example.guojian.weekcook.utils.GetBitmapFromSdCardUtil;
import com.example.guojian.weekcook.utils.ImageLoaderUtil;
import com.example.guojian.weekcook.utils.ScreenShotUtils;
import com.example.guojian.weekcook.utils.WaterMaskImageUtil;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class DetailsActivity extends Activity implements MyScrollView.OnScrollListener {
    private static DBServices db;
    private static ArrayList<String> cookIdList = new ArrayList<>();
    private static ProgressDialog dialog;
    private String realIp;
    private CookBean cookBean;
    private ImageView mCollectImg, mDetailsImage;
    private ListView mListViewMaterial, mListViewProcess;
    private LinearLayout mDetailsTitleLinearLayout, mCollectLinearLayout, mButtonBack,
            mShareLinearLayout, mEndMessage, mEndMessageScreenShot;
    private MyScrollView mScrollView;
    private boolean isRed;
    private TextView mTitleName, mName, mContent, mPeopleNum, mCookingTime, mTag, mTotalSteps;
    private final static String TAG = "DetailsActivity";
    private int screenWidth;//手机屏幕宽度
    private int mDetailsTitleHeight;//标题栏的高度
    private int mScrollViewTop;//标题栏的高度
    private List<ProcessBean> processBeenList;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //是否取消收藏
    public void setCancelCollection() {
        //提示对话框
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setView(getLayoutInflater().inflate(R.layout.alert_dialog_view, null));
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "移除",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCollectImg.setImageDrawable(getResources()
                                .getDrawable(R.mipmap.cook_no_collected_white));
                        isRed = false;
                        Toast.makeText(DetailsActivity.this, "已取消收藏~", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
        builder.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
        Button pButton = builder.getButton(DialogInterface.BUTTON_POSITIVE);
        pButton.setTextColor(getResources().getColor(R.color.red_theme));
        Button nButton = builder.getButton(DialogInterface.BUTTON_NEGATIVE);
        nButton.setTextColor(getResources().getColor(R.color.gray));
    }

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareSDK.initSDK(this);

        mContext = getApplicationContext();
        setContentView(R.layout.activity_details);
        initViews();
        mShareLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mScrollView.getChildCount(); i++) {
                    mScrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
                }
                dialog = new ProgressDialog(DetailsActivity.this);
                dialog.setMessage(" 截屏中，请稍等...");
                dialog.show();
                //开始执行AsyncTask，并传入某些数据
                //mEndMessage.setVisibility(View.GONE);
                //mEndMessageScreenShot.setVisibility(View.VISIBLE);
                new ScreenShotTask().execute("New Text");
            }
        });
        mScrollView.setOnScrollListener(this);
        WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onScroll(int scrollY) {

        if (scrollY >= screenWidth - mDetailsTitleHeight) {
            mTitleName.setVisibility(View.VISIBLE);
            mDetailsTitleLinearLayout.setBackgroundColor(getResources()
                    .getColor(R.color.red_theme));
        } else if (scrollY > 0 && scrollY <= screenWidth - mDetailsTitleHeight) {
            updateActionBarAlpha(scrollY * (255 - 25) / (screenWidth - mDetailsTitleHeight) + 25);
            mDetailsTitleLinearLayout.setBackgroundColor(getResources()
                    .getColor(R.color.white_00FFFFFF));
        } else if (scrollY <= screenWidth - mDetailsTitleHeight) {
            mTitleName.setVisibility(View.GONE);
            mDetailsTitleLinearLayout.setBackgroundColor(getResources()
                    .getColor(R.color.white_00FFFFFF));
        }
    }

    /**
     * 窗口有焦点的时候，即所有的布局绘制完毕的时候，我们来获取购买布局的高度和myScrollView距离父类布局的顶部位置
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mDetailsTitleHeight = mDetailsTitleLinearLayout.getHeight();
            //buyLayoutTop = mBuyLayout.getTop();
            mScrollViewTop = mScrollView.getTop();
        }
    }

    public void setActionBarAlpha(int alpha) throws Exception {
        if (mDetailsTitleLinearLayout == null || mScrollView == null) {
            throw new Exception("acitonBar is not binding or bgDrawable is not set.");
        }
        mDetailsTitleLinearLayout.setAlpha(alpha);
        //mActionBar.setBackgroundDrawable(mBgDrawable);
    }

    void updateActionBarAlpha(int alpha) {
        try {
            setActionBarAlpha(alpha);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        mTitleName = (TextView) findViewById(R.id.tv_details_cook_title_name);
        //ImageView mShareImg = (ImageView) findViewById(R.id.iv_share_the_cook_data);
        mScrollView = (MyScrollView) findViewById(R.id.scrollView_details);
        mDetailsTitleLinearLayout = (LinearLayout) findViewById(R.id.ll_details_title);
        mShareLinearLayout = (LinearLayout) findViewById(R.id.ll_share_the_cook_data);
        mButtonBack = (LinearLayout) findViewById(R.id.ll_details_back_to_list);
        mCollectLinearLayout = (LinearLayout) findViewById(R.id.ll_collect_the_cook_data);
        mCollectImg = (ImageView) findViewById(R.id.iv_collection_img);
        mDetailsImage = (ImageView) findViewById(R.id.iv_details_img);
        mName = (TextView) findViewById(R.id.tv_details_cook_name);
        mContent = (TextView) findViewById(R.id.tv_details_cook_content);
        mPeopleNum = (TextView) findViewById(R.id.tv_details_cook_peoplenum);
        mCookingTime = (TextView) findViewById(R.id.tv_details_cook_cookingtime);
        mTag = (TextView) findViewById(R.id.tv_details_cook_tag);
        mListViewMaterial = (ListView) findViewById(R.id.lv_listview_material);
        mListViewProcess = (ListView) findViewById(R.id.lv_listview_process);
        //mlinearLayout = (LinearLayout) findViewById(R.id.linear1);
        mEndMessage = (LinearLayout) findViewById(R.id.ll_end_message);
        mEndMessageScreenShot = (LinearLayout) findViewById(R.id.ll_end_message_screen_shot);
        mTotalSteps = (TextView) findViewById(R.id.tv_cook_total_process);
    }

    @Override
    protected void onResume() {
        //Log.i(TAG, "DetailsActivity ____________onResume()");
        super.onResume();
        initDB();
        Intent intent = this.getIntent();
        cookBean = (CookBean) intent.getSerializableExtra("cookBean01");
        setUpViews();
        realIp = cookBean.getReal_ip();
        Log.i(TAG,"realIp=="+realIp);
        if (realIp.equals("mary") && !cookIdList.contains(cookBean.getId_cook())) {
            mCollectImg.setImageDrawable(getResources()
                    .getDrawable(R.mipmap.cook_no_collected_white));
            isRed = false;
        } else {
            mCollectImg.setImageDrawable(getResources()
                    .getDrawable(R.mipmap.cook_collected_white));
            isRed = true;
        }
        Log.i(TAG,"isRed=="+realIp);
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCollectLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRed) {//删除
                    setCancelCollection();
                } else {
                    mCollectImg.setImageDrawable(getResources()
                            .getDrawable(R.mipmap.cook_collected_white));
                    isRed = true;
                    Toast.makeText(DetailsActivity.this, "收藏成功~", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    protected void onPause() {
        Log.i(TAG, "DetailsActivity_onPause()");
        super.onPause();
        if (realIp.equals("mary") && !cookIdList.contains(cookBean.getId_cook())) {
            if (isRed) {//保存
                MyDBServiceUtils.saveData(cookBean, db);
            }
        } else {
            if (!isRed) {//删除
                MyDBServiceUtils.delectData(cookBean, db);
                cookBean.setReal_ip("mary");
            }
        }
    }

    private void initDB() {
        cookIdList.clear();
        db = MyDBServiceUtils.getInstance(this);
        ArrayList<CookBean> cookBeanList = MyDBServiceUtils.getAllObject(db);
        for (int i = 0; i < cookBeanList.size(); i++) {
            String cook_id = cookBeanList.get(i).getId_cook();
            cookIdList.add(cook_id);
        }
    }

    private void setUpViews() {
        mTitleName.setText(cookBean.getName_cook());
        mName.setText(cookBean.getName_cook());
        String img_url = cookBean.getPic();
        ImageLoaderUtil.setPicBitmap2(mDetailsImage, img_url);
        String mContentString = cookBean.getContent().replace("<br />", "");
        mContent.setText(mContentString);
        String mPeopleNumString = "用餐人数: " + cookBean.getPeoplenum();
        mPeopleNum.setText(mPeopleNumString);
        String mCookingTimeString = "烹饪时间: " + cookBean.getCookingtime();
        mCookingTime.setText(mCookingTimeString);
        String mTagString = "标签: " + cookBean.getTag_cook();
        mTag.setText(mTagString);
        List<MaterialBean> materialBeanList = cookBean.getMaterialBeen();
        MaterialAdapter mMaterialAdapter = new MaterialAdapter(this, materialBeanList);
        mListViewMaterial.setAdapter(mMaterialAdapter);
        setListViewHeightBasedOnChildren1(mListViewMaterial);
        processBeenList = cookBean.getProcessBeen();
        ProcessAdapter mProcessAdapter = new ProcessAdapter(this, processBeenList);
        mListViewProcess.setAdapter(mProcessAdapter);
        setListViewHeightBasedOnChildren1(mListViewProcess);
        mListViewProcess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String positionString = "" + position;
                StepViewPagerBean stepViewPagerBean = new StepViewPagerBean(processBeenList, positionString);
                Intent mIntent = new Intent(DetailsActivity.this, ProcessLargeImgActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("stepViewPagerBean", stepViewPagerBean);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
            }
        });
        String mTotalStepsString = "共" + processBeenList.size() + "步,点击进入大图";
        mTotalSteps.setText(mTotalStepsString);
    }

    public void setListViewHeightBasedOnChildren1(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //params.height += 16;// if without this statement,the listview will be a little short
        listView.setLayoutParams(params);
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "DetailsActivity ____________onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "DetailsActivity ____________onStart()");
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "DetailsActivity ____________onStop()");
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    /**
     * 分享功能
     *
     * @param activityTitle Activity的名字
     * @param msgTitle      消息标题
     * @param msgText       消息内容
     * @param imgPath       图片路径，不分享图片则传null
     */
    public void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/*");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Details Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    String screenShotFileName = null;
    private class ScreenShotTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                if (GetBitmapFromSdCardUtil.hasSdcard()) {
                    try{
                        Bitmap mScreenShotBitmap = ScreenShotUtils.getScrollViewBitmap(mScrollView);
                        Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon96);
                        Bitmap watermarkBitmap = WaterMaskImageUtil.createWaterMaskRightTop(
                                DetailsActivity.this,
                                mScreenShotBitmap, waterBitmap, 10, 10);
                        screenShotFileName = ScreenShotUtils.savePic(watermarkBitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "无SD卡,存储失败!", Toast.LENGTH_SHORT)
                            .show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            //更新UI的操作，这里面的内容是在UI线程里面执行的
            dialog.dismiss();
            //mEndMessage.setVisibility(View.GONE);
            //mEndMessageScreenShot.setVisibility(View.GONE);
            //Toast.makeText(getApplicationContext(), "截图已保存", Toast.LENGTH_SHORT).show();
            //getPopupWindowSharePic();
            if(screenShotFileName != null){
                SharePicPopupWindow();
                mPopupWindowSharePic.showAtLocation(mScrollView, Gravity.BOTTOM, 0, -500);
            }
        }
    }

    private void showSharePic(String platform, String fileName) {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        if (platform != null) {
            oks.setPlatform(platform);
        }
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.cook_details_shared_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.guojian.weekcook");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我正在使用《易厨房》APP分享菜谱,下载地址：http://a.app.qq.com/o/simple.jsp?pkgname=com.guojian.weekcook");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(fileName);//确保SDcard下面存在此张图片

        oks.show(this);
    }

    private PopupWindow mPopupWindowSharePic;
    private void DismissPopupWindow(){
        if (mPopupWindowSharePic != null && mPopupWindowSharePic.isShowing()) {
            mPopupWindowSharePic.dismiss();
            mPopupWindowSharePic = null;
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.alpha = 1f;
            getWindow().setAttributes(params);
        }
    }
    private void SharePicPopupWindow() {
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        final View popupWindow_sharePic = getLayoutInflater()
                .inflate(R.layout.share_pop_window_details_activity, null, false);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindow_sharePic.setFocusable(true); // 这个很重要
        popupWindow_sharePic.setFocusableInTouchMode(true);
        mPopupWindowSharePic = new PopupWindow(popupWindow_sharePic, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        mPopupWindowSharePic.setFocusable(true);
        // 重写onKeyListener
        popupWindow_sharePic.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    DismissPopupWindow();
                }
                return false;
            }
        });

        mPopupWindowSharePic.setAnimationStyle(R.style.AnimationFade_Settings);
        //设置actiivity透明度
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.5f;
        getWindow().setAttributes(params);
        // 点击其他地方消失
        popupWindow_sharePic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DismissPopupWindow();
                return false;
            }
        });

        final TextView mCancel
                = (TextView) popupWindow_sharePic.findViewById(R.id.share_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DismissPopupWindow();
            }
        });

        final LinearLayout mWeChat
                = (LinearLayout) popupWindow_sharePic.findViewById(R.id.wechat);
        mWeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //比如分享到QQ，其他平台则只需要更换平台类名，例如Wechat.NAME则是微信
                Platform plat = ShareSDK.getPlatform(Wechat.NAME);
                if(screenShotFileName != null){
                    showSharePic(plat.getName(), screenShotFileName);
                }
                DismissPopupWindow();
            }
        });

        final LinearLayout mWeChatMoments
                = (LinearLayout) popupWindow_sharePic.findViewById(R.id.wechatmoments);
        mWeChatMoments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //比如分享到QQ，其他平台则只需要更换平台类名，例如Wechat.NAME则是微信
                Platform plat = ShareSDK.getPlatform(WechatMoments.NAME);
                if(screenShotFileName != null){
                    showSharePic(plat.getName(), screenShotFileName);
                }
                DismissPopupWindow();
            }
        });

        final LinearLayout mSinaWeibo
                = (LinearLayout) popupWindow_sharePic.findViewById(R.id.sinaweibo);
        mSinaWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //比如分享到QQ，其他平台则只需要更换平台类名，例如Wechat.NAME则是微信
                Platform plat = ShareSDK.getPlatform(SinaWeibo.NAME);
                if(screenShotFileName != null){
                    showSharePic(plat.getName(), screenShotFileName);
                }
                DismissPopupWindow();
            }
        });

    }

}
