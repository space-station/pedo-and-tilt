package cn.demo.pedoandtilt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView centerText;
    String tiltString = "tilt?";
    String pedoString = "pedo?";
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
        centerText = this.findViewById(R.id.center_text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        myStepDetector = new MyStepDetector(memorizeStep);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(myStepDetector,sensor,SensorManager.SENSOR_DELAY_UI);
        myStepDetector.setOnSensorChangeListener(stepListener);

        myTiltDetector = new MyTiltDetector();
        Sensor gSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(myTiltDetector,gSensor,SensorManager.SENSOR_DELAY_UI);
        myTiltDetector.setOnTiltListener(tiltListener);


        centerText.setText("started");
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(myStepDetector);
        myStepDetector.setOnSensorChangeListener(null);
    }
}
