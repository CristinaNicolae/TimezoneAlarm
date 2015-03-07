package com.cristina.timezonealarm.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews;

import com.cristina.timezonealarm.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Cristina on 3/7/2015.
 */

@RemoteViews.RemoteView
public class AnalogClock extends View {

    private Time mCalendar;

    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mDial;

    private int mDialWidth;
    private int mDialHeight;

    private boolean mAttached;

    private final Handler mHandler = new Handler();
    private float mMinutes;
    private float mHour;
    private boolean mChanged;

    public TimeZone mtimeZone;

    public void setAnalogClockListener(AnalogClockListener analogClockListener) {
        this.analogClockListener = analogClockListener;
    }

    AnalogClockListener analogClockListener;

    public AnalogClock(Context context) {
        this(context, null);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
        Resources r = super.getContext().getResources();
        mDial = r.getDrawable(R.drawable.bottom_clock_background);
        mHourHand = r.getDrawable(R.drawable.bottom_clock_hour_line);
        mMinuteHand = r.getDrawable(R.drawable.bottom_clock_minute_line);
        mCalendar = new Time();
        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();

            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
        }


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float) heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    public String setTimeZone(TimeZone timeZone) {
        mtimeZone = timeZone;
        return onTimeChanged();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

        int availableWidth = super.getRight() - super.getLeft();
        int availableHeight = super.getBottom() - super.getTop();

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        final Drawable dial = mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();

        boolean scaled = false;

        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w,
                    (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);

        canvas.save();
        canvas.rotate(mHour / 12.0f * 360.0f, x, y);
        final Drawable hourHand = mHourHand;
        if (changed) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        hourHand.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

        final Drawable minuteHand = mMinuteHand;
        if (changed) {
            w = minuteHand.getIntrinsicWidth();
            h = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        minuteHand.draw(canvas);
        canvas.restore();

        if (scaled) {
            canvas.restore();
        }
    }

    private String onTimeChanged() {

        Date date = new Date();
        SimpleDateFormat sdfAmerica = new SimpleDateFormat("hh:mm");
        sdfAmerica.setTimeZone(mtimeZone);
        String sDate = sdfAmerica.format(date);

        String[] dateSplit = sDate.split(":");

        Date currentDate = new Date();
        currentDate.setHours(Integer.valueOf(dateSplit[0]));
        currentDate.setMinutes(Integer.valueOf(dateSplit[1]));

        mCalendar.set(currentDate.getTime());

        int hour = mCalendar.hour;
        int minute = mCalendar.minute;
        int second = mCalendar.second;

        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;
        mChanged = true;

        updateContentDescription(mCalendar);

        return sDate;
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {

                if (analogClockListener != null) {
                    analogClockListener.onTimeTicked();
                    invalidate();
                }
            }
        }
    };

    private void updateContentDescription(Time time) {
        final int flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
        String contentDescription = DateUtils.formatDateTime(super.getContext(),
                time.toMillis(false), flags);
        setContentDescription(contentDescription);
    }


    public interface AnalogClockListener {
        public void onTimeTicked();

    }


}
