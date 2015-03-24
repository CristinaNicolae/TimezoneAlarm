package com.cristina.timezonealarm;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cristina.timezonealarm.custom.AnalogClock;
import com.cristina.timezonealarm.data.AlarmsProvider;
import com.cristina.timezonealarm.data.AlarmsTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class TimezoneFragment extends Fragment implements
       AnalogClock.AnalogClockListener

      {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
          private static final int MODE_PRIVATE =1 ;
          private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                updateLocalTimeZoneLabel(getView());
            }
        }
    };
    TimeZone timeZone1;
    TimeZone timeZone2;
    ImageView alarmImageView;
    //Selectable time zones layout variables - START
    RelativeLayout rlClockLeft;
    RelativeLayout rlClockRight;
    RelativeLayout rlInteractionLeft;
    RelativeLayout rlInteractionRight;
    ImageButton imageButtonPlusLeft;
    ImageButton imageButtonPlusRight;
    ImageButton imageButtonDeleteLeft;
    ImageButton imageButtonDeleteRight;
    TextView textViewCityLeft;
    TextView textViewCityRight;
    TextView textViewTimeLeft;
    TextView textViewTimeRight;
    AnalogClock analogClockLeft;
   AnalogClock analogClockRight;
    TimeZone localTZ;
   // Spinner timeZoneSpinner;
    ArrayAdapter<CharSequence> timeZoneAdapter;
    //Selectable time zones layout variables - END
    boolean isLeftClockClicked = false;
    boolean isRightClockClicked = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
          SharedPreferences prefs;
          String tz1;
          String tz2;

    private ShareActionProvider mShareActionProvider;
    public TimezoneFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeZonesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimezoneFragment newInstance(String param1, String param2) {
        TimezoneFragment fragment = new TimezoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setTimeZone1(TimeZone timeZone1) {
        this.timeZone1 = timeZone1;
    }

    public void setTimeZone2(TimeZone timeZone2) {
        this.timeZone2 = timeZone2;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

    }

          public void checkFirstRun() {
          boolean isFirstRun = getActivity().getSharedPreferences("PREFERENCE1", MODE_PRIVATE).getBoolean("isFirstRunTimezones", true);
              if (isFirstRun){

                  // Place your dialog code here to display the dialog
                  new AlertDialog.Builder(getActivity()).setTitle("Welcome!").setMessage("You can change the timezones from settings.").setNeutralButton("OK", null).show();

                  getActivity().getSharedPreferences("PREFERENCE1", MODE_PRIVATE)
                          .edit()
                          .putBoolean("isFirstRunTimezones", false)
                          .apply();
              }


          }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        checkFirstRun();

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);



        bindSelectableTimeZonesLayout(rootView);
        setupLocalTimeZone(rootView);
        addActions();

