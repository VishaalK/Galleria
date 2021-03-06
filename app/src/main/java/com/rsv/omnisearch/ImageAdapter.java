package com.rsv.omnisearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] mThumbIds;

    public ImageAdapter(Context c, Integer[] arr) {
        mContext = c;
        mThumbIds = new Integer[arr.length];
        System.arraycopy(arr, 0, mThumbIds, 0, arr.length);
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(320, 320));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }

        int imageID = mThumbIds[position];
        final int pos = position;
        Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(),
                imageID, MediaStore.Images.Thumbnails.MINI_KIND, null);
        imageView.setImageBitmap(bm);

//        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }
}
