package cn.demo.pedoandtilt;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MyTiltDetector implements SensorEventListener {


    public interface OnTiltListener {
        void onTilt(int idx);
    }

    OnTiltListener tiltListener;
    public void setOnTiltListener(OnTiltListener tiltListener) {
        this.tiltListener = tiltListener;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
