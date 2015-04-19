package com.example.nolitsou.hapi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nolitsou.hapi.data.Alarm;
import com.github.nkzawa.socketio.client.Ack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlarmContainer extends FrameLayout {

    private static final String LOG_STR = "AlarmFragment";
    private View rootView;
    private AlarmsAdapter listAdapter;
    private ListView alarmListView;

    public AlarmContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        rootView = getRootView();
        Alarm.setAlarmContainer(this);
        TextView addAlarm = (TextView) rootView.findViewById(R.id.addAlarmButton);
        /*
        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View addAlarmView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_edit, null);
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
                        TimePicker tp = ((TimePicker) addAlarmView.findViewById(R.id.addAlarm_time));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
                        calendar.set(Calendar.MINUTE, tp.getCurrentMinute());
                        Alarm.addAlarm(calendar.getTime(), true, false);
                        rootView.removeView(addAlarmView);
                    }
                });
            }
        });
        */
        this.alarmListView = (ListView) rootView.findViewById(R.id.alarmList);
        this.setListAdapter(new AlarmsAdapter(getContext(), Alarm.getAlarms()));
        alarmListView.setAdapter(listAdapter);
        getListAdapter().notifyDataSetChanged();

        Alarm.loadingAlarmsLL = ((RelativeLayout) rootView.findViewById(R.id.loading_alarms));
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
            Alarm item = ((AlarmsAdapter) l.getAdapter()).getItem(i);
            if (alarm.get_id().equals(item.get_id())) {
                return l.getChildAt(i);
            }
        }
        return null;
    }

    public void loadData() {
        if (getContext() != null && ((AbstractActivity) getContext()).socketConnected()) {
            Log.i(LOG_STR, "Loading alarms");
            GetAlarmsTask task = new GetAlarmsTask(getListAdapter());
            task.execute();
        } else {
            Log.w(LOG_STR, "not connected");
        }
    }

    class GetAlarmsTask extends AsyncTask<String, String, Void> {
        //private ProgressDialog mDialog;
        private Exception error;
        private ProgressDialog mDialog;
        private AlarmsAdapter adapter;
        private boolean done;

        public GetAlarmsTask(AlarmsAdapter adapter) {
            this.adapter = adapter;
        }

        protected void onPreExecute() {
        }

        protected Void doInBackground(String... data) {
            done = false;
            Log.i(LOG_STR, "getting alarms");
            ((AbstractActivity) getContext()).getSocketService().getSocket().emit("alarm:get", new JSONObject(), new Ack() {
                @Override
                public void call(Object... arg0) {
                    Alarm.getAlarms().clear();
                    Alarm alarm;
                    try {
                        if (arg0.length > 0) {
                            JSONArray data = ((JSONObject) arg0[0]).getJSONArray("list");
                            for (int i = 0; i < data.length(); i++) {
                                alarm = new Alarm(data.getJSONObject(i));
                                Alarm.getAlarms().add(alarm);
                            }
                        }
                        Log.i(LOG_STR, "got " + Alarm.getAlarms().size() + " alarms");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    done = true;
                }
            });
            while (!done) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            Log.i(LOG_STR, "opst execute");
            adapter.notifyDataSetChanged();
            Alarm.loadingAlarmsLL.setVisibility(View.GONE);

        }
    }

}