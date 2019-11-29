package cn.demo.pedoandtilt.algorithm;

public class DynamicSchmittTrigger extends SchmittTrigger {
    public static final String PTAG = "--PPPP--";

    boolean previousStateOn = true;
    long lastPeakTs = -1;
    long currentPeakTs = -1;
    long peakDist = 0;
    private float deltaFlipping = 0.39f;
    private float lowZ = 0.0f;
    private float highZ = 20.0f;

    public DynamicSchmittTrigger(float low, float high, float flipDelta) {
        super(low, high);
        this.deltaFlipping = flipDelta;
        previousStateOn = stateOn;
    }

    @Override
    public boolean calcState(float z) {
        boolean state = super.calcState(z);

        if (previousStateOn != stateOn) {
            if (stateOn) {
                highZ = z;
            } else {
                lowZ = z;
                saveCurrentPeakTs(currentPeakTs);
            }
        }

        previousStateOn = stateOn;

        if (stateOn) {
            highZ = Math.max(highZ, z);
            if (highZ == z) {
                currentPeakTs = System.currentTimeMillis();
            }
            offBar = highZ - deltaFlipping;
        } else {
            lowZ = Math.min(lowZ, z);
            onBar = lowZ + deltaFlipping;
        }
        return state;
    }

    private void saveCurrentPeakTs(long cur) {
        if (lastPeakTs < 0) {
            peakDist = 5000;
        } else {
            peakDist = cur - lastPeakTs;
        }
        lastPeakTs = cur;
    }

    public long getPeakDist() {
        return peakDist;
    }
}
