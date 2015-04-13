package com.example.nolitsou.hapi;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nolitsou.hapi.data.Alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AlarmsAdapter extends ArrayAdapter<Alarm> {
    private final Context context;
    private final ArrayList<Alarm> values;

    public AlarmsAdapter(Context context, ArrayList<Alarm> alarms) {
        super(context, R.layout.alarm_list, alarms);
        this.context = context;
        this.values = alarms;
        Collections.sort(this.values);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.alarm_list, parent, false);
        if (values.size() > position) {
            LinearLayout alarmHeaderLayout = (LinearLayout) rowView.findViewById(R.id.alarmHeader);
            LinearLayout alarmItem = (LinearLayout) rowView.findViewById(R.id.alarm_item_layout);
            TextView alarmTime = (TextView) rowView.findViewById(R.id.alarmDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(values.get(position).getTime());
            String curTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
            if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
                curTime += " AM";
            } else {
                curTime += " PM";
            }
            alarmTime.setText(curTime);
            if (values.get(position).isEnable()) {
                alarmHeaderLayout.setBackgroundResource(R.color.alarm_enable);
            } else {
                alarmHeaderLayout.setBackgroundResource(R.color.alarm_disable);

            }

            LinearLayout alarmDetails = (LinearLayout) rowView.findViewById(R.id.alarmDetailsContent);
            CheckBox alarmEnabled = (CheckBox) rowView.findViewById(R.id.alarmEnabled);
            alarmEnabled.setChecked(values.get(position).isEnable());
            alarmDetails.setVisibility(View.GONE);
            //alarmDetailsContent
            alarmItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout alarmDetails = (LinearLayout) rowView.findViewById(R.id.alarmDetailsContent);
                    if (alarmDetails.getVisibility() == View.VISIBLE) {
                        collapse(rowView, alarmDetails);
                    } else {
                        expand(rowView, alarmDetails);
                    }
                }
            });
            rowView.findViewById(R.id.removeAlarm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    values.get(position).remove();
                }
            });
            alarmEnabled.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    values.get(position).setEnable(isChecked);
                    values.get(position).updateEnable();
                }
            });
        }
        return rowView;
    }

    private void expand(View rowView, LinearLayout layout) {
        //set Visible
        rotateArrow(rowView, 0f, 90f);

        layout.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        layout.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(layout, 0, layout.getMeasuredHeight());
        mAnimator.start();
    }

    private void collapse(View rowView, final LinearLayout layout) {

        rotateArrow(rowView, 90f, 0f);

        int finalHeight = layout.getHeight();

        ValueAnimator mAnimator = slideAnimator(layout, finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator arg0) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(final LinearLayout layout, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                layoutParams.height = value;
                layout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void rotateArrow(View rowView, float from, float to) {
        ImageView arrow = (ImageView) rowView.findViewById(R.id.alarm_item_arrow);
        RotateAnimation anim = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(100);
        anim.setFillAfter(true);
        arrow.startAnimation(anim);
    }

}
