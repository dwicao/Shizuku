package moe.shizuku.stub;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Reads the package name of the hidden Shizuku build from the stub's application metadata.
 * It is patched in at stub creation time (see the Shizuku manager app); the placeholder value
 * is used when the same binary is installed without being patched (should not happen).
 */
public final class ClonePackage {

    private ClonePackage() {
        throw new AssertionError();
    }

    public static final String META_DATA_NAME = "moe.shizuku.stub.CLONE_PACKAGE";
    private static final String PLACEHOLDER = "@@CLONE_PACKAGE@@";

    public static String get(Context context) {
        try {
            Bundle metaData = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData;
            if (metaData == null) {
                return null;
            }
            String value = metaData.getString(META_DATA_NAME);
            if (value == null || value.isEmpty() || PLACEHOLDER.equals(value)) {
                return null;
            }
            return value;
        } catch (Throwable t) {
            return null;
        }
    }
}
