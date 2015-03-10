package com.cristina.timezonealarm;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        final TextView textViewLocalTZ = (TextView) rootView.findViewById(R.id.textViewLocalTZ);
        textViewLocalTZ.setText(localTZ.getDisplayName());

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
                startActivity(myIntent);
            }
        });

        analogClockRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), AlarmsActivity.class);
                myIntent.putExtra("timezone", tz2);
                startActivity(myIntent);
            }
        });

//        imageButtonPlusLeft.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("TimeZonesFragment", "left plus: onClick");
//                isLeftClockClicked = true;
//                if (timeZoneSpinner.getAdapter() == null) {
//                    timeZoneSpinner.setAdapter(timeZoneAdapter);
//                }
//                timeZoneSpinner.performClick();
//            }
//        });
//
//        imageButtonPlusRight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isRightClockClicked = true;
//
//                if (timeZoneSpinner.getAdapter() == null) {
//                    timeZoneSpinner.setAdapter(timeZoneAdapter);
//                }
//                timeZoneSpinner.performClick();
//            }
//        });

//        analogClockLeft.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                rlClockLeft.setVisibility(View.GONE);
//                rlInteractionLeft.setVisibility(View.VISIBLE);
//                imageButtonDeleteLeft.setVisibility(View.VISIBLE);
//                imageButtonPlusLeft.setVisibility(View.GONE);
//                return true;
//            }
//        });
//
//        analogClockRight.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                rlClockRight.setVisibility(View.GONE);
//                rlInteractionRight.setVisibility(View.VISIBLE);
//                imageButtonDeleteRight.setVisibility(View.VISIBLE);
//                imageButtonPlusRight.setVisibility(View.GONE);
//                return true;
//            }
//        });

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

//    private ArrayAdapter<CharSequence> setupSpinnerAdapter() {
//        ArrayAdapter<CharSequence> adapter =
//                new ArrayAdapter <CharSequence> (getActivity(), android.R.layout.simple_spinner_item );
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////        String[]TZ = TimeZone.getAvailableIDs();
////        ArrayList<String> TZ1 = new ArrayList<String>();
////        for(int i = 0; i < TZ.length; i++) {
////            if(!(TZ1.contains(TimeZone.getTimeZone(TZ[i]).getDisplayName()))) {
////                TZ1.add(TimeZone.getTimeZone(TZ[i]).getDisplayName());
////            }
////        }
//
////        Collections.sort(TZ1);
//        String[] timezones = getResources().getStringArray(R.array.timezonesList);
//
//        for(int i = 0; i < timezones.length; i++) {
//            adapter.add(timezones[i]);
//        }
//        return adapter;
//    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        String selectedId = (String) (parent
//                .getItemAtPosition(position));
//        Log.d("TimeZonesFragment", "time zone spinner: onItemSelected");
//
//
//        if (isLeftClockClicked) {
//            timeZone1 = TimeZone.getTimeZone(selectedId);
//            rlClockLeft.setVisibility(View.VISIBLE);
//            rlInteractionLeft.setVisibility(View.GONE);
//            textViewCityLeft.setText(selectedId);
//            updateAnalogClocks();
//            isLeftClockClicked = false;
//        }
//
//        if (isRightClockClicked) {
//            timeZone2 = TimeZone.getTimeZone(selectedId);
//            rlClockRight.setVisibility(View.VISIBLE);
//            rlInteractionRight.setVisibility(View.GONE);
//            textViewCityRight.setText(selectedId);
//            updateAnalogClocks();
//            isRightClockClicked = false;
//        }
//
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }

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
            textViewCityLeft.setText(tz1);
            String gmt = String.valueOf(hourDiff);
            if (hourDiff == 0) {
                gmt = "";
            }
            textViewTimeLeft.setText(gmt);
        }
        if (analogClockRight != null && timeZone2 != null) {
            gmtSelectedOffset = timeZone2.getRawOffset();
            long gmtSelected = TimeUnit.HOURS.convert(gmtSelectedOffset, TimeUnit.MILLISECONDS);
            long hourDiff = gmtSelected - gmtLocal;
            analogClockRight.setTimeZone(timeZone2);
            textViewCityRight.setText(tz2);

            String gmt = String.valueOf(hourDiff);
            if (gmtSelectedOffset == 0) {
                gmt = "";
            }
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

        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            // nada
        }
    }


    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I am using this amazing app!");
        return shareIntent;
    }

    @Override
    public void onStart()
    {
        super.onStart();


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



}
