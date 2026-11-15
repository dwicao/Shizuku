package moe.shizuku.stub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Forwards legacy binder requests (sent to the original package name) to the hidden
 * Shizuku build.
 */
public class StubBinderRequestReceiver extends BroadcastReceiver {

    private static final String TAG = "ShizukuStub";

    @Override
    public void onReceive(Context context, Intent intent) {
        String clone = ClonePackage.get(context);
        if (clone == null) {
            Log.e(TAG, "clone package name not configured");
            return;
        }

        try {
            context.sendBroadcast(new Intent(intent.getAction())
                    .setPackage(clone)
                    .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                    .putExtras(intent));
        } catch (Throwable t) {
            Log.e(TAG, "failed to forward " + intent.getAction() + " to " + clone, t);
        }
    }
}
