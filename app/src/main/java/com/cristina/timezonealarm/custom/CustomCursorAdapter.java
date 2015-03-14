package com.cristina.timezonealarm.custom;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Cristina on 3/14/2015.
 */
public class CustomCursorAdapter extends SimpleCursorAdapter {


    public CustomCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }



}
