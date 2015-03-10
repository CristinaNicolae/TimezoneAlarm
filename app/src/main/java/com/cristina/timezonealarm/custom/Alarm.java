package com.cristina.timezonealarm.custom;

/**
 * Created by Cristina on 3/7/2015.
 */
public class Alarm {


    public int numberOfHours;
    public int numberOfMinutes;
    public float angle;
    public int timeOfDay;
    public  String title;
    public int active;

    public Alarm(int numberOfHours, int numberOfMinutes, float angle, int timeOfDay, String title, int active)
    {
        this.numberOfHours = numberOfHours;
        this.numberOfMinutes = numberOfMinutes;
        this.angle = angle;
        this.timeOfDay = timeOfDay;
        this.title = title;
        this.active=active;

    }
}
