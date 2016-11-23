package com.example.juan.javaopencvtest;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HciTest3Activity extends AppCompatActivity implements View.OnClickListener{
    private SensorManager sensorManager;
    private TextView lightLevel;
    private ImageView levelImge;
    private Button return_edit;
    private LinearLayout figure_show;
    private  int[] images = new int[]{
            R.drawable.sunday1,
            R.drawable.cloudy,
            R.drawable.night,

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_hcitest3);
        figure_show = (LinearLayout)findViewById(R.id.show_figure3);
        return_edit = (Button)findViewById(R.id.return_edit3);
        lightLevel = (TextView)findViewById(R.id.light_level);
        levelImge  =(ImageView)findViewById(R.id.show_leve_img);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(listener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sensorManager !=null){
            sensorManager.unregisterListener(listener);
        }
    }
    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float value = event.values[0];
            if(value<1){
                levelImge.setImageResource(images[2]);
            }else if(value>=1&&value<501){
                levelImge.setImageResource(images[1]);
            }else{
                levelImge.setImageResource(images[0]);
            }
            lightLevel.setText(""+value+" lx");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.show_figure3:
                Intent intent3 = new Intent(HciTest3Activity.this, figureCheck.class);
                startActivity(intent3);
                break;
            case R.id.return_edit3:

                Intent intent4 = new Intent(HciTest3Activity.this, MainActivity.class);
                startActivity(intent4);
                break;

        }
    }
    private void initListener(){
        figure_show.setOnClickListener(this);
        return_edit.setOnClickListener(this);

    }
}
