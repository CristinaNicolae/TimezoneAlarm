package com.cristina.timezonealarm;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cristina.timezonealarm.custom.AnalogClock;

import java.util.TimeZone;

/**
 * Created by Cristina on 3/7/2015.
 */
public class TimezoneFragment extends  Fragment implements AnalogClock.AnalogClockListener{


    AnalogClock analogClock1;
    AnalogClock analogClock2;

    String timeZone1;
    String timeZone2;

    ImageView alarmImageView;

    public void setTimeZone1(String timeZone1) {
        this.timeZone1 = timeZone1;
    }

    public void setTimeZone2(String timeZone2) {
        this.timeZone2 = timeZone2;
    }

    TextView timezoneClock1;
    TextView timezoneClock2;

    ImageView plusLeft;
    ImageView plusRight;

    RelativeLayout rlLeft;
    RelativeLayout rlRight;


   // private OnTimeZonesFragmentInteractionListener mListener;


        public TimezoneFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
           final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            plusLeft = (ImageView) rootView.findViewById(R.id.plusLeft);
            plusRight = (ImageView) rootView.findViewById(R.id.plusRight);

            rlLeft = (RelativeLayout) rootView.findViewById(R.id.rlLeft);
            rlRight = (RelativeLayout) rootView.findViewById(R.id.rlRight);

            analogClock1 = (AnalogClock) rootView.findViewById(R.id.analogClockTimeZone1);
            analogClock2 = (AnalogClock) rootView.findViewById(R.id.analogClockTimeZone2);

            plusLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    plusLeft.setVisibility(View.GONE);
                    timezoneClock1 = (TextView) rootView.findViewById(R.id.timeZoneClock1);
                    timezoneClock1.setText(analogClock1.setTimeZone(TimeZone.getTimeZone("America/New_York")));
                    timeZone1 = "America/New_York";
                    rlLeft.setVisibility(View.VISIBLE);

                }
            });

            plusRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    plusRight.setVisibility(View.GONE);
                    timezoneClock2 = (TextView)  rootView.findViewById(R.id.timeZoneClock2);
                    timezoneClock2.setText(analogClock2.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam")));
                    timeZone2 = "Europe/Amsterdam";
                    rlRight.setVisibility(View.VISIBLE);

                }
            });

            analogClock1.setAnalogClockListener(this);
            analogClock2.setAnalogClockListener(this);

//            alarmImageView = (ImageView) rootView.findViewById(R.id.alarmButton);
//            alarmImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.alarmsSelected();
//                }
//            });



            return rootView;
        }


    @Override
    public void onTimeTicked() {
        if (analogClock1 != null && timeZone1!=null) {
            String timezoneHour1 =  analogClock1.setTimeZone(TimeZone.getTimeZone(timeZone1));
            timezoneClock1.setText(timezoneHour1);
        }
        if (analogClock2 != null && timeZone2!=null) {

            String timezoneHour2 = analogClock2.setTimeZone(TimeZone.getTimeZone(timeZone2));
            timezoneClock2.setText(timezoneHour2);
        }

    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser) {
//            Activity a = getActivity();
//            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
//        }
//    }
}
