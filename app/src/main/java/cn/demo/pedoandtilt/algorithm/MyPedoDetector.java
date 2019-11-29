package cn.demo.pedoandtilt.algorithm;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.text.DecimalFormat;

/**
 * Use Dynamic Schmitt trigger and state machine
 * to determine acc peak
 */
public class MyPedoDetector implements SensorEventListener {
    private final String TAG = "MyPedoDetector";
    private final String PTAG = "--PPPP--";

    DynamicSchmittTrigger schmitt = new DynamicSchmittTrigger(9.7f, 10.3f, 0.3f);
    VelRing velRing = new VelRing(10);
    OnPedoListener onPedoListener;
    private int pedoCount = 0;

    int peakIdx = 0;
    float csum = 0f;

    public MyPedoDetector(int startPedoCount) {
        super();
        pedoCount = startPedoCount;
        velRing.fill(9.9f);
    }

    DecimalFormat df = new DecimalFormat(".#");

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                checkPedo(calcAcc(event.values));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setOnPedoListener(OnPedoListener onPedoListener) {
        this.onPedoListener = onPedoListener;
    }

    public void onAccData(float[] values) {
        if (values != null) {
            checkPedo(calcAcc(values));
        }
    }

    public void checkPedo(float acc) {
        if (detectPeak(acc)) {
            long peakDist = schmitt.getPeakDist();
            Log.d(PTAG, "---------- Peak detected! " + (++peakIdx) + ", peakDist: " + peakDist);

            if (peakDist >= 166 && peakDist <= 2000) {
                reportPedo();
            }
        }
    }

    synchronized private float calcAcc(float[] values) {
        float acc = (float) Math.sqrt(
                +Math.pow(values[0], 2)
                        + Math.pow(values[1], 2)
                        + Math.pow(values[2], 2)
        );
        return acc;
    }

    public boolean detectPeak(float val) {
        Log.d(PTAG, "acc: " + df.format(val));
        schmitt.calcState(val);
        int onOffState = schmitt.getTransientOnOffState();

        velRing.save(val);
        float mean = velRing.calcMeanBackward(0, 10);

        csum += (val - mean);

//        Log.d(PTAG, "OnOffState: " + onOffState + ", mean: " + mean + "      , csum" + csum);
        boolean onOff = schmitt.getTransientOnOffTrigger();
        if (onOff) {
            schmitt.clearTransientOnOffTrigger();
        }
        return onOff;
    }

    private void reportPedo() {
        pedoCount++;
        if (onPedoListener != null) {
            onPedoListener.onPedo(pedoCount);
        }
    }

    public interface OnPedoListener {
        void onPedo(int stepIdx);

        void onDetectorStateChange(int pedometerState);
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
