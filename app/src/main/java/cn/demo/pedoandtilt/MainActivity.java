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

    private MyStepDetector myStepDetector;
    private SensorManager sensorManager;
    private MyStepDetector.OnSensorChangeListener stepListener = new MyStepDetector.OnSensorChangeListener() {

        @Override
        public void onStepsListenerChange(int steps) {
            centerText.setText("pedo: " + steps);
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
        myStepDetector = new MyStepDetector(memorizeStep);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(myStepDetector,sensor,SensorManager.SENSOR_DELAY_UI);
        myStepDetector.setOnSensorChangeListener(stepListener);

        centerText.setText("started");
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(myStepDetector);
        myStepDetector.setOnSensorChangeListener(null);
    }
}
