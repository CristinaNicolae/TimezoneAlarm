package com.cristina.timezonealarm.custom;

import android.app.PendingIntent;

/**
 * Created by Cristina on 3/14/2015.
 */
public class PendingIntents {

    public int id;
    public PendingIntent pendingIntent;

    public PendingIntents(int id, PendingIntent pendingIntent)
    {
        this.id = id;
        this.pendingIntent = pendingIntent;
    }
}
