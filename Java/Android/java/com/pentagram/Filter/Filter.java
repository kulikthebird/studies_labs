package com.pentagram.Filter;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;


/**
 * Created by Tomek on 2015-05-26.
 */
public class Filter
{

    private byte[] pixel;
    int len = 0;
    int w, h;

    private int uByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    private void swapBitmaps()
    {
        BitmapHolder bmpHndl = BitmapHolder.getInstance();
        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer = ByteBuffer.wrap(pixel);
        bmpHndl.actual.copyPixelsFromBuffer(buffer);
    }

    private void fillBuffer()
    {
        BitmapHolder bmpHndl = BitmapHolder.getInstance();
        Bitmap b = bmpHndl.previous;
        h = bmpHndl.previous.getHeight();
        w = bmpHndl.previous.getWidth();
        len = b.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(len);  //Create a new buffer
        b.copyPixelsToBuffer(buffer);                               //Move the byte data to the buffer
        pixel = buffer.array(); //Get the underlying array containing the data.
    }


    public void BlackWhite(int progress)
    {
        fillBuffer();

        int avg;
        for(int i=0; i<len; i+=4) {
            avg = (int) (( uByteToInt(pixel[i]) + uByteToInt(pixel[i+1]) + uByteToInt(pixel[i+2])) /  3);
            pixel[i] = (byte) ( avg - ( avg - uByteToInt(pixel[i]) )* (1.0 - progress/100.0) );
            pixel[i+1] = (byte) ( avg - ( avg - uByteToInt(pixel[i+1]) )* (1.0 - progress/100.0) );
            pixel[i+2] = (byte) ( avg - ( avg - uByteToInt(pixel[i+2]) )* (1.0 - progress/100.0) );
        }

        swapBitmaps();
    }

    public void Sepia(int progress)
    {
        fillBuffer();

        int avg = 0, avg2 = 0;
        for(int i=0; i<len; i+=4) {
            avg2 = (( uByteToInt(pixel[i]) + uByteToInt(pixel[i+1]) + uByteToInt(pixel[i+2])) / 3);

            avg = avg2;
            pixel[i + 2] = (byte) ( avg - ( avg - uByteToInt(pixel[i+2]) )* (1.0 - progress/100.0) );

            avg = avg2+30;
            avg = (int)( avg - ( avg - uByteToInt(pixel[i+1]) )* (1.0 - progress/100.0) );
            if (avg <= 255)
                pixel[i + 1] = (byte) avg;
            else
                pixel[i + 1] = (byte) 255;

            avg = avg2+60;
            avg = (int) ( avg - ( avg - uByteToInt(pixel[i]) )* (1.0 - progress/100.0) );
            if (avg <= 255)
                pixel[i] = (byte) avg;
            else
                pixel[i] = (byte)255;
        }

        swapBitmaps();
    }

    public void Brightness(int progress)
    {
        fillBuffer();

        int b = (int) ((progress - 50) * 255/50.0) ;
        int temp;
        for(int i=0; i<len; i+=4) {
            temp = uByteToInt(pixel[i]) + b;
            if(temp < 0)
                temp = 0;
            else if(temp > 255)
                temp = 255;
            pixel[i] = (byte) temp;

            temp = uByteToInt(pixel[i+1]) + b;
            if(temp < 0)
                temp = 0;
            else if(temp > 255)
                temp = 255;
            pixel[i+1] = (byte) temp;

            temp = uByteToInt(pixel[i+2]) + b;
            if(temp < 0)
                temp = 0;
            else if(temp > 255)
                temp = 255;
            pixel[i+2] = (byte) temp;

        }

        swapBitmaps();
    }

    public void Laplace(int progress)
    {
        fillBuffer();

        byte[] p = pixel.clone();
        for(int i=4*w+4; i<len-4*w-4; i++) {
            if(i % 4 == 3)
                continue;
            int wq = (  uByteToInt(p[i-4-4*w])*(-1) + uByteToInt(p[i-4*w])*(-1) + uByteToInt(p[i-4*w+1])*0 + uByteToInt(p[i-4])*(-1) + uByteToInt(p[i])* 0
                    + uByteToInt(p[i+4])*1 + uByteToInt(p[i+4*w-4])*0 + uByteToInt(p[i+4*w])*1 + uByteToInt(p[i+4*w+4])*1  );
            wq = wq/6;
            //wq +=128;
            wq = Math.abs(wq);
            pixel[i] = (byte)wq;
        }

        swapBitmaps();
    }

    public void Gradient(int progress)
    {
        fillBuffer();

        byte[] p = pixel.clone();
        for(int i=4*w+4; i<len-4*w-4; i++) {
            if(i % 4 == 3)
                continue;
            int wq = (  uByteToInt(p[i-4-4*w])*(-1) + uByteToInt(p[i-4*w])*(-1) + uByteToInt(p[i-4*w+1])*0 + uByteToInt(p[i-4])*(-1) + uByteToInt(p[i])* 0
                    + uByteToInt(p[i+4])*1 + uByteToInt(p[i+4*w-4])*0 + uByteToInt(p[i+4*w])*1 + uByteToInt(p[i+4*w+4])*1  );
            wq = wq/6;
            wq +=128;
            pixel[i] = (byte)wq;
        }

        swapBitmaps();
    }

    public void RedBoosts(int progress)
    {
        fillBuffer();

        int avg;
        for(int i=0; i<len; i+=4) {
            avg = (int) (( uByteToInt(pixel[i]) + uByteToInt(pixel[i+1]) + uByteToInt(pixel[i+2])) /  3);
            if((uByteToInt(pixel[i]) < uByteToInt(pixel[i+1]) + progress*255/100)  || (uByteToInt(pixel[i]) < uByteToInt(pixel[i+2]) + progress*255/100 ))
            {
                pixel[i] = (byte) avg;
                pixel[i + 1] = (byte) avg;
                pixel[i + 2] = (byte) avg;
            }
        }

        swapBitmaps();
    }

    public void GreenBoosts(int progress)
    {
        fillBuffer();

        int avg;
        for(int i=0; i<len; i+=4) {
            avg = (int) (( uByteToInt(pixel[i]) + uByteToInt(pixel[i+1]) + uByteToInt(pixel[i+2])) /  3);
            if((uByteToInt(pixel[i+1]) < uByteToInt(pixel[i+2]) + progress*255/100)  || (uByteToInt(pixel[i+1]) < uByteToInt(pixel[i]) + progress*255/100 ))
            {
                pixel[i] = (byte) avg;
                pixel[i + 1] = (byte) avg;
                pixel[i + 2] = (byte) avg;
            }
        }

        swapBitmaps();
    }

    public void BlueBoosts(int progress)
    {
        fillBuffer();

        int avg;
        for(int i=0; i<len; i+=4) {
            avg = (int) (( uByteToInt(pixel[i]) + uByteToInt(pixel[i+1]) + uByteToInt(pixel[i+2])) /  3);


            if((uByteToInt(pixel[i+2]) < uByteToInt(pixel[i]) + progress*255/100)  || (uByteToInt(pixel[i+2]) < uByteToInt(pixel[i+1]) + progress*255/100 ))
            {
                pixel[i] = (byte) avg;
                pixel[i + 1] = (byte) avg;
                pixel[i + 2] = (byte) avg;
            }
        }

        swapBitmaps();
    }

    public void Winieta(int progress)
    {
        fillBuffer();

        int sx = w/2;
        int sy = h/2;
        int x, y;
        double r, max, a;
        max = Math.sqrt(sx*sx + sy*sy);
        max /= 3.5 - 3 * (1.0 - progress/100.0);
        for(int i=0; i<len; i+=4)
        {
            x = ((i/4) % w )- sx;
            y = ((i/4) / w) - sy;
            r = Math.sqrt(x*x + y*y);
            if(r > max)
                r = max;
            a = (1.0-r/max);
            pixel[i] = (byte)(uByteToInt(pixel[i]) * a);
            pixel[i+1] = (byte)(uByteToInt(pixel[i+1]) * a);
            pixel[i+2] = (byte)(uByteToInt(pixel[i+2]) * a);
        }

        swapBitmaps();
    }

}
