package com.hbbmobile.bsnapp.base.view;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 10/10/2016.
 */
public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private static ImageLoader instance;

    private ImageLoader() {

    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    public void loadImage(final Activity activity, final String url, final ImageView imageView) {

        try {
            Glide.with(activity)
                    .load(url)
                    .error(R.drawable.avatar)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(imageView);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }


    }

    public void loadImageCaching(final Activity activity, final String url, final ImageView imageView) {

        try {
            Glide.with(activity)
                    .load(url)
                    .error(R.drawable.avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(imageView);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }


    }
}
