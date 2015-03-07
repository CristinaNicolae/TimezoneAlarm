package com.cristina.timezonealarm.custom;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cristina.timezonealarm.R;

import java.util.ArrayList;

/**
 * Created by Cristina on 3/7/2015.
 */
public class AlarmsListViewAdapter extends BaseAdapter {


    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    public int resource;
    Alarm tempValues = null;
    int i = 0;

    public int currentPosition = -1;

    public AlarmsListViewAdapter(Activity a, ArrayList<Alarm> d, Resources resLocal, int resource) {

        this.activity = a;
        this.data = d;
        this.res = resLocal;
        this.resource = resource;

    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public void addElement(Alarm alarm)
    {
        data.add(0,alarm);
        notifyDataSetChanged();

    }

    public void updateElement(Alarm alarm)
    {
        data.set(0, alarm);
        notifyDataSetChanged();
    }

    public void remove(int position)
    {
        data.remove(position);
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(resource, parent, false);


        TextView clock = (TextView)  rowView.findViewById(R.id.hour);
        TextView titleTextView = (TextView) rowView.findViewById(R.id.titleTextView);
        ToggleButton toggleButton = (ToggleButton) rowView.findViewById(R.id.activeAlarm);
        Alarm alarm = (Alarm) data.get(position);
        String ampmValue;
        if (alarm.timeOfDay == 1) ampmValue= "PM";
        else ampmValue = "AM";
        clock.setText(String.format("%02d", alarm.numberOfHours) + ":" + String.format("%02d", alarm.numberOfMinutes)  + " " + ampmValue);

        if(!alarm.title.isEmpty())
        {
            titleTextView.setText(alarm.title);
        }
        if(alarm.active)
        {
            toggleButton.setChecked(true);
        }
        else
            toggleButton.setChecked(false);

        return rowView;
    }

}
