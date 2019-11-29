package cn.demo.pedoandtilt;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.demo.pedoandtilt.algorithm.MyPedoDetector;
import cn.demo.pedoandtilt.algorithm.MyTiltDetector;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final String XTAG = "xxxxxxxxxxxxxx";

    TextView accXText;
    TextView accYText;
    TextView accZText;
    TextView aStepCounterText;
    TextView qPedometerText;
    TextView aWristTiltText;

    TextView pedoText;
    TextView tiltText;

    private int aStep1st = -1, qPedo1st = -1;
    LinearLayout second;
    boolean defaultColor = true;
    private int aWristTilt = 0;

    private SensorManager sensorManager;
    private MyPedoDetector myPedoDetector;
    private MyPedoDetector.OnPedoListener pedoListener = new MyPedoDetector.OnPedoListener() {

        @Override
        public void onPedo(int pedoIdx) {
            pedoText.setText("pedo: " + pedoIdx);
        }

        @Override
        public void onDetectorStateChange(int detectorState) {
        }
    };

    private MyTiltDetector myTiltDetector;
    private MyTiltDetector.OnTiltListener tiltListener = new MyTiltDetector.OnTiltListener() {

        @Override
        public void onTilt(int tiltIdx) {
            tiltText.setText("tilt: " + tiltIdx);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        pedoText = this.findViewById(R.id.pedo);
        tiltText = this.findViewById(R.id.tilt);
        second=this.findViewById(R.id.second);
        accXText = this.findViewById(R.id.acc_x);
        accYText = this.findViewById(R.id.acc_y);
        accZText = this.findViewById(R.id.acc_z);
        aStepCounterText =this.findViewById(R.id.gsensor_x);
        qPedometerText =this.findViewById(R.id.gsensor_y);
        aWristTiltText =this.findViewById(R.id.gsensor_z);

        setTextColor();

        tiltText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                changeBackgroundColor();
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }

    @Override
    protected void onStart() {
        super.onStart();

        myPedoDetector = new MyPedoDetector(0);
        myPedoDetector.setOnPedoListener(pedoListener);

        myTiltDetector = new MyTiltDetector();
        myTiltDetector.setOnTiltListener(tiltListener);

        pedoText.setText("pedo: 0");
        tiltText.setText("tilt: 0");

        registerSensors();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }


    DecimalFormat decimalFormat=new DecimalFormat(".00");

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }

        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            showAcc(event);

            myPedoDetector.onSensorChanged(event);
            myTiltDetector.onSensorChanged(event);
        } else if (type == Sensor.TYPE_STEP_DETECTOR) {
//            Log.i(XTAG, "TYPE_STEP_DETECTOR: " + event.values[0] +"(" + event.values.length +")");
            int stepDetected = (int)event.values[0];
        } else if (type == Sensor.TYPE_STEP_COUNTER) {
            int tmp = (int)event.values[0];
            if(aStep1st < 0){
                aStep1st = tmp;
            }
            //Log.i(XTAG, "TYPE_STEP_COUNTER: " + event.values[0] +"(" + event.values.length +")");
            int aStep = tmp - aStep1st + 1;
            aStepCounterText.setText("aStep: "+aStep);
        } else if (type == TYPE_QTI_PEDO) {
            int tmp = (int)event.values[0];
            if(qPedo1st < 0){
                qPedo1st = tmp;
            }
//            Log.i(XTAG, "TYPE_QTI_PEDO: " + event.values[0] +"(" + event.values.length +")");
            int qPedo = tmp - qPedo1st + 1;
            qPedometerText.setText("qPedo: "+qPedo);
        } else if (type == TYPE_WRIST_TILT) {
//            Log.i(XTAG, "TYPE_WRIST_TILT: " + event.values[0] +"(" + event.values.length +")");
            aWristTilt += (int)event.values[0];
            aWristTiltText.setText("aTilt: "+aWristTilt);
        } else if (type == TYPE_QTI_AMD) {
//            Log.i(XTAG, "TYPE_QTI_AMD: " + event.values[0] +"(" + event.values.length +")");
        } else if (type == TYPE_QTI_RMD) {
//            Log.i(XTAG, "TYPE_QTI_RMD: " + event.values[0] +"(" + event.values.length +")");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public static int TYPE_WRIST_TILT = 26;
    public static int TYPE_QTI_AMD = 33171006;
    public static int TYPE_QTI_RMD = 33171007;
    public static int TYPE_QTI_PEDO = 33171009;
    public int[] usingTypes = new int[]{
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_STEP_DETECTOR,
            Sensor.TYPE_STEP_COUNTER,
            TYPE_WRIST_TILT,
            TYPE_QTI_AMD,
            TYPE_QTI_RMD,
            TYPE_QTI_PEDO
    };

    public void registerSensors() {
        Set<Integer> usingSet = new HashSet<>();
        for(int i : usingTypes){
            usingSet.add(i);
        }
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Set<String> tmp = new HashSet<>();
        for (Sensor s : sensorList) {
            if(!usingSet.contains(s.getType())){
                continue;
            }
            if(!tmp.add(s.getStringType())){
                continue;
            }
            Log.d(XTAG, s.getStringType() + " (" + s.getType() + ")");
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void changeBackgroundColor(){
        defaultColor = !defaultColor;
        int res = defaultColor ? android.R.color.background_light : R.color.Green;
        second.setBackgroundResource(res);
    }

    void setTextColor(){
        pedoText.setTextColor(Color.RED);
        tiltText.setTextColor(Color.RED);

        accXText.setTextColor(Color.BLUE);
        accYText.setTextColor(Color.BLUE);
        accZText.setTextColor(Color.BLUE);
        aStepCounterText.setTextColor(Color.GREEN);
        qPedometerText.setTextColor(Color.GREEN);
        aWristTiltText.setTextColor(Color.GREEN);
    }

    void showAcc(@NonNull SensorEvent event) {
        accXText.setText("x: " + decimalFormat.format(event.values[0]));
        accYText.setText("y: " + decimalFormat.format(event.values[1]));
        accZText.setText("z: " + decimalFormat.format(event.values[2]));
    }
}
