package com.pentagram.Filter;

/**
 * Created by Tomek on 2015-03-30.
 */
public class RowBean {
    public int icon;
    public String text;

    public RowBean(){

    }

    public RowBean(/*int icon,*/ String title)
    {
        //this.icon = icon;
        this.text = title;
    }
}