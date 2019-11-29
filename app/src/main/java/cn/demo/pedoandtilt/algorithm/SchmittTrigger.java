package cn.demo.pedoandtilt.algorithm;

public class SchmittTrigger {

    protected float offBar = 7.0f;
    protected float onBar = 9.0f;

    protected boolean stateOn = true;
    protected boolean transientOnTrigger = false;

    /**
     * 0 off,
     * 1 off->on,
     * 2 off->on->off
     */
    protected int transientOnOffState = 0;

    protected int[][] mat = new int[][]{
            {0, 1},
            {2, 1},
            {0, 1}
    };

    public SchmittTrigger(float offBar, float onBar) {
        this.offBar = offBar;
        this.onBar = onBar;
    }

    public boolean calcState(float z) {
        if(stateOn){
            if(z< offBar){
                stateOn = false;
                transientOnTrigger = false;
            }
        }else {
            if (z > onBar) {
                stateOn = true;
                transientOnTrigger = true;
            }else {
                transientOnTrigger = false;
            }
        }

        calcTrancientOnOffState(stateOn);
        return stateOn;
    }

    public boolean isStateOn(){
        return stateOn;
    }

    public boolean getTransientOnTrigger() {
        return transientOnTrigger;
    }

    public void clearTransientOnTrigger() {
        transientOnTrigger = false;
    }

    protected int calcTrancientOnOffState(boolean on) {
        int idx = on ? 1 : 0;
        transientOnOffState = mat[transientOnOffState][idx];
        return transientOnOffState;
    }

    public int getTransientOnOffState() {
        return transientOnOffState;
    }

    public boolean getTransientOnOffTrigger() {
        return transientOnOffState == 2;
    }

    public void clearTransientOnOffTrigger() {
        if (transientOnOffState == 2) {
            transientOnOffState = 0;
        }
    }
}
