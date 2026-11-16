package rikka.shizuku.shell;

import android.content.AttributionSource;
import android.content.IContentProvider;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.system.Os;
import android.text.TextUtils;

import rikka.hidden.compat.ActivityManagerApis;

import java.io.File;

import dalvik.system.BaseDexClassLoader;
import rikka.hidden.compat.PackageManagerApis;
import stub.dalvik.system.VMRuntimeHidden;

public class ShizukuShellLoader {

    private static String[] args;
    private static String callingPackage;
    private static Handler handler;

    private static final Binder receiverBinder = new Binder() {

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                IBinder binder = data.readStrongBinder();

                String sourceDir = data.readString();
                if (binder != null) {
                    handler.post(() -> onBinderReceived(binder, sourceDir));
                } else {
                    System.err.println("Server is not running");
                    System.err.flush();
                    System.exit(1);
                }
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    private static void requestForBinder() throws Exception {
        String managerApplicationId = System.getenv("MANAGER_APPLICATION_ID");
        if (TextUtils.isEmpty(managerApplicationId) || "MANAGER_PKG".equals(managerApplicationId)) {
            managerApplicationId = BuildConfig.MANAGER_APPLICATION_ID;
        }

        // The manager provider is protected by INTERACT_ACROSS_USERS_FULL, which shell holds.
        // Never fall back to a broadcast: the binder must not be handed to random apps.
        String authority = managerApplicationId + ".shizuku";
        int userId = Os.getuid() / 100000;
        IContentProvider provider = ActivityManagerApis.getContentProviderExternal(authority, userId, null, authority);
        if (provider == null) {
            throw new IllegalStateException("Couldn't find Shizuku provider " + authority);
        }

        Bundle extras = new Bundle();
        extras.putBinder("binder", receiverBinder);

        Bundle reply;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AttributionSource.Builder source = new AttributionSource.Builder(Os.getuid());
            source.setPackageName(callingPackage);
            reply = provider.call(source.build(), authority, "requestBinderForShell", null, extras);
        } else if (Build.VERSION.SDK_INT >= 30) {
            reply = provider.call(callingPackage, null, authority, "requestBinderForShell", null, extras);
        } else if (Build.VERSION.SDK_INT >= 29) {
            reply = provider.call(callingPackage, authority, "requestBinderForShell", null, extras);
        } else {
            reply = provider.call(callingPackage, "requestBinderForShell", null, extras);
        }

        ActivityManagerApis.removeContentProviderExternal(authority, null);

        if (reply == null) {
            throw new IllegalStateException("Shizuku provider rejected the shell request");
        }
    }

    private static void onBinderReceived(IBinder binder, String sourceDir) {
        var base = sourceDir.substring(0, sourceDir.lastIndexOf('/'));
        String librarySearchPath = base + "/lib/" + VMRuntimeHidden.getRuntime().vmInstructionSet();
        String systemLibrarySearchPath = System.getProperty("java.library.path");
        if (!TextUtils.isEmpty(systemLibrarySearchPath)) {
            librarySearchPath += File.pathSeparatorChar + systemLibrarySearchPath;
        }

        try {
            System.out.println("Entering shell...");
            var classLoader = new BaseDexClassLoader(sourceDir, null, librarySearchPath, ClassLoader.getSystemClassLoader());
            Class<?> cls = classLoader.loadClass("moe.shizuku.manager.shell.Shell");
            cls.getDeclaredMethod("main", String[].class, String.class, IBinder.class, Handler.class)
                    .invoke(null, args, callingPackage, binder, handler);
        } catch (ClassNotFoundException tr) {
            System.err.println("Class not found");
            System.err.println("Make sure you have Shizuku v12.0.0 or above installed");
            System.err.flush();
            System.exit(1);
        } catch (Throwable tr) {
            tr.printStackTrace(System.err);
            System.err.flush();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        ShizukuShellLoader.args = args;

        String packageName;
        var pkg = PackageManagerApis.getPackagesForUidNoThrow(Os.getuid());
        if (pkg.size() == 1) {
            packageName = pkg.get(0);
        } else {
            packageName = System.getenv("RISH_APPLICATION_ID");
            if (TextUtils.isEmpty(packageName) || "PKG".equals(packageName)) {
                abort("RISH_APPLICATION_ID is not set, please set this environment variable in rish to the package name of the terminal app");
                System.exit(1);
            }
        }

        ShizukuShellLoader.callingPackage = packageName;

        if (Looper.getMainLooper() == null) {
            Looper.prepareMainLooper();
        }

        handler = new Handler(Looper.getMainLooper());

        try {
            requestForBinder();
        } catch (Throwable tr) {
            tr.printStackTrace(System.err);
            System.err.flush();
            System.exit(1);
        }

        handler.postDelayed(() -> abort(
                String.format(
                        "Request timeout. " +
                        "If you are using stealth mode, MANAGER_APPLICATION_ID may not be correct. Please set this environment variable in rish to the package name of Shizuku.\n" +
                        "Otherwise, the connection between the current app (%1$s) and Shizuku app may be blocked by your system. " +
                        "Please disable all battery optimization features for both current app (%1$s) and Shizuku app.",
                        packageName)
        ), 5000);

        Looper.loop();
        System.exit(0);
    }

    private static void abort(String message) {
        System.err.println(message);
        System.err.flush();
        System.exit(1);
    }
}
