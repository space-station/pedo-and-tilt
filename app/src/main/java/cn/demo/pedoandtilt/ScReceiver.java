package cn.demo.pedoandtilt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ScReceiver extends BroadcastReceiver {

    private static final String TAG = "ScReceiver";

    public ScReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String host = null;
        Uri uri = intent.getData();
        if (uri != null) {
            host = uri.getHost();
        } else {
            Log.d(TAG, "uri is null");
            return;
        }
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if ("6868".equals(host)) {
            i.setClass(context, MainActivity.class);
            context.startActivity(i);
        }
    }
}
