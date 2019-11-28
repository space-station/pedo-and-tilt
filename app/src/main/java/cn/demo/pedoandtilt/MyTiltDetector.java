package cn.demo.pedoandtilt;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class MyTiltDetector implements SensorEventListener {

    public static final String ATAG = "======";

    int tiltIdx = 0;

    SchmittTrigger schmitt = new SchmittTrigger(7.0f, 9.0f);
    ArcVelRing arcVelRing = new ArcVelRing(10);

    long lastTimestamp = -1;
    float lastArc = -100.0f;


    public interface OnTiltListener {
        void onTilt(int tiltIdx);
    }

    OnTiltListener tiltListener;

    public void setOnTiltListener(OnTiltListener tiltListener) {
        this.tiltListener = tiltListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        schmitt.check(z);

        int deltaArc = (int)(calcDeltaArc(z) * 1000);
        arcVelRing.save(deltaArc);

        if(schmitt.getTransientTrigger() && checkSpeed()){
            schmitt.clearTransientTrigger();
            tiltListener.onTilt(++tiltIdx);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    float calcDeltaArc(float z){
        float gz = 9.85f;
        if(z>gz){
            z = gz;
        }
        else if(z<-gz){
            z = -gz;
        }

        float arc = (float)Math.asin(z/gz);

        if(lastArc < -100.0f){
            lastArc = arc;
            return 0.0f;
        }

        float delta = arc - lastArc;
        lastArc = arc;
        return delta;
    }

    int speedCap = 1000;
    int speedFloor = 300;

    boolean checkSpeed(){
        int mean = arcVelRing.calcMeanBackward(-1, 3);
        //Log.d(ATAG, "arcVelRing.calcMeanBackward: " + mean);

        if(arcVelRing.getArcVel() < speedFloor && arcVelRing.calcMeanBackward(-1, 5) < speedFloor){
            schmitt.clearTransientTrigger();
            return false;
        }
        if(arcVelRing.getArcVel() < speedFloor && mean > speedFloor && mean < speedCap){
            return true;
        }
        return false;
    }

    class ArcVelRing{

        private int RING_SIZE = 10;
        private int[] ring = new int[RING_SIZE];
        private int cursor = RING_SIZE - 1;

        public ArcVelRing(int size){
            RING_SIZE = size;
        }

        private int increaseCursor(){
            cursor++;
            cursor = cursor % RING_SIZE;
            return cursor;
        }

        void save(int v){
            int c = increaseCursor();
            ring[c] = Math.abs(v);
        }

        int getArcVel(){
            return ring[cursor];
        }
        int calcMeanBackward(int offset, int n){
            int sum = 0;
            int l = -n;
            for(int i = 0; i > l; i--){
                int c = ( cursor + offset + i) % RING_SIZE;
                if(c < 0){
                    c = c + RING_SIZE;
                }
                // special case
                if(i == 0 && ring[c] >= speedCap) return speedCap;
                sum += ring[c];
            }
            return sum / n;
        }
    }

    // deltaT is about 200 ms
    long calcDeltaT(long timestamp){
        if(lastTimestamp < 0){
            lastTimestamp = timestamp;
            return 0;
        }

        long delta = timestamp - lastTimestamp;
        lastTimestamp = timestamp;
        return delta/1000_000;
    }
}
