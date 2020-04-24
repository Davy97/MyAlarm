package com.example.my_alarm;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyDialog extends Dialog implements View.OnClickListener{
    protected static int default_width = WindowManager.LayoutParams.WRAP_CONTENT; // 默认宽度
    protected static int default_height = WindowManager.LayoutParams.WRAP_CONTENT;// 默认高度
    public static int TYPE_TWO_BT = 2;
    public static int TYPE_NO_BT = 0;
    public TextView dialog_title;
    public EditText dialog_message;
    public Button bt_cancel, bt_confirm;
    private LinearLayout ll_button;
    protected Context mContext;
    private View.OnClickListener listener;
    private View customView;
    private int cycle;
    private TextView tv_repeat_value;
    private RelativeLayout repeat_rl;
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
        dialog_message = (EditText) customView.findViewById(R.id.dialog_time);
        dialog_message.clearFocus();
        bt_confirm = (Button) customView.findViewById(R.id.dialog_confirm);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repeat_rl:
                selectRemindCycle();
                break;
//            case R.id.set_btn:
//                setClock();
//                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        repeat_rl = (RelativeLayout) findViewById(R.id.repeat_rl);
        repeat_rl.setOnClickListener(this);
        tv_repeat_value = (TextView) findViewById(R.id.tv_repeat_value);
        super.onCreate(savedInstanceState);
        setContentView(customView);
        //ButterKnife  view绑定
        //ButterKnife.bind(this,customView);
    }

    public MyDialog setClickListener(View.OnClickListener listener) {
        this.listener = listener;
        bt_confirm.setOnClickListener(listener);
        return this;
    }

    public MyDialog setMessage(String message) {
        dialog_message.setText(message);
        return this;
    }

    public MyDialog setTitle(String title) {
        dialog_title.setText(title);
        return this;
    }

    /**
     * @param repeat 解析二进制闹钟周期
     * @param flag   flag=0返回带有汉字的周一，周二cycle等，flag=1,返回weeks(1,2,3)
     * @return
     */
    public static String parseRepeat(int repeat, int flag) {
        String cycle = "";
        String weeks = "";
        if (repeat == 0) {
            repeat = 127;
        }
        if (repeat % 2 == 1) {
            cycle = "周一";
            weeks = "1";
        }
        if (repeat % 4 >= 2) {
            if ("".equals(cycle)) {
                cycle = "周二";
                weeks = "2";
            } else {
                cycle = cycle + "," + "周二";
                weeks = weeks + "," + "2";
            }
        }
        if (repeat % 8 >= 4) {
            if ("".equals(cycle)) {
                cycle = "周三";
                weeks = "3";
            } else {
                cycle = cycle + "," + "周三";
                weeks = weeks + "," + "3";
            }
        }
        if (repeat % 16 >= 8) {
            if ("".equals(cycle)) {
                cycle = "周四";
                weeks = "4";
            } else {
                cycle = cycle + "," + "周四";
                weeks = weeks + "," + "4";
            }
        }
        if (repeat % 32 >= 16) {
            if ("".equals(cycle)) {
                cycle = "周五";
                weeks = "5";
            } else {
                cycle = cycle + "," + "周五";
                weeks = weeks + "," + "5";
            }
        }
        if (repeat % 64 >= 32) {
            if ("".equals(cycle)) {
                cycle = "周六";
                weeks = "6";
            } else {
                cycle = cycle + "," + "周六";
                weeks = weeks + "," + "6";
            }
        }
        if (repeat / 64 == 1) {
            if ("".equals(cycle)) {
                cycle = "周日";
                weeks = "7";
            } else {
                cycle = cycle + "," + "周日";
                weeks = weeks + "," + "7";
            }
        }

        return flag == 0 ? cycle : weeks;
    }

    public void selectRemindCycle() {

        final SelectRemindCyclePopup fp = new SelectRemindCyclePopup(mContext);
        fp.showPopup((LinearLayout) findViewById(R.id.activity_main));
        fp.setOnSelectRemindCyclePopupListener(new SelectRemindCyclePopup
                .SelectRemindCyclePopupOnClickListener() {

            @Override
            public void obtainMessage(int flag, String ret) {
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
                        int repeat = Integer.valueOf(ret);
                        tv_repeat_value.setText(parseRepeat(repeat, 0));
                        cycle = repeat;
                        fp.dismiss();
                        break;
                    case 8:
                        tv_repeat_value.setText("每天");
                        cycle = 0;
                        fp.dismiss();
                        break;
                    case 9:
                        tv_repeat_value.setText("只响一次");
                        cycle = -1;
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