package com.example.nolitsou.hapi.data;

import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.example.nolitsou.hapi.AbstractActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Alarm extends SocketData implements Comparable {
    public static ArrayAdapter<Alarm> notify;
    public static RelativeLayout loadingAlarmsLL;
    public static boolean dataLoading = false;
    private static ArrayList<Alarm> alarms = new ArrayList<Alarm>();
    ;
    private String _id;
    private Date time;
    private boolean enable;
    private boolean repeat;

    public Alarm(JSONObject json) throws JSONException {
        this.set_id(json.getString("_id"));
        this.setTime(strToDate(json.getString("time")));
        this.setEnable(json.getBoolean("enable"));
        this.repeat = json.getBoolean("repeat");
    }

    public Alarm(String _id, Date date, boolean enable, boolean repeat) {
        this.set_id(_id);
        this.setTime(date);
        this.setEnable(enable);
        this.repeat = repeat;
    }

    public static void loadAlarms() {
    }

    public static void addAlarm(Date date, boolean enable, boolean repeat) {
    }

    protected static void updateList() {
    }

    private static Date strToDate(String strDate) {
        Date date = null;
        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            df.setTimeZone(tz);
            date = df.parse(strDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    private static String dateToStr(Date date) {
        String strDate = null;
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        strDate = df.format(date);
        return strDate;
    }

    public static ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    public static void setAlarms(ArrayList<Alarm> alarms) {
        Alarm.alarms = alarms;
    }


    public static void setActivity(AbstractActivity activity) {
        Alarm.activity = activity;
    }

    public void updateEnable() {
    }

    @Override
    public String toString() {
        return "Alarm(" + get_id() + ") at " + getTime() + "enabled? " + enable;

    }

    private JSONObject toJSON() {
        JSONObject json = new JSONObject();
        JSONObject alarm = new JSONObject();
        try {
            alarm.put("_id", this.get_id());
            alarm.put("time", dateToStr(getTime()));
            alarm.put("enable", this.enable);
            alarm.put("repeat", repeat);
            json.put("alarm", alarm);
        } catch (JSONException e) {
        }
        return json;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void remove() {
    }

    @Override
    public int compareTo(Object arg) {
        if (arg instanceof Alarm) {
            Calendar current = Calendar.getInstance();
            current.setTime(getTime());
            Calendar argTime = Calendar.getInstance();
            argTime.setTime(((Alarm) arg).getTime());

            if (current.get(Calendar.HOUR_OF_DAY) > argTime.get(Calendar.HOUR_OF_DAY)) {
                return 1;
            } else if (current.get(Calendar.HOUR_OF_DAY) < argTime.get(Calendar.HOUR_OF_DAY)) {
                return -1;
            } else {
                if (current.get(Calendar.MINUTE) > argTime.get(Calendar.MINUTE)) {
                    return 1;
                } else if (current.get(Calendar.MINUTE) > argTime.get(Calendar.MINUTE)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } else {
            return 0;
        }
    }
}
