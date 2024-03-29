package com.example.my_alarm;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MyDialog extends Dialog implements View.OnClickListener{


    protected static int default_width = WindowManager.LayoutParams.WRAP_CONTENT; // 默认宽度
    protected static int default_height = WindowManager.LayoutParams.WRAP_CONTENT;// 默认高度
    public static int TYPE_TWO_BT = 2;
    public static int TYPE_NO_BT = 0;
    public TextView dialog_title;
    public EditText dialog_time;
    public Button bt_cancel, bt_confirm;
    private LinearLayout ll_button;
    protected Context mContext;
    private View.OnClickListener listener;
    private View customView;
    public boolean[] cycle;
    private TextView tv_repeat_value;
    private RelativeLayout repeat_rl;
    public Calendar selectedTime;
    //	@Bind(R.id.icon)
    ImageView icon;


    public MyDialog(Context context) {
        super(context, R.style.FullScreenDialog);
        mContext = context;
        customView = LayoutInflater.from(context).inflate(R.layout.my_dialogue, null);

        icon = (ImageView) customView.findViewById(R.id.icon);

        ll_button = (LinearLayout) customView.findViewById(R.id.ll_button);
        dialog_title = (TextView) customView.findViewById(R.id.dialog_title);
        setTitle("提示信息");
        dialog_time = (EditText) customView.findViewById(R.id.dialog_time);
        dialog_time.clearFocus();
        bt_confirm = (Button) customView.findViewById(R.id.dialog_confirm);
        bt_cancel =(Button) customView.findViewById(R.id.dialog_cancel);
    }

    public void setBtnClick(View.OnClickListener clickListener){
        bt_confirm.setOnClickListener(clickListener);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repeat_rl:
                selectRemindCycle();
                break;
            case R.id.dialog_time:
                setTime();
                break;
            case R.id.dialog_cancel:
                dismiss();
            default:
                break;
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(customView);
        repeat_rl = (RelativeLayout) findViewById(R.id.repeat_rl);
        repeat_rl.setOnClickListener(this);
        tv_repeat_value = (TextView) findViewById(R.id.tv_repeat_value);
        dialog_time.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);

        tv_repeat_value.setText("只响一次");
        cycle = null;
        bt_confirm.setTextColor(mContext.getResources().getColor(R.color.line_and_outline_grey));
        bt_confirm.setEnabled(false);
        //ButterKnife  view绑定
        //ButterKnife.bind(this,customView);
    }

    private void setTime(){
        Calendar c =Calendar.getInstance();
        new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar=Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);

                Calendar currentTime=Calendar.getInstance();
                if (calendar.getTimeInMillis()<=currentTime.getTimeInMillis()){
                    calendar.setTimeInMillis(calendar.getTimeInMillis()+24*60*60*1000);
                }
                selectedTime=calendar;
                dialog_time.setText(""+selectedTime.get(Calendar.HOUR_OF_DAY)+":"+selectedTime.get(Calendar.MINUTE));
            }
        },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
        bt_confirm.setEnabled(true);
        bt_confirm.setTextColor(mContext.getResources().getColor(R.color.light_black));

    }

    public MyDialog setTitle(String title) {
        dialog_title.setText(title);
        return this;
    }

    public static String[] weekName={"周日","周一","周二","周三","周四","周五","周六"};

    /**
     * @param repeat 解析二进制闹钟周期
     * @param flag   flag=0返回带有汉字的周一，周二cycle等，flag=1,返回weeks(1,2,3)
     * @return
     */
    public static String parseRepeat(boolean[] repeat, int flag) {
        String cycle = "";
        String weeks = "";
        for(int i =0; i<7;i++){
            if(repeat[(i+1)%7]==false) continue;
            if("".equals(cycle)){
                cycle+=weekName[(i+1)%7];
                weeks +=(i+1);
            } else {
                cycle+="," + weekName[(i+1)%7];
                weeks +="," + (i+1);
            }

        }
        return flag == 0 ? cycle : weeks;
    }

    public void selectRemindCycle() {

        final SelectRemindCyclePopup fp = new SelectRemindCyclePopup(mContext);
        fp.showPopup(customView);
        fp.setOnSelectRemindCyclePopupListener(new SelectRemindCyclePopup
                .SelectRemindCyclePopupOnClickListener() {

            @Override
            public void obtainMessage(int flag, boolean[] ret) {
                switch (flag) {
                    // 星期一
                    case 0:

                        break;
                    // 星期二
                    case 1:

                        break;
                    // 星期三
                    case 2:

                        break;
                    // 星期四
                    case 3:

                        break;
                    // 星期五
                    case 4:

                        break;
                    // 星期六
                    case 5:

                        break;
                    // 星期日
                    case 6:

                        break;
                    // 确定
                    case 7:
                        cycle = ret;
                        tv_repeat_value.setText(parseRepeat(cycle, 0));
                        fp.dismiss();
                        break;
                    case 8:
                        tv_repeat_value.setText("每天");
                        boolean[] x = {true,true,true,true,true,true,true};
                        cycle = x;
                        fp.dismiss();
                        break;
                    case 9:
                        tv_repeat_value.setText("只响一次");
                        cycle = null;
                        fp.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    public MyDialog setIcon(int iconResId) {
        dialog_title.setVisibility(View.GONE);
        icon.setVisibility(View.VISIBLE);
        icon.setBackgroundResource(iconResId);

        return this;
    }


}
