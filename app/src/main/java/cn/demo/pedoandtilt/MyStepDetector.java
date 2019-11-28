package cn.demo.pedoandtilt;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class MyStepDetector implements SensorEventListener {
    private final String TAG = "MyStepDetector";
    private final String PTAG = "--PPPP--";

    ShiftSchmittTrigger schmitt = new ShiftSchmittTrigger(9.7f, 10.3f, 0.5f);
    VelRing velRing = new VelRing(10);

    float lastAcceleration = -1;
    OnStepListener onStepListener;

    public static int CURRENT_STEP = 0;
    public static int TEMP_STEP = 0;
    int peakIdx = 0;
    float csum = 0f;
    private int pedometerState = 2;

    public MyStepDetector(int newSteps) {
        super();
        CURRENT_STEP = newSteps;
        velRing.fill(9.9f);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                calcAcceleration(event);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setOnStepListener(OnStepListener onStepListener) {
        this.onStepListener = onStepListener;
    }

    synchronized private void calcAcceleration(SensorEvent event) {
        float acceleration = (float) Math.sqrt(Math.pow(event.values[0], 2)
                + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
        detectStep(acceleration);
    }

    public void detectStep(float curAcceleration) {
        Log.d(PTAG, "acc: " + curAcceleration);
        if (lastAcceleration > 0) {
            boolean detected = detectPeak(curAcceleration, lastAcceleration);
            if (detected) {
                long deltaTime2LastPeak = schmitt.getDeltaPeak();
                Log.d(PTAG, "----------Peak detected!" + (++peakIdx) + ", deltaTime: " + deltaTime2LastPeak);

                if (deltaTime2LastPeak >= 166 && deltaTime2LastPeak <= 2000) {
                    reportStep();
                }
                if (deltaTime2LastPeak >= 200) {
                }
            }
        }
        lastAcceleration = curAcceleration;
    }

    public boolean detectPeak(float newValue, float oldValue) {
        schmitt.check(newValue);
        int onOffState = schmitt.getTransientOnOffState();

        velRing.save(newValue);
        float mean = velRing.calcMeanBackward(0, 10);

        csum += (newValue - mean);

        Log.d(PTAG, "mean:" + mean + ", OnOffState: " + onOffState + "      , csum" + csum);
        boolean onOff = schmitt.getTransientOnOffTrigger();
        if (onOff) {
            schmitt.clearTransientOnOffTrigger();
        }
        return onOff;
    }

    private void reportStep() {
        CURRENT_STEP++;
        if (onStepListener != null) {
            onStepListener.onStep(CURRENT_STEP);
        }
    }

    public interface OnStepListener {
        void onStep(int stepIdx);
        void onPedometerStateChange(int pedometerState);
    }

    class VelRing {

        private int RING_SIZE = 10;
        private float[] ring = new float[RING_SIZE];
        private int cursor = RING_SIZE - 1;

        public VelRing(int size) {
            RING_SIZE = size;
        }

        private int increaseCursor() {
            cursor++;
            cursor = cursor % RING_SIZE;
            return cursor;
        }

        void fill(float f) {
            for (int i = 0; i < RING_SIZE; i++) {
                ring[i] = f;
            }
        }

        void save(float v) {
            int c = increaseCursor();
            ring[c] = Math.abs(v);
        }

        float getVel() {
            return ring[cursor];
        }

        float calcMeanBackward(int offset, int n) {
            float sum = 0;
            int l = -n;
            for (int i = 0; i > l; i--) {
                int c = (cursor + offset + i) % RING_SIZE;
                if (c < 0) {
                    c = c + RING_SIZE;
                }
                // special case
                sum += ring[c];
            }
            return sum / n;
        }
    }

}