//        timeZoneAdapter = setupSpinnerAdapter();
//        timeZoneSpinner = (Spinner)rootView.findViewById(R.id.spinner);
//        timeZoneSpinner.setOnItemSelectedListener(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        tz1 = prefs.getString(getString(R.string.pref_tz1_key),
                getString(R.string.pref_tz1_default));
        tz2 = prefs.getString(getString(R.string.pref_tz2_key),
                getString(R.string.pref_tz2_default));


        return rootView;
    }

    private void setupLocalTimeZone(View rootView) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getActivity().registerReceiver(mIntentReceiver, filter, null, null);
        updateLocalTimeZoneLabel(rootView);
    }

    private void updateLocalTimeZoneLabel(View rootView) {
        Calendar cal = Calendar.getInstance();
        localTZ = cal.getTimeZone();
       // final TextView textViewLocalTZ = (TextView) rootView.findViewById(R.id.textViewLocalTZ);
       // textViewLocalTZ.setText(localTZ.getDisplayName());

        final android.widget.AnalogClock clock = (android.widget.AnalogClock) rootView.findViewById(R.id.analogClockCenter);
        ViewTreeObserver vto = clock.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (clock.getWidth() != 0) {
                  //  int margins = getActivity().getResources().getDimensionPixelOffset(R.dimen.clock_margin);
                  //  textViewLocalTZ.setWidth(clock.getWidth() - margins);
                }
            }
        });

    }

    private void bindSelectableTimeZonesLayout(View rootView) {
        //Selectable time zones layout variables - START
        rlClockLeft = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutClockLeft);
        rlClockRight = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutClockRight);

        rlInteractionLeft = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutInteractionLeft);
        rlInteractionRight = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutInteractionRight);

        imageButtonPlusLeft = (ImageButton) rootView.findViewById(R.id.imageButtonPlusLeft);
        imageButtonPlusRight = (ImageButton) rootView.findViewById(R.id.imageButtonPlusRight);

        imageButtonDeleteLeft = (ImageButton) rootView.findViewById(R.id.imageButtonDeleteLeft);
        imageButtonDeleteRight = (ImageButton) rootView.findViewById(R.id.imageButtonDeleteRight);

        textViewCityLeft = (TextView) rootView.findViewById(R.id.textViewCityLeft);
        textViewCityRight = (TextView) rootView.findViewById(R.id.textViewCityRight);
        textViewTimeLeft = (TextView) rootView.findViewById(R.id.textViewTimeLeft);
        textViewTimeRight = (TextView) rootView.findViewById(R.id.textViewTimeRight);

        analogClockLeft = (AnalogClock) rootView.findViewById(R.id.analogClockTimeZoneLeft);
        analogClockRight = (AnalogClock) rootView.findViewById(R.id.analogClockTimeZoneRight);
        android.widget.AnalogClock clock = (android.widget.AnalogClock)rootView.findViewById(R.id.analogClockCenter);
        //Selectable time zones layout variables - END
    }

    private void addActions() {
        analogClockLeft.setAnalogClockListener(this);
        analogClockRight.setAnalogClockListener(this);


        analogClockLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), AlarmsActivity.class);
                myIntent.putExtra("timezone", tz1);
                myIntent.putExtra("gmt", textViewTimeLeft.getText());
                startActivity(myIntent);
            }
        });

        analogClockRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), AlarmsActivity.class);
                myIntent.putExtra("timezone", tz2);
                myIntent.putExtra("gmt", textViewTimeRight.getText());
                startActivity(myIntent);
            }
        });

        imageButtonDeleteLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonDeleteLeft.setVisibility(View.GONE);
                imageButtonPlusLeft.setVisibility(View.VISIBLE);
            }
        });

        imageButtonDeleteRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonDeleteRight.setVisibility(View.GONE);
                imageButtonPlusRight.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onTimeTicked() {
        updateAnalogClocks();
    }

    private void updateAnalogClocks() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int GMTLocalOffset = mTimeZone.getRawOffset();
        long gmtLocal = TimeUnit.HOURS.convert(GMTLocalOffset, TimeUnit.MILLISECONDS);
        int gmtSelectedOffset;

        tz1 = prefs.getString(getString(R.string.pref_tz1_key),
                getString(R.string.pref_tz1_default));
        tz2 = prefs.getString(getString(R.string.pref_tz2_key),
                getString(R.string.pref_tz2_default));

        if (analogClockLeft != null && timeZone1 != null) {
            gmtSelectedOffset = timeZone1.getRawOffset();
            long gmtSelected = TimeUnit.HOURS.convert(gmtSelectedOffset, TimeUnit.MILLISECONDS);
            long hourDiff = gmtSelected - gmtLocal;
            analogClockLeft.setTimeZone(timeZone1);
            textViewCityLeft.setText(tz1.split("/")[1]);
            String gmt = String.valueOf(hourDiff);
//            if (hourDiff == 0) {
//                gmt = "";
//            }
            textViewTimeLeft.setText(gmt);
        }
        if (analogClockRight != null && timeZone2 != null) {
            gmtSelectedOffset = timeZone2.getRawOffset();
            long gmtSelected = TimeUnit.HOURS.convert(gmtSelectedOffset, TimeUnit.MILLISECONDS);
            long hourDiff = gmtSelected - gmtLocal;
            analogClockRight.setTimeZone(timeZone2);
            textViewCityRight.setText(tz2.split("/")[1]);

            String gmt = String.valueOf(hourDiff);
//            if (gmtSelectedOffset == 0) {
//                gmt = "";
//            }
            textViewTimeRight.setText(gmt);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        tz1 = prefs.getString(getString(R.string.pref_tz1_key),
                getString(R.string.pref_tz1_default));
        tz2 = prefs.getString(getString(R.string.pref_tz2_key),
                getString(R.string.pref_tz2_default));

        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareIntent(tz1,tz2));
        }
    }


    private Intent createShareIntent(String tz1, String tz2) {
       Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I am using TimezoneAlarm. I set " + tz1 + " and " + tz2 + " to add alarms! You should check it! https://plus.google.com/+CristinaNicolae/posts/erjLJKMiury " + "#TimezoneAlarm");
        return shareIntent;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        tz1 = prefs.getString(getString(R.string.pref_tz1_key),
                getString(R.string.pref_tz1_default));
        tz2 = prefs.getString(getString(R.string.pref_tz2_key),
                getString(R.string.pref_tz2_default));

        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareIntent(tz1,tz2));
        }
        timeZone1 = TimeZone.getTimeZone(tz1);
            rlClockLeft.setVisibility(View.VISIBLE);
            rlInteractionLeft.setVisibility(View.GONE);

            isLeftClockClicked = false;

            timeZone2 = TimeZone.getTimeZone(tz2);
            rlClockRight.setVisibility(View.VISIBLE);
            rlInteractionRight.setVisibility(View.GONE);

            isRightClockClicked = false;
            updateAnalogClocks();

        }

//          @Override
//          public void onPause()
//          {
//              super.onPause();
//              getActivity().unregisterReceiver(mIntentReceiver);
//          }





}
