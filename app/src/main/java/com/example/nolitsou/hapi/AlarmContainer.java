package com.example.nolitsou.hapi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nolitsou.hapi.data.Alarm;
import com.example.nolitsou.hapi.data.Settings;
import com.example.nolitsou.hapi.utils.GetJsonTask;
import com.example.nolitsou.hapi.utils.ServerResponse;
import com.example.nolitsou.hapi.utils.ServerResponseError;

import org.json.JSONArray;
import org.json.JSONException;

public class AlarmContainer extends FrameLayout {

    private static final String LOG_STR = "AlarmFragment";
    private View rootView;
    private AlarmsAdapter listAdapter;
    private ListView alarmListView;
    private RelativeLayout loadingAlarm;

    public AlarmContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        rootView = getRootView();
        Alarm.setAlarmContainer(this);
        TextView addAlarm = (TextView) rootView.findViewById(R.id.addAlarmButton);

        this.alarmListView = (ListView) rootView.findViewById(R.id.alarmList);
        this.listAdapter = new AlarmsAdapter(getContext(), Alarm.getAlarms());
        alarmListView.setAdapter(listAdapter);
        this.listAdapter.notifyDataSetChanged();

        this.loadingAlarm = ((RelativeLayout) rootView.findViewById(R.id.loading_alarms));
        loadData();
    }

    public void loadData() {
        GetAlarmsTask getAlarms = new GetAlarmsTask();
        Settings.loadSettings(getContext());
        System.out.println("*** " + Settings.host + "api/alarms");
        getAlarms.execute(Settings.host + "api/alarms");
    }


    class GetAlarmsTask extends GetJsonTask {
        @Override
        protected void onPostExecute(ServerResponse result) {
            int i = 0;
            if (result != null) {
                if (result.success) {
                    if (result.data.has("items")) {
                        Alarm.getAlarms().clear();
                        try {
                            JSONArray list = result.data.getJSONArray("items");
                            for (i = 0; i < list.length(); i++) {
                                System.out.println("--------------------------->NEW ALARM");
                                Alarm.getAlarms().add(new Alarm(list.getJSONObject(i)));
                            }
                            listAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (((ServerResponseError) result).getCode() == 401) {
                    ((AbstractActivity) getContext()).notLoggedIn();
                }
            }
            loadingAlarm.setVisibility(View.GONE);
        }
    }

}