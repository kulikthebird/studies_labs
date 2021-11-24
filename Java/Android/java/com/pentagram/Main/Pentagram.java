package com.pentagram.Main;

import android.content.ContentValues;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pentagram.Filter.BitmapHolder;
import com.pentagram.Filter.FilterActivity;
import com.pentagram.R;

public class Pentagram extends Activity {
    private static final int SELECT_PHOTO = 100;
    private static final int MAKE_PHOTO = 10;
    private BitmapHolder bmpHndl;
    Uri mImageCaptureUri1;
    ImageView img;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        img = (ImageView)findViewById(R.id.imageView1);
        bmpHndl = BitmapHolder.getInstance();
    }

    public void onClick(View view) { // onclick zalezny od przycisku
        switch (view.getId()) {
            case R.id.cam:
                open(0);
                break;
            case R.id.load:
                open(1);
                break;
        }
    }

    public void open(int i) { //funckja, dla odpowiedniej wartosci otwiera 0 - aparat 1 - galerie
        Intent intent;
        if (i == 0) {
            /*intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, MAKE_PHOTO);*/
            intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            mImageCaptureUri1 = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri1);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, MAKE_PHOTO);
        }
        else if (i == 1) {
            intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PHOTO);
        }
    }

    public void start(Bitmap bp) { //tu chcialem zrobic wywylanie kolejnej Activity
        //img.setImageBitmap(bp);
        Intent intent = new Intent(Pentagram.this, FilterActivity.class);
        //intent.putExtra("bitmap", bp);
        bmpHndl.setOriginalBitmap(bp);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //tu dostajemy bmp z aparatu lub galerii
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bp;
        try {
            switch(requestCode) {
                case SELECT_PHOTO: // gdy dostalismy odpowiedz z galerii, przerabiamy na bmp
                    if(resultCode == RESULT_OK){
                        Uri selectedImage = data.getData();
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        bp = BitmapFactory.decodeStream(imageStream);
                        start(bp);
                    }
                    break;
                case MAKE_PHOTO: // gdy dostalismy odpowiedz z aparatu, przerabiamy na bmp
                    /*Bundle extras = data.getExtras();
                    bp = (Bitmap) extras.get("data");*/
                    InputStream imageStream = getContentResolver().openInputStream(mImageCaptureUri1);
                    bp = BitmapFactory.decodeStream(imageStream);
                    start(bp);
                    break;
                case 0: // po powrocie z filrow
                    Bitmap b = bmpHndl.getActualBitmap();
                    if(b!= null) {
                        img.setImageBitmap(b);
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        MediaStore.Images.Media.insertImage(getContentResolver(), b, "IMG_" + timeStamp + ".jpg", timeStamp.toString());
                        Toast.makeText(getApplicationContext(), "Picture have been saved", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        } catch (FileNotFoundException x) {}
    }
}
