package com.example.hapi;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hapi.data.Alarm;

public class AlarmFragment extends CustomFragment {

    private ViewGroup rootView;
	private AlarmsAdapter listAdapter;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        this.rootView = (ViewGroup) inflater.inflate(
                R.layout.alarms, container, false);
        System.out.println("geting alarms:!::!");
        updateAlarmList();
        Alarm.setAlarmFragment(this);
        title = "Alarms";
        TextView addAlarm = (TextView)rootView.findViewById(R.id.addAlarmButton);
        addAlarm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final View addAlarmView = LayoutInflater.from(getActivity()).inflate(R.layout.alarm_edit, null);
				rootView.addView(addAlarmView);
				addAlarmView.findViewById(R.id.addAlarm_cancel).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						rootView.removeView(addAlarmView);
					}
				});
				addAlarmView.findViewById(R.id.addAlarm_confirm).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						TimePicker tp =((TimePicker)addAlarmView.findViewById(R.id.addAlarm_time));
						Calendar calendar = Calendar.getInstance();
						calendar.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
						calendar.set(Calendar.MINUTE, tp.getCurrentMinute());
						Alarm.addAlarm(calendar.getTime(), true, false);
						rootView.removeView(addAlarmView);
					}
				});
			}
		});
        return rootView;
    }
    public void updateAlarmList() {
	    ListView l = (ListView) rootView.findViewById(R.id.alarmList);
		this.setListAdapter(new AlarmsAdapter(getActivity(), Alarm.getAlarms()));
	    l.setAdapter(listAdapter);
	    Alarm.loadingAlarmsLL = ((RelativeLayout)rootView.findViewById(R.id.loading_alarms));
	    Alarm.loadingAlarmsLL.setVisibility(View.VISIBLE);
		Alarm.loadAlarms((ArrayAdapter<Alarm>)listAdapter);
    }
	public AlarmsAdapter getListAdapter() {
		return listAdapter;
	}
	public void setListAdapter(AlarmsAdapter listAdapter) {
		this.listAdapter = listAdapter;
	}
	public View getAlarmInList(Alarm alarm) {
		int i = 0;
	    ListView l = (ListView) rootView.findViewById(R.id.alarmList);
	    for (i = 0; i < l.getAdapter().getCount(); i++) {
	    	Alarm item = ((AlarmsAdapter)l.getAdapter()).getItem(i);
	    	if (alarm.get_id().equals(item.get_id())) {
	    		return l.getChildAt(i);
	    	}
	    }
		return null;
	}
	public void loadData() {
	    Alarm.loadingAlarmsLL = ((RelativeLayout)rootView.findViewById(R.id.loading_alarms));
	    Alarm.loadingAlarmsLL.setVisibility(View.VISIBLE);
		Alarm.loadAlarms(getListAdapter());
	}
	
}