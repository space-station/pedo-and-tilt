package cn.demo.pedoandtilt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.hardware.SensorEventListener;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView centerText;
    TextView tilt;
    TextView mGsensor_x;
    TextView mGsensor_y;
    TextView mGsensor_z;
    TextView mAcc_x;
    TextView mAcc_y;
    TextView mAcc_z;
    LinearLayout second;
    String tiltString = "tilt?";
    String pedoString = "pedo?";
    private SensorManager mSensorManager;
    private Sensor gsensor;
    private Sensor accelerometer;
    private float mAccX,mAccY,mAccZ;
    private float mgX,mgY,mgZ;
    private String asX,asY,asZ;
    private String gsX,gsY,gsZ;
    public void refreshText(){
        centerText.setText(pedoString + "\n" + tiltString);
    }

    private SensorManager sensorManager;

    private MyTiltDetector myTiltDetector;
    private MyTiltDetector.OnTiltListener tiltListener = new MyTiltDetector.OnTiltListener() {

        @Override
        public void onTilt(int idx) {
            tiltString = "tilt: detected (" + idx + ")";
            refreshText();
        }
    };

    private MyStepDetector myStepDetector;
    private MyStepDetector.OnSensorChangeListener stepListener = new MyStepDetector.OnSensorChangeListener() {

        @Override
        public void onStep(int steps) {
            pedoString = "pedo: " + steps;
            refreshText();
        }

        @Override
        public void onPedometerStateChange(int pedometerState) {

        }
    };

    int memorizeStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        centerText = this.findViewById(R.id.pedo);
        tilt=this.findViewById(R.id.tilt);
        second=this.findViewById(R.id.second);
        mAcc_x=this.findViewById(R.id.acc_x);
        mAcc_y=this.findViewById(R.id.acc_y);
        mAcc_z=this.findViewById(R.id.acc_z);
        mGsensor_x=this.findViewById(R.id.gsensor_x);
        mGsensor_y=this.findViewById(R.id.gsensor_y);
        mGsensor_z=this.findViewById(R.id.gsensor_z);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //获取gsensor的对象
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //注册数据监听器，当有数据时会回调onSensorChanged方法
        mSensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        tilt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeBackgroundColor();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        myStepDetector = new MyStepDetector(memorizeStep);
        //Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(myStepDetector,accelerometer,SensorManager.SENSOR_DELAY_UI);
        myStepDetector.setOnSensorChangeListener(stepListener);

        myTiltDetector = new MyTiltDetector();
        //Sensor gSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(myTiltDetector,gsensor,SensorManager.SENSOR_DELAY_UI);
        myTiltDetector.setOnTiltListener(tiltListener);


        centerText.setText("started");
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(myStepDetector);
        myStepDetector.setOnSensorChangeListener(null);
    }

    protected void changeBackgroundColor(){
        second.setBackgroundResource(R.color.Green);
    }

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == null)
            return ;
        //判断获取的数据类型是不是gsensor
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //获得数据为float类型的数据
            Log.i("M", "onSensorChanged: acc");
            mAccX = event.values[0];
            mAccY = event.values[1];
            mAccZ = event.values[2];

            DecimalFormat decimalFormat=new DecimalFormat(".00");
            asX=decimalFormat.format(mAccX);
            asY=decimalFormat.format(mAccY);
            asZ=decimalFormat.format(mAccZ);

            mAcc_x.setText("x: "+asX);
            mAcc_y.setText("y: "+asY);
            mAcc_z.setText("z: "+asZ);
        }

        if(event.sensor.getType() == Sensor.TYPE_GRAVITY){
            //获得数据为float类型的数据
            Log.i("M", "onSensorChanged: gravity");
            mgX = event.values[0];
            mgY = event.values[1];
            mgZ = event.values[2];

            DecimalFormat decimalFormat=new DecimalFormat(".00");
            gsX=decimalFormat.format(mgX);
            gsY=decimalFormat.format(mgY);
            gsZ=decimalFormat.format(mgZ);

            mGsensor_x.setText("x: "+gsX);
            mGsensor_y.setText("y: "+gsY);
            mGsensor_z.setText("z: "+gsZ);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
