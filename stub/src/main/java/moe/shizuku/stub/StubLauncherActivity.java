package moe.shizuku.stub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Launcher activity for the stub. Forwards the launch to the hidden Shizuku build.
 */
public class StubLauncherActivity extends Activity {

    private static final String TAG = "ShizukuStub";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String clone = ClonePackage.get(this);
        if (clone != null) {
            try {
                startActivity(new Intent(Intent.ACTION_MAIN)
                        .setPackage(clone)
                        .addCategory(Intent.CATEGORY_LAUNCHER)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (Throwable t) {
                Log.e(TAG, "failed to launch " + clone, t);
            }
        } else {
            Log.e(TAG, "clone package name not configured");
        }
        finish();
    }
}
