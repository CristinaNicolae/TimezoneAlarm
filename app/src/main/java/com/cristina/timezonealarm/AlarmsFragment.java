package com.cristina.timezonealarm;

/**
 * Created by Cristina on 3/7/2015.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cristina.timezonealarm.custom.Alarm;
import com.cristina.timezonealarm.custom.AlarmsListViewAdapter;
import com.cristina.timezonealarm.data.AlarmsProvider;
import com.cristina.timezonealarm.data.AlarmsTable;
import com.cristina.timezonealarm.swipelistview.SwipeDismissListViewTouchListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


public class AlarmsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public AlarmsFragment() {
    }

    ImageView needle;
    ImageView back;
    PointF touchPoint;
    TextView time;

    Alarm alarm;
    Button addNewAlarm;
    Button setAlarm;
    boolean isTouchable = false;
    ListView alarmListView;
    AlarmsListViewAdapter listViewAdapter;
    ArrayList<Alarm> alarmArrayList = new ArrayList<Alarm>();
    ImageView upImage;
    ImageView downImage;
    ImageView timeZoneImage;
    Uri alarmUri;
    private SimpleCursorAdapter adapter;


    private ArrayAdapter<String> _adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarms, container, false);


        alarmUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState
                .getParcelable(AlarmsProvider.CONTENT_ITEM_TYPE);


        needle = (ImageView) rootView.findViewById(R.id.needle);
        back = (ImageView) rootView.findViewById(R.id.back);
        time = (TextView) rootView.findViewById(R.id.timeTextView);
        addNewAlarm = (Button) rootView.findViewById(R.id.addNewAlarm);
        setAlarm = (Button) rootView.findViewById(R.id.setAlarm);
        alarmListView = (ListView) rootView.findViewById(R.id.alarmListView);

        String[] from = new String[] { AlarmsTable.COLUMN_TITLE };
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.titleTextView };

        getLoaderManager().initLoader(0, null,this);
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.alarm_list_view_item, null, from,
                to, 0);

        alarmListView.setAdapter(adapter);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        alarmListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {

                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    listViewAdapter.remove(position);
                                }
                                listViewAdapter.notifyDataSetChanged();
                                if (alarmArrayList.size() == 3)
                                    downImage.setVisibility(View.VISIBLE);
                                else downImage.setVisibility(View.INVISIBLE);
                            }
                        });
        alarmListView.setOnTouchListener(touchListener);

        alarmListView.setOnScrollListener(touchListener.makeScrollListener());


        Resources res = getResources();
        listViewAdapter = new AlarmsListViewAdapter(getActivity(), alarmArrayList, res, R.layout.alarm_list_view_item);
        alarmListView.setAdapter(listViewAdapter);
        upImage = (ImageView) rootView.findViewById(R.id.upImage);
        downImage = (ImageView) rootView.findViewById(R.id.downImage);

        setAlarm.setOnClickListener(new View.OnClickListener() {
            EditText editText;
            TextView titleTextView;
            ToggleButton toggleButton;
            InputMethodManager imm;

            @Override
            public void onClick(View v) {
                listViewAdapter.addElement(alarm);
                listViewAdapter.notifyDataSetChanged();
                if (alarmArrayList.size() == 4)
                    downImage.setVisibility(View.VISIBLE);
                else downImage.setVisibility(View.INVISIBLE);
                alarmListView.post(new Runnable() {
                    @Override
                    public void run() {
                        View rowView = alarmListView.getChildAt(0);
                        editText = (EditText) rowView.findViewById(R.id.title);
                        titleTextView = (TextView) rowView.findViewById(R.id.titleTextView);
                        toggleButton = (ToggleButton) rowView.findViewById(R.id.activeAlarm);


                        editText.setVisibility(View.VISIBLE);
                        editText.requestFocus();

                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {

                                    alarm.title = editText.getText().toString();
                                    alarm.active = 1;
                                    editText.setVisibility(View.GONE);
                                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                    titleTextView.setText(alarm.title);
                                    listViewAdapter.updateElement(alarm);
                                    toggleButton.setChecked(true);

                                    Intent intent = getActivity().getIntent();

                                    ContentValues values = new ContentValues();
                                    values.put(AlarmsTable.COLUMN_HOUR, alarm.numberOfHours);
                                    values.put(AlarmsTable.COLUMN_MINUTE, alarm.numberOfMinutes);
                                    values.put(AlarmsTable.COLUMN_ANGLE, alarm.angle);
                                    values.put(AlarmsTable.COLUMN_TIMEOFDAY, alarm.timeOfDay);
                                    values.put(AlarmsTable.COLUMN_TITLE, alarm.title);
                                    values.put(AlarmsTable.COLUMN_ACTIVE, alarm.active);
                                    values.put(AlarmsTable.COLUMN_TIMEZONEID, intent.getStringExtra("timezone"));


//                                    alarmUri = getActivity().getContentResolver().insert(AlarmsProvider.CONTENT_URI, values);
//
//
//                                    Bundle extras = getActivity().getIntent().getExtras();
//                                    Uri todoUri = Uri.parse(AlarmsProvider.CONTENT_URI + "/1");
//
//
//
//                                    try {
//                                        fillData(todoUri);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }

                                    Intent intent2 = new Intent(getActivity(), MyBroadcastReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent2,PendingIntent.FLAG_UPDATE_CURRENT);


                                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(System.currentTimeMillis());
                                    if(alarm.timeOfDay==1)
                                    calendar.set(Calendar.HOUR_OF_DAY, alarm.numberOfHours+12);
                                    else
                                    calendar.set(Calendar.HOUR_OF_DAY, alarm.numberOfHours);
                                    calendar.set(Calendar.MINUTE, alarm.numberOfMinutes);

                                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                            AlarmManager.INTERVAL_DAY, pendingIntent);


                                    return true;
                                }
                                return false;
                            }
                        });


                    }
                });


                needle.setVisibility(View.INVISIBLE);
                isTouchable = false;
                addNewAlarm.setVisibility(View.VISIBLE);
                if (alarmArrayList.size() >= 5) {
                    addNewAlarm.setEnabled(false);
                    addNewAlarm.setTextColor(Color.DKGRAY);
                } else {
                    addNewAlarm.setEnabled(true);
                    addNewAlarm.setTextColor(getActivity().getApplicationContext().getResources().getColor(R.color.ThemeYellow));
                }
                setAlarm.setVisibility(View.INVISIBLE);
                time.setVisibility(View.INVISIBLE);

            }
        });

        addNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.setVisibility(View.VISIBLE);
                needle.setVisibility(View.VISIBLE);
                needle.setPivotX(back.getWidth() / 2);
                needle.setPivotY(back.getHeight() / 2);
                needle.setRotation(0);
                alarm = new Alarm(12, 0, 0, -1, "", 0);
                time.setText(String.format("%02d", alarm.numberOfHours) + ":" + String.format("%02d", alarm.numberOfMinutes) + "  PM");
                isTouchable = true;
                addNewAlarm.setVisibility(View.INVISIBLE);
                setAlarm.setVisibility(View.VISIBLE);

            }
        });

        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (isTouchable) {
                    final float centerXOfImageOnScreen = back.getWidth() / 2;
                    final float centerYOfImageOnScreen = back.getHeight() / 2;

                    final PointF viewCenter = new PointF(centerXOfImageOnScreen, centerYOfImageOnScreen);

                    touchPoint = new PointF(event.getX(), event.getY());

                    double xDist = (touchPoint.x - viewCenter.x);
                    double yDist = (touchPoint.y - viewCenter.y);

                    float angle = (float) Math.abs(Math.atan(xDist / yDist));

                    if (xDist < 0 && yDist >= 0) {
                        angle = (float) (Math.PI - angle);
                    }
                    if (xDist >= 0 && yDist > 0) {
                        angle = (float) (Math.PI + angle);
                    }
                    if (xDist > 0 && yDist <= 0) {
                        angle = (float) (2 * Math.PI - angle);
                    }

                    angle = -(float) Math.toDegrees(angle);

                    needle.setPivotX(back.getWidth() / 2);
                    needle.setPivotY(back.getHeight() / 2);
                    needle.setRotation(angle);

                    if (angle == 0) {
                        angle = 360;
                    }

                    if (Math.abs(alarm.angle - angle) > 180) {
                        if (angle < alarm.angle) {
                            if (alarm.angle - Math.abs(alarm.angle - angle) < 0.0) {
                                alarm.timeOfDay = -alarm.timeOfDay;
                            }
                        }

                        if (angle > alarm.angle) {
                            if (alarm.angle - Math.abs(alarm.angle - angle) < -180 * 2.0) {
                                alarm.timeOfDay = -alarm.timeOfDay;
                            }
                        }
                    }

                    int numberOfMinutes = (int) ((360 + angle) * 2);
                    int numberOfHours = numberOfMinutes / 60;
                    numberOfMinutes = numberOfMinutes - 60 * numberOfHours;

                    if (numberOfHours == 0) {
                        numberOfHours = 12;
                    }

                    alarm.numberOfHours = numberOfHours;
                    alarm.numberOfMinutes = numberOfMinutes;
                    alarm.angle = angle;

                    String ampmValue;
                    if (alarm.timeOfDay == 1) ampmValue = "PM";
                    else ampmValue = "AM";


                    time.setText(String.format("%02d", numberOfHours) + ":" + String.format("%02d", numberOfMinutes) + " " + ampmValue);
                }

                return true;
            }
        });

        alarmListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                Log.d("first visible", String.valueOf(alarmListView.getFirstVisiblePosition()));
//                Log.d("last visible", String.valueOf(alarmListView.getLastVisiblePosition()));
//                Log.d("size", String.valueOf(alarmArrayList.size()-1));
                if (alarmListView.getFirstVisiblePosition() == 0 && alarmListView.getLastVisiblePosition() == alarmArrayList.size() - 1) {
                    upImage.setVisibility(View.INVISIBLE);
                    downImage.setVisibility(View.INVISIBLE);
                }
                if (alarmListView.getFirstVisiblePosition() == 0 && alarmListView.getLastVisiblePosition() != alarmArrayList.size() - 1) {
                    upImage.setVisibility(View.INVISIBLE);
                    downImage.setVisibility(View.VISIBLE);
                }
                if (alarmListView.getFirstVisiblePosition() != 0 && alarmListView.getLastVisiblePosition() != alarmArrayList.size() - 1) {
                    upImage.setVisibility(View.VISIBLE);
                    downImage.setVisibility(View.VISIBLE);
                }
                if (alarmListView.getFirstVisiblePosition() != 0 && alarmListView.getLastVisiblePosition() == alarmArrayList.size() - 1) {
                    upImage.setVisibility(View.VISIBLE);
                    downImage.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {AlarmsTable.COLUMN_ID, AlarmsTable.COLUMN_TITLE};
        CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(),
                AlarmsProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

    private void fillData(Uri uri) {

        //getLoaderManager().initLoader(0, null, this);
//        String[] projection = { AlarmsTable.COLUMN_TITLE };
//        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null,
//                null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//            String category = cursor.getString(cursor
//                    .getColumnIndexOrThrow(AlarmsTable.COLUMN_TITLE));
//
//            Log.d("styrsa", cursor.getString(cursor
//                    .getColumnIndexOrThrow(AlarmsTable.COLUMN_TITLE)));
//
//
//            // always close the cursor
//            cursor.close();
    }

}