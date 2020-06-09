package com.example.my_alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.my_alarm.AlarmManagerUtil;


import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private MainActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        btAddAlarm = (Button) findViewById(R.id.btnAddAlarm);
        lvAlarmList = (ListView) findViewById(R.id.lvAlarmList);

        adapter = new ArrayAdapter<AlarmData>(this, android.R.layout.simple_list_item_1);
        lvAlarmList.setAdapter(adapter);
        readSavedAlarmList();

        btAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarm();
            }
        });


        lvAlarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                new AlertDialog.Builder(activity).setTitle("操作").setItems(new CharSequence[]{"删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                deleteAlarm(position);
                                break;
                            default:
                                break;
                        }
                    }
                }).setNegativeButton("取消", null).show();
                return true;
            }
        });
    }

    private void deleteAlarm(int position) {
        AlarmData ad = adapter.getItem(position);
        adapter.remove(ad);
        saveAlarmList();

        alarmManager.cancel(PendingIntent.getBroadcast(this, ad.getId(), new Intent(this, AlarmReceiver.class), 0));
    }

    private void addAlarm() {

        final MyDialog myDialog = new MyDialog(this);
        myDialog.setBtnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean[] cycle = myDialog.cycle;
                Calendar calendar = myDialog.selectedTime;
                if ( calendar == null) {
                    myDialog.dismiss();
                    return;
                }
                if (cycle == null) {

                    AlarmData ad = (new AlarmData(myDialog.selectedTime.getTimeInMillis(), null));
                    adapter.add(ad);
                    Calendar currentTime = Calendar.getInstance();
                    if (calendar.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                        calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
                    }
                    AlarmManagerUtil.setAlarm(activity, 0, calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE), ad.getId(), -1, "");

//                alarmManager.set(AlarmManager.RTC_WAKEUP,
//                        ad.getTime(),
//                        PendingIntent.getBroadcast(activity,
//                                ad.getId(),
//                                new Intent(activity,AlarmReceiver.class),0));

                } else {
                    ArrayList<Integer> arr = new ArrayList<Integer>();
                    AlarmData ad = (new AlarmData(myDialog.selectedTime.getTimeInMillis(), arr));
                    adapter.add(ad);
                    for (Integer i : arr) {
                        AlarmManagerUtil.setAlarm(activity, 2, calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE), ad.getId(), -1, "");
                    }
                }
                saveAlarmList();
                myDialog.dismiss();
            }
        });
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.show();


    }

    //   1/2/3/4
    private void saveAlarmList() {

        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE).edit();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < adapter.getCount(); ++i) {
            if (adapter.getItem(i).alarmDate != null) {
                String s = "";
                for (Integer date : adapter.getItem(i).alarmDate) {
                    s += date + "/";
                }
                sb.append(s + ":" + adapter.getItem(i).getTime()).append(",");
            } else {
                sb.append(adapter.getItem(i).getTime()).append(",");
            }
        }
        if (sb.length() > 1) {
            String content = sb.toString().substring(0, sb.length() - 1);
            editor.putString(KEY_ALARM_LIST, content);
            System.out.println(content);
        } else {
            editor.putString(KEY_ALARM_LIST, null);
        }
        editor.commit();

    }

    private void readSavedAlarmList() {
        SharedPreferences sp = getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(KEY_ALARM_LIST, null);

        if (content != null) {
            String[] timeString = content.split(",");
            for (String string : timeString) {
                String[] timeString2 = string.split(":");
                if (timeString2.length > 1) {
                    string = timeString2[1];
                    timeString2 = timeString2[0].split("/");
                    ArrayList<Integer> arr = new ArrayList<Integer>();
                    for (String ss : timeString2) {
                        if(ss.equals("")) continue;
                        arr.add(Integer.parseInt(ss));
                    }
                    adapter.add(new AlarmData(Long.parseLong(string), arr));
                } else {
                    adapter.add(new AlarmData(Long.parseLong(string), null));
                }
            }
        }
    }

    private static class AlarmData {
        public AlarmData(long time, ArrayList<Integer> _alarmDate) {
            this.time = time;
            date = Calendar.getInstance();
            date.setTimeInMillis(time);
            if (_alarmDate == null) {

                timeLable = String.format("%d月%d日 %d:%d",
                        date.get(Calendar.MONTH) + 1,
                        date.get(Calendar.DAY_OF_MONTH),
                        date.get(Calendar.HOUR_OF_DAY),
                        date.get(Calendar.MINUTE));
            } else {
                alarmDate = _alarmDate;
                timeLable = "";
                for (Integer d : alarmDate) {
                    timeLable = timeLable + MyDialog.weekName[d] + " ";
                }
                timeLable = timeLable + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE);
            }


        }

        public long getTime() {
            return time;
        }

        public String getTimeLable() {
            return timeLable;
        }

        @Override
        public String toString() {
            return getTimeLable();
        }

        public int getId() {
            return (int) (getTime() % (1e9 + 7) * 1000813 % (1e9 + 9));

        }

        private String timeLable = "";
        private long time = 0;
        private Calendar date = null;
        private ArrayList<Integer> alarmDate = null;
    }


    private static final String KEY_ALARM_LIST = "alarmlist";
    private Button btAddAlarm;
    private ListView lvAlarmList;
    private ArrayAdapter<AlarmData> adapter;
    private AlarmManager alarmManager;


}
