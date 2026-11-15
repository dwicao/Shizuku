package moe.shizuku.stub;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import moe.shizuku.api.BinderContainer;

/**
 * Proxies calls to the Shizuku manager provider (hardcoded to the original package by
 * client apps and by the Shizuku server) to the hidden Shizuku build's provider.
 * Binders pass through {@link #call}, so the proxied bundle is re-parcelled unchanged.
 */
public class StubShizukuProvider extends ContentProvider {

    private static final String TAG = "ShizukuStub";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        android.content.Context context = getContext();
        if (context == null) {
            return null;
        }

        String clone = ClonePackage.get(context);
        if (clone == null) {
            Log.e(TAG, "clone package name not configured");
            return null;
        }

        Uri cloneUri = Uri.parse("content://" + clone + ".shizuku");

        Bundle forwarded = extras != null ? extras : new Bundle();
        forwarded.setClassLoader(BinderContainer.class.getClassLoader());

        try {
            return context.getContentResolver().call(cloneUri, method, arg, forwarded);
        } catch (Throwable t) {
            Log.e(TAG, "failed to proxy provider call to " + clone, t);
            return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
