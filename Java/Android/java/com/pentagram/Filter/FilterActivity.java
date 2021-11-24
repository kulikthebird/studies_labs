package com.pentagram.Filter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.util.ArrayList;

import com.pentagram.R;


public class FilterActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {

    private int[] operations = new int[20];
    private int op = 0;
    private Spinner spinner;
    private SeekBar bar;
    private ImageView img;
    private ArrayList<RowBean> list = null;
    private String[] filters = new String[] {"Black&White", "Sepia", "Gradient edge", "Laplace edge", "Brightness", "Winieta", "Red boost", "Green boost", "Blue boost"};
    private Filter ft;
    BitmapHolder bmpHndl;

    private int last_filter, last_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Intent intent = getIntent();
        //Bitmap bmp = intent.getParcelableExtra("bitmap");


        bar = (SeekBar)findViewById(R.id.seekBar);
        bar.setOnSeekBarChangeListener(this);
        createList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createList()
    {
        bmpHndl = BitmapHolder.getInstance();


        /*Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.zdj);
        bmpHndl.actual = Bitmap.createScaledBitmap(b, 500, 400, false);
        bmpHndl.previous = Bitmap.createScaledBitmap(b, 500, 400, false);
        bmpHndl.original = Bitmap.createScaledBitmap(b, 500, 400, false);
        */
        ft = new Filter();


        list = new ArrayList<RowBean>();
        RowBean RowBean_data[] = new RowBean[7];
        for(int i=0; i<filters.length; i++)
        {
            list.add(new RowBean(filters[i]));
        }

        img = (ImageView) findViewById(R.id.imageView);
        img.setImageBitmap(bmpHndl.actual);


        RowAdapter adapter = new RowAdapter(this, R.layout.list_row, list);

        spinner = (Spinner)findViewById(R.id.filterSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                bmpHndl.previous = bmpHndl.actual.copy(bmpHndl.actual.getConfig(), true);
                op+=2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser)
    {
        int filter = spinner.getSelectedItemPosition();
        operations[op] = filter;
        operations[op+1] = progress;
        useFilter(filter, progress);
        img.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void onClick1(View view)
    {
        bmpHndl.previous = bmpHndl.original.copy(bmpHndl.original.getConfig(), true);
        bmpHndl.actual = bmpHndl.original.copy(bmpHndl.original.getConfig(), true);
        img.setImageBitmap(bmpHndl.actual);
        op = 0;
        operations[0] = 0;
        operations[1] = 0;
        img.invalidate();
    }

    public void onClick2(View view)
    {
        bmpHndl.actual = bmpHndl.previous.copy(bmpHndl.previous.getConfig(), true);
        img.setImageBitmap(bmpHndl.actual);
        if(op>0)
            op -= 2;
        img.invalidate();
    }

    public void onClick3(View view)
    {
        for(int i=0; i<op+2; i+=2)
        {
            useFilter(operations[i], operations[i+1]);
            bmpHndl.previous = bmpHndl.actual;
        }
        bmpHndl.actual_out = bmpHndl.actual;
        op = 0;
        this.finish();
        // accept
    }

    private void useFilter(int filter, int progress)
    {
        switch(filter)
        {
            case 0:
                // B&W
                ft.BlackWhite(progress);
                break;
            case 1:
                // Sepia
                ft.Sepia(progress);
                break;
            case 2:
                // Gradient edge detector
                ft.Gradient(progress);
                break;
            case 3:
                // Laplace edge detector
                ft.Laplace(progress);
                break;
            case 4:
                // Brightness
                ft.Brightness(progress);
                break;
            case 5:
                // Winieta
                ft.Winieta(progress);
                break;
            case 6:
                // Boosts red colour
                ft.RedBoosts(progress);
                break;
            case 7:
                // Boosts green colour
                ft.GreenBoosts(progress);
                break;
            case 8:
                // Boosts blue colour
                ft.BlueBoosts(progress);
                break;
        }
    }


}
