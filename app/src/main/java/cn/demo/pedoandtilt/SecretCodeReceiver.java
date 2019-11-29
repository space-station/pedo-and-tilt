package cn.demo.pedoandtilt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class SecretCodeReceiver extends BroadcastReceiver {

    private static final String TAG = "SecretCodeReceiver";

    public SecretCodeReceiver() {
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

        if ("6868".equals(host)) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setClass(context, MainActivity.class);
            context.startActivity(i);
        }
    }
}
