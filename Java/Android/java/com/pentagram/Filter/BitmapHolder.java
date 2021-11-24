package com.pentagram.Filter;

import android.graphics.Bitmap;

/**
 * Created by Tomek on 2015-05-26.
 */
public class BitmapHolder {

    private static BitmapHolder ref = null;
    private int n = 0;

    public Bitmap original, previous, actual;
    public Bitmap original_out, actual_out;

    public void setOriginalBitmap(Bitmap bmp)
    {
        original_out = bmp;
        int target_size = 640;

        float scale = Math.min(640/(float)bmp.getWidth(), 640/(float)bmp.getHeight());
        int width = (int)(scale * bmp.getWidth());
        int height = (int)(scale * bmp.getHeight());
        original = Bitmap.createScaledBitmap(bmp, width, height, false);
        previous = Bitmap.createScaledBitmap(bmp, width, height, false);
        actual = Bitmap.createScaledBitmap(bmp, width, height, false);
    }

    public Bitmap getActualBitmap()
    {
        return actual_out;
    }

    BitmapHolder()
    {

    }

    public static BitmapHolder getInstance()
    {
        if(ref == null)
        {
            ref = new BitmapHolder();
        }
        return ref;
    }


}
