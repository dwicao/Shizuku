package moe.shizuku.stub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Forwards Shizuku permission requests (sent to the original package name) to the hidden
 * Shizuku build.
 */
public class StubRequestPermissionActivity extends Activity {

    private static final String TAG = "ShizukuStub";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String clone = ClonePackage.get(this);
        Intent intent = getIntent();
        String action = intent != null ? intent.getAction() : null;
        if (clone != null && action != null) {
            try {
                startActivity(new Intent(action)
                        .setPackage(clone)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtras(intent));
            } catch (Throwable t) {
                Log.e(TAG, "failed to forward " + action + " to " + clone, t);
            }
        } else {
            Log.e(TAG, "clone package name not configured");
        }
        finish();
    }
}
