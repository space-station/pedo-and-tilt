package cn.demo.pedoandtilt;

public class ShiftSchmittTrigger extends SchmittTrigger {
    public static final String PTAG = "--PPPP--";
    boolean previousStateOn = true;
    long lastPeakTs = -1;
    long currentPeakTs = -1;
    long deltaPeakTs = 0;
    private float shiftGap = 0.39f;
    private float lowZ = 0.0f;
    private float highZ = 20.0f;

    public ShiftSchmittTrigger(float low, float high, float shiftGap) {
        super(low, high);
        this.shiftGap = shiftGap;
        previousStateOn = stateOn;
    }

    @Override
    public boolean check(float z) {
        boolean ret = super.check(z);

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
            offBar = highZ - shiftGap;
        } else {
            lowZ = Math.min(lowZ, z);
            onBar = lowZ + shiftGap;
        }
        return ret;
    }

    private void saveCurrentPeakTs(long cur) {
        if (lastPeakTs < 0) {
            deltaPeakTs = 5000;
        } else {
            deltaPeakTs = cur - lastPeakTs;
        }
        lastPeakTs = cur;
    }

    long getDeltaPeak() {
        return deltaPeakTs;
    }
}
