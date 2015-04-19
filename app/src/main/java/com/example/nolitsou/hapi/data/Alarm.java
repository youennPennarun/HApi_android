package com.example.nolitsou.hapi.data;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.example.nolitsou.hapi.AbstractActivity;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
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

    public static void loadAlarms(ArrayAdapter adapter) {
        Alarm.notify = adapter;
        loadAlarms();
    }

    public static void loadAlarms() {
        if (activity.getSocketService().getSocket() != null) {
            activity.getSocketService().getSocket().emit("alarm:get", new JSONObject());
        }
    }

    public static void addAlarm(Date date, boolean enable, boolean repeat) {
        JSONObject data = new JSONObject();
        JSONObject alarm = new JSONObject();
        try {
            alarm.put("time", dateToStr(date));
            alarm.put("enable", enable);
            alarm.put("repeat", dateToStr(date));
            data.put("alarm", alarm);
            activity.getSocketService().getSocket().emit("alarm:add", data);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void setSocket(Socket socket) {
        socket.on("response:alarm:get", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Alarm.alarms.clear();
                Alarm alarm;
                try {
                    if (args.length > 0) {
                        JSONArray data = ((JSONObject) args[0]).getJSONArray("list");
                        for (int i = 0; i < data.length(); i++) {
                            alarm = new Alarm(data.getJSONObject(i));
                            getAlarms().add(alarm);
                            if (notify != null) {
                                Alarm.updateList();
                            }
                        }
                        if (notify != null) {
                            Alarm.updateList();
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).on("alarm:new", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Alarm alarm;
                try {
                    if (args.length > 0) {
                        alarm = new Alarm(((JSONObject) args[0]).getJSONObject("alarm"));
                        alarms.add(alarm);
                        if (notify != null) {
                            Alarm.updateList();
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).on("alarm:remove", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject alarm;
                JSONObject data;
                String idToRemove;
                try {
                    if (args.length > 0) {
                        data = (JSONObject) args[0];
                        if (data.has("alarm")) {
                            alarm = data.getJSONObject("alarm");
                            if (alarm.has("_id")) {
                                idToRemove = alarm.getString("_id");
                                for (int i = 0; i < alarms.size(); i++) {
                                    if (alarms.get(i).get_id().equals(idToRemove)) {
                                        alarms.remove(i);
                                        break;
                                    }
                                }
                                if (notify != null) {
                                    Alarm.updateList();
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).on("alarm:update", new Emitter.Listener() {
            @Override
            public void call(Object... arg0) {
                if (arg0.length > 0) {
                    JSONObject data = (JSONObject) arg0[0];
                    if (data.has("alarm")) {
                        JSONObject updated;
                        try {
                            updated = data.getJSONObject("alarm");
                            if (updated.has("_id")) {
                                for (Alarm alarm : alarms) {
                                    if (alarm.get_id().equals(updated.getString("_id"))) {
                                        if (updated.has("repeat")) {
                                            alarm.repeat = updated.getBoolean("repeat");
                                        }
                                        if (updated.has("enable")) {
                                            alarm.enable = updated.getBoolean("enable");
                                            final Alarm tmp = alarm;
                                           // TODO
                                        }
                                        if (updated.has("time")) {
                                            alarm.time = strToDate(updated.getString("time"));
                                        }
                                        //updateList();
                                        break;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    protected static void updateList() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (notify != null) {
                    Alarm.notify.notifyDataSetChanged();
                    loadingAlarmsLL.setVisibility(View.GONE);
                    dataLoading = false;
                }
            }
        });
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
        JSONObject json = new JSONObject();
        JSONObject alarm = new JSONObject();
        JSONObject update = new JSONObject();
        try {
            update.put("enable", this.enable);
            alarm.put("_id", this.get_id());
            alarm.put("update", update);
            json.put("alarm", alarm);
            activity.getSocketService().getSocket().emit("alarm:update", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        activity.getSocketService().getSocket().emit("alarm:remove", this.toJSON());
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
