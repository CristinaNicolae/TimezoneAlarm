package com.cristina.timezonealarm;

/**
 * Created by Cristina on 3/7/2015.
 */

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.cristina.timezonealarm.custom.Alarm;
import com.cristina.timezonealarm.custom.PendingIntents;
import com.cristina.timezonealarm.data.AlarmsProvider;
import com.cristina.timezonealarm.data.AlarmsTable;

import java.util.ArrayList;
import java.util.Calendar;


public class AlarmsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public AlarmsFragment() {
    }

    ImageView needle;
    ImageView back;
    PointF touchPoint;
    TextView time;


    String timezone;
    Alarm alarm;
    Button addNewAlarm;
    Button setAlarm;
    boolean isTouchable = false;
    ListView alarmListView;
    ArrayList<Alarm> alarmArrayList = new ArrayList<Alarm>();
    Uri alarmUri;
    private SimpleCursorAdapter adapter;
    ArrayList<PendingIntents> piArray = new ArrayList<PendingIntents>();


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

        String[] from = new String[]{AlarmsTable.COLUMN_ID, AlarmsTable.COLUMN_TITLE, AlarmsTable.COLUMN_HOUR, AlarmsTable.COLUMN_MINUTE, AlarmsTable.COLUMN_TIMEOFDAY};
        // Fields on the UI to which we map
        int[] to = new int[]{R.id.alarmID, R.id.titleTextView, R.id.hour, R.id.minute, R.id.timeOfDay};

        getLoaderManager().initLoader(0, null, this);
        Intent intent = getActivity().getIntent();
        timezone = intent.getStringExtra("timezone");
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.alarm_list_view_item, null, from,
                to, 0);


        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {


                if (aColumnIndex == 2) {
                    int hours = aCursor.getInt(aColumnIndex);
                    TextView textView = (TextView) aView;
                    textView.setText(String.format("%02d", hours));
                    return true;
                }
                if (aColumnIndex == 3) {
                    int minutes = aCursor.getInt(aColumnIndex);
                    TextView textView = (TextView) aView;
                    textView.setText(String.format("%02d", minutes));
                    return true;
                }

                return false;
            }
        });
        alarmListView.setAdapter(adapter);

        alarmListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {


                removeDialog(view);

                return false;
            }
        });

        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create custom dialog object
                final Dialog dialog = new Dialog(getActivity());
                // Include dialog.xml file
                dialog.setContentView(R.layout.custom_dialog);
                // Set dialog title
                dialog.setTitle("NEW ALARM");

                // set values for custom dialog components - text, image and button
                final EditText editText = (EditText) dialog.findViewById(R.id.titleET);
                Button yesButton = (Button) dialog.findViewById(R.id.buttonYES);
                Button noButton = (Button) dialog.findViewById(R.id.buttonNO);

                dialog.show();

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = getActivity().getIntent();

                        alarm.title = String.valueOf(editText.getText());

                        ContentValues values = new ContentValues();
                        values.put(AlarmsTable.COLUMN_HOUR, alarm.numberOfHours);
                        values.put(AlarmsTable.COLUMN_MINUTE, alarm.numberOfMinutes);
                        values.put(AlarmsTable.COLUMN_ANGLE, alarm.angle);

                        String ampm;
                        if (alarm.timeOfDay == 1)
                            ampm = "PM";
                        else
                            ampm = "AM";

                        values.put(AlarmsTable.COLUMN_TIMEOFDAY, ampm);
                        values.put(AlarmsTable.COLUMN_TITLE, alarm.title);
                        values.put(AlarmsTable.COLUMN_ACTIVE, alarm.active);
                        values.put(AlarmsTable.COLUMN_TIMEZONEID, intent.getStringExtra("timezone"));

                        alarmUri = getActivity().getContentResolver().insert(AlarmsProvider.CONTENT_URI, values);
                        int id = Integer.valueOf(alarmUri.toString().split("/")[1]);

                        Intent intent2 = new Intent(getActivity(), MyBroadcastReceiver.class);
                        intent2.putExtra("timezone", intent.getStringExtra("timezone"));
                        intent2.putExtra("title", alarm.title);
                        intent2.putExtra("alarm_time", String.format("%02d", alarm.numberOfHours) + ":" + String.format("%02d", alarm.numberOfMinutes));
                        intent2.putExtra("id", id);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id, intent2, 0);

                        piArray.add(new PendingIntents(id, pendingIntent));


                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        int gmt = Integer.valueOf(intent.getStringExtra("gmt"));
                        if (alarm.timeOfDay == 1)
                            calendar.set(Calendar.HOUR_OF_DAY, alarm.numberOfHours + 12 + gmt);
                        else
                            calendar.set(Calendar.HOUR_OF_DAY, alarm.numberOfHours + gmt );


                        calendar.set(Calendar.MINUTE, alarm.numberOfMinutes - 1);
                        calendar.set(Calendar.SECOND, 0);

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY, pendingIntent);

                        dialog.dismiss();


                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
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
//                if (alarmListView.getFirstVisiblePosition() == 0 && alarmListView.getLastVisiblePosition() == alarmArrayList.size() - 1) {
//                    upImage.setVisibility(View.INVISIBLE);
//                    downImage.setVisibility(View.INVISIBLE);
//                }
//                if (alarmListView.getFirstVisiblePosition() == 0 && alarmListView.getLastVisiblePosition() != alarmArrayList.size() - 1) {
//                    upImage.setVisibility(View.INVISIBLE);
//                    downImage.setVisibility(View.VISIBLE);
//                }
//                if (alarmListView.getFirstVisiblePosition() != 0 && alarmListView.getLastVisiblePosition() != alarmArrayList.size() - 1) {
//                    upImage.setVisibility(View.VISIBLE);
//                    downImage.setVisibility(View.VISIBLE);
//                }
//                if (alarmListView.getFirstVisiblePosition() != 0 && alarmListView.getLastVisiblePosition() == alarmArrayList.size() - 1) {
//                    upImage.setVisibility(View.VISIBLE);
//                    downImage.setVisibility(View.INVISIBLE);
//                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {AlarmsTable.COLUMN_ID, AlarmsTable.COLUMN_TITLE, AlarmsTable.COLUMN_HOUR, AlarmsTable.COLUMN_MINUTE, AlarmsTable.COLUMN_TIMEOFDAY};
        Intent intent = getActivity().getIntent();
        CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(),
                AlarmsProvider.CONTENT_URI, projection, AlarmsTable.COLUMN_TIMEZONEID + "= ?", new String[]{intent.getStringExtra("timezone")}, null);

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


    public void removeDialog(final View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Delete alarm?");
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        TextView textView = (TextView) view.findViewById(R.id.alarmID);

                        Uri uri = Uri.parse(AlarmsProvider.CONTENT_URI + "/"
                                + textView.getText());
                        getActivity().getContentResolver().delete(uri, null, null);

                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        for (int i = 0; i < piArray.size(); i++) {
                            if (piArray.get(i).id == Integer.valueOf(textView.getText().toString())) {
                                alarmManager.cancel(piArray.get(i).pendingIntent);
                            }

                        }


                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}