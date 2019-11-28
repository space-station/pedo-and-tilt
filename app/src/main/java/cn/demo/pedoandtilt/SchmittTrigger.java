package cn.demo.pedoandtilt;

public class SchmittTrigger {

    protected float offBar = 7.0f;
    protected float onBar = 9.0f;

    protected boolean stateOn = true;
    protected boolean transientOnTrigger = false;
    protected int transientOnOffState = 0; //0 off, 1 off->on, 2 off->on->off

    protected int[][] mat = new int[][]{
            {0, 1},
            {2, 1},
            {0, 1}
    };

    public SchmittTrigger(float low, float high){
        this.offBar = low;
        this.onBar = high;
    }

    public boolean check(float z){
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

        checkTrancientOnOff(stateOn);
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

    protected void checkTrancientOnOff(boolean on) {
        int idx = on ? 1 : 0;
        transientOnOffState = mat[transientOnOffState][idx];
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
