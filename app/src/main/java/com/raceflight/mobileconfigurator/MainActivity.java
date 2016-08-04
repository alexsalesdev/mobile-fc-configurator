package com.raceflight.mobileconfigurator;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.raceflight.mobileconfigurator.communication.BT_New;
import com.raceflight.mobileconfigurator.communication.SerialFTDI;

public class MainActivity extends AppCompatActivity {

    private App app;
    private Button submit;
    private TextView historyBox;
    private EditText command;
    private Button clear;
    private ScrollView historyView;
    private StringBuilder historyBuilder = new StringBuilder();

    private RecyclerView configMenu;
    private LinearLayoutManager mLayoutManager;
    private Button pidsTuningButton;
    private Button ratesTuningButton;
    private Button configTuningButton;


    final View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;
            app.commMW.Write(("get " + tv.getText().toString() + "\n").getBytes());
            command.setText("set " +  tv.getText().toString() + " = ");
            int pos = command.getText().length();
            command.setSelection(pos);
        }
    };

    //raceflight
    private String[] pidsTuning;
    private String[] ratesTuning;
    private String[] configTuning;

    //betaflight

    private void raceflightMode() {
        pidsTuning = new String[] {"fpkp", "fpki", "fpkd", "frkp", "frki", "frkd", "fykp", "fyki", "fykd", "wpgyrolpf", "wrgyrolpf", "wygyrolpf", "wpkdlpf", "wrkdlpf", "wykdlpf", "witchcraft"};
        ratesTuning = new String[] {"fpexpo", "frexpo", "fyexpo", "ftmid", "ftexpo", "frrate", "fprate", "fyrate", "ctrarate", "wtpabreak", "fpacrop", "fracrop", "fyacrop"};
        configTuning = new String[] {"acc_hardware", "baro_hardware", "mag_hardware", "rf_loop_ctrl", "yaw_jump_prevention", "motor_pwm_rate", "min_command", "min_throttle", "max_throttle", "rc_smoothing", "roll_yaw_cam_mix_degrees", "max_check", "min_check", "mid_rc"};
    }

    private void betaflightMode() {
        pidsTuning = new String[] { "p_pitch", "i_pitch", "d_pitch", "p_roll", "i_roll", "d_roll", "p_yaw", "i_yaw", "d_yaw","pid_controller", "gyro_lowpass", "gyro_lpf", "yaw_p_limit", "dterm_lowpass", "yaw_lowpass"};
        ratesTuning = new String[] {"rc_rate", "rc_rate_yaw", "rc_expo", "rc_yaw_expo", "thr_mid", "thr_expo", "roll_rate", "pitch_rate", "yaw_rate", "tpa_rate", "tpa_breakpoint"};
        configTuning = new String[] {"gyro_sync_denom", "pid_process_denom", "acc_hardware",  "baro_hardware", "mag_hardware", "use_unsynced_pwm", "motor_pwm_protocol", "yaw_jump_prevention",
                "motor_pwm_rate", "min_command", "min_throttle", "max_throttle", "rc_smooth_interval_ms", "roll_yaw_cam_mix_degrees", "max_check", "min_check", "mid_rc"};

    }

    private final Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BT_New.MESSAGE_STATE_CHANGE:
                    Log.i("ccc", "MESSAGE_STATE_CHANGE: " + msg.arg1);

                    switch (msg.arg1) {
                        case BT_New.STATE_CONNECTED:
                            app.commMW.Write("#".getBytes());
                            // setStatus("Connected");
                            break;
                        case BT_New.STATE_CONNECTING:
                            setStatus(getString(R.string.Connecting));
                            break;
                        case BT_New.STATE_NONE:
                            break;
                    }

                    break;
                case BT_New.MESSAGE_WRITE:
                    //Toast.makeText(getApplicationContext(), "WRITE", Toast.LENGTH_SHORT).show();
                    break;
                case BT_New.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    historyBuilder.append(readMessage);
                    historyBox.setText(historyBuilder.toString());
                    historyView.fullScroll(View.FOCUS_DOWN);
                    historyView.fullScroll(View.FOCUS_DOWN);
                    break;
                case BT_New.MESSAGE_DEVICE_NAME:
                    String deviceName = msg.getData().getString(BT_New.DEVICE_NAME);
                    setStatus(getString(R.string.Connected) + "->" + deviceName);
                    Log.d("ccc", "Device Name=" + deviceName);
                    break;
                case BT_New.MESSAGE_TOAST:
                    Log.i("ccc", "MESSAGE_TOAST:" + msg.getData().getString(BT_New.TOAST));
                    Toast.makeText(getApplicationContext(), msg.getData().getString(BT_New.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_connect) {
            app.commMW.SetHandler(mHandler1);
            app.commMW.Connect("", app.SerialPortBaudRateMW);
            Toast.makeText(getApplicationContext(), "App connected: " + app.commMW.Connected, Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.raceflight) {
            Toast.makeText(getApplicationContext(), "Raceflight mode", Toast.LENGTH_SHORT).show();
            raceflightMode();
            configMenu.setAdapter(new StringAdapter(getApplicationContext(), clickListener, pidsTuning));
            return true;
        } else if (id == R.id.betaflight) {
            Toast.makeText(getApplicationContext(), "Betaflight mode", Toast.LENGTH_SHORT).show();
            betaflightMode();
            configMenu.setAdapter(new StringAdapter(getApplicationContext(), clickListener, pidsTuning));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (App) getApplication();

        submit = (Button) findViewById(R.id.submit);
        clear = (Button) findViewById(R.id.clear);
        command = (EditText) findViewById(R.id.command);
        historyBox = (TextView) findViewById(R.id.historybox);
        historyView = (ScrollView) findViewById(R.id.historyView);

        historyBuilder.append("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

        pidsTuningButton = (Button) findViewById(R.id.pids_tuning);
        ratesTuningButton = (Button) findViewById(R.id.rates_tuning);
        configTuningButton = (Button) findViewById(R.id.config_tuning);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.commMW.Write((command.getText().toString() + "\n").getBytes());
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command.setText("");
            }
        });

        configMenu = (RecyclerView) findViewById(R.id.config_menu);

        mLayoutManager = new LinearLayoutManager(this);
        configMenu.setLayoutManager(mLayoutManager);



        pidsTuningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configMenu.setAdapter(new StringAdapter(getApplicationContext(), clickListener, pidsTuning));
            }
        });

        ratesTuningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configMenu.setAdapter(new StringAdapter(getApplicationContext(),clickListener, ratesTuning));
            }
        });

        configTuningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configMenu.setAdapter(new StringAdapter(getApplicationContext(),clickListener, configTuning));
            }
        });

        raceflightMode();
        configMenu.setAdapter(new StringAdapter(getApplicationContext(), clickListener, pidsTuning));
        Toast.makeText(getApplicationContext(), "Raceflight mode", Toast.LENGTH_SHORT).show();
    }

    private final void setStatus(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }




}
