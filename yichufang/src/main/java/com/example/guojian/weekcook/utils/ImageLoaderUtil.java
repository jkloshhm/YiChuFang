package com.example.guojian.weekcook.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.example.guojian.weekcook.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by guojian on 10/26/16.
 */
public class ImageLoaderUtil {

    public static void setPicBitmap(final ImageView ivPic, final String pic_url) {

        final ImageView mImageView = ivPic;
        String imageUrl = pic_url;
        //ImageSize mImageSize = new ImageSize(100, 100);

        //显示图片的配置
        //显示图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.loading1)
                .showImageOnFail(R.mipmap.error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, mImageView, options);
    }


    public static void setPicBitmap1(final ImageView ivPic, final String pic_url) {

        final ImageView mImageView = ivPic;
        String imageUrl = pic_url;
        ImageSize mImageSize = new ImageSize(230, 230);

        //显示图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();

        ImageLoader.getInstance().loadImage(imageUrl, mImageSize, options,
                new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                mImageView.setImageBitmap(loadedImage);
            }

        });
    }

    public static void setPicBitmap2(final ImageView ivPic, final String pic_url) {

        final ImageView mImageView = ivPic;
        String imageUrl = pic_url;
        //ImageSize mImageSize = new ImageSize(100, 100);

        //显示图片的配置
        //显示图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.loading1)
                .showImageOnFail(R.mipmap.error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.NONE)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, mImageView, options);
    }

    public static void setRoundedBitmap(final ImageView ivPic, final String pic_url) {

        final ImageView mImageView = ivPic;
        String imageUrl = pic_url;
        //ImageSize mImageSize = new ImageSize(100, 100);

        //显示图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.loading1)
                .showImageOnFail(R.mipmap.error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.NONE)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new RoundedBitmapDisplayer(13))
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, mImageView, options);
    }
}
