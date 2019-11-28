package cn.demo.pedoandtilt;

public class SchmittTrigger {

    private float offBar = 7.0f;
    private float onBar = 9.0f;

    private boolean stateOn = true;
    private boolean transientTrigger = false;

    public SchmittTrigger(float low, float high){
        this.offBar = low;
        this.onBar = high;
    }

    public boolean check(float z){
        if(stateOn){
            if(z< offBar){
                stateOn = false;
                transientTrigger = false;
            }
        }else {
            if (z > onBar) {
                stateOn = true;
                transientTrigger = true;
            }else {
                transientTrigger = false;
            }
        }
        return stateOn;
    }

    public boolean isStateOn(){
        return stateOn;
    }

    public boolean getTransientTrigger(){
        return transientTrigger;
    }

    public void clearTransientTrigger(){
        transientTrigger = false;
    }
}
