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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final String XTAG = "xxxxxxxxxxxxxx";

    TextView mAcc_x;
    TextView mAcc_y;
    TextView mAcc_z;
    TextView aStepCounterText;
    TextView qPedometerText;
    TextView aWristTiltText;

    TextView pedoText;
    TextView tiltText;

    LinearLayout second;

    private float accX, accY, accZ;
    private int aStep = 0,qPedo = 0, aWristTilt = 0;
    private int aStep1st = -1, qPedo1st = -1;
    boolean dft = true;

    private int myPedo, myTilt;

    private SensorManager sensorManager;

    Set<String> receivedTypeSet = new HashSet<String>();
    List<String> receivedTypeList = new ArrayList<String>();

    private MyStepDetector myStepDetector;
    int memorizeStep = 0;
    private MyStepDetector.OnSensorChangeListener stepListener = new MyStepDetector.OnSensorChangeListener() {

        @Override
        public void onStep(int stepIdx) {
            pedoText.setText("pedo: " + stepIdx);
        }

        @Override
        public void onPedometerStateChange(int pedometerState) {

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
        setContentView(R.layout.activity_main);
        pedoText = this.findViewById(R.id.pedo);
        tiltText = this.findViewById(R.id.tilt);
        second=this.findViewById(R.id.second);
        mAcc_x=this.findViewById(R.id.acc_x);
        mAcc_y=this.findViewById(R.id.acc_y);
        mAcc_z=this.findViewById(R.id.acc_z);
        aStepCounterText =this.findViewById(R.id.gsensor_x);
        qPedometerText =this.findViewById(R.id.gsensor_y);
        aWristTiltText =this.findViewById(R.id.gsensor_z);

        setTextColor();

        tiltText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeBackgroundColor();
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }

    @Override
    protected void onStart() {
        super.onStart();

        myStepDetector = new MyStepDetector(memorizeStep);
        myStepDetector.setOnSensorChangeListener(stepListener);

        myTiltDetector = new MyTiltDetector();
        myTiltDetector.setOnTiltListener(tiltListener);

        registerSensors();

        pedoText.setText("pedo: 0");
        tiltText.setText("tilt: 0");
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

        //判断获取的数据类型是不是gsensor
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //获得数据为float类型的数据
            showAcc(event);

            myStepDetector.onSensorChanged(event);
            myTiltDetector.onSensorChanged(event);
        }

        else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            //获得数据为float类型的数据
//            Log.i(XTAG, "TYPE_STEP_DETECTOR: " + event.values[0] +"(" + event.values.length +")");
            int stepDetected = (int)event.values[0];
        }

        else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            //获得数据为float类型的数据
            int tmp = (int)event.values[0];
            if(aStep1st < 0){
                aStep1st = tmp;
            }
            //Log.i(XTAG, "TYPE_STEP_COUNTER: " + event.values[0] +"(" + event.values.length +")");
            aStep = tmp - aStep1st + 1;
            aStepCounterText.setText("aStep: "+aStep);
        }

        else if (event.sensor.getType() == TYPE_QTI_PEDO) {
            //获得数据为float类型的数据
            int tmp = (int)event.values[0];
            if(qPedo1st < 0){
                qPedo1st = tmp;
            }
//            Log.i(XTAG, "TYPE_QTI_PEDO: " + event.values[0] +"(" + event.values.length +")");
            qPedo = tmp - qPedo1st + 1;
            qPedometerText.setText("qPedo: "+qPedo);
        }

        else if (event.sensor.getType() == TYPE_WRIST_TILT) {
            //获得数据为float类型的数据
//            Log.i(XTAG, "TYPE_WRIST_TILT: " + event.values[0] +"(" + event.values.length +")");
            aWristTilt += (int)event.values[0];
            aWristTiltText.setText("aTilt: "+aWristTilt);
        }

        else if (event.sensor.getType() == TYPE_QTI_AMD) {
            //获得数据为float类型的数据
//            Log.i(XTAG, "TYPE_QTI_AMD: " + event.values[0] +"(" + event.values.length +")");
        }

        else if (event.sensor.getType() == TYPE_QTI_RMD) {
            //获得数据为float类型的数据
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
            Log.d(XTAG, s.getStringType() + " (" + s.getType()+","+ s.getId() + ")");
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void changeBackgroundColor(){
        dft = !dft;
        int res = dft ? android.R.color.background_light : R.color.Green;
        second.setBackgroundResource(res);
    }

    void setTextColor(){
        pedoText.setTextColor(Color.RED);
        tiltText.setTextColor(Color.RED);

        mAcc_x.setTextColor(Color.BLUE);
        mAcc_y.setTextColor(Color.BLUE);
        mAcc_z.setTextColor(Color.BLUE);
        aStepCounterText.setTextColor(Color.GREEN);
        qPedometerText.setTextColor(Color.GREEN);
        aWristTiltText.setTextColor(Color.GREEN);
    }

    void showAcc(SensorEvent event){
        accX = event.values[0];
        accY = event.values[1];
        accZ = event.values[2];

        mAcc_x.setText("x: "+ decimalFormat.format(accX));
        mAcc_y.setText("y: "+ decimalFormat.format(accY));
        mAcc_z.setText("z: "+ decimalFormat.format(accZ));
    }
}
