package com.justzht.unity.lwp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import static com.justzht.unity.lwp.LiveWallpaperUtils.logD;
import static com.justzht.unity.lwp.LiveWallpaperUtils.logE;

/**
 * The default content provider for live wallpaper to init
 */
public class LiveWallpaperInitProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        if (getContext() == null)
        {
            logE("InitProvider.onCreate -> getContext == null");
            return false;
        }
        logD(String.format("Create With Context %s, defaultActivity class %s, defaultService class %s",getContext(),getDefaultActivity(),getDefaultService()));
        LiveWallpaperManager.getInstance().defaultActivity = getDefaultActivity();
        LiveWallpaperManager.getInstance().defaultService = getDefaultService();
        LiveWallpaperManager.getInstance().Init(getContext());
        return true;
    }

    protected Class getDefaultActivity()
    {
        return LiveWallpaperPresentationActivity.class;
    }

    protected Class getDefaultService()
    {
        return LiveWallpaperPresentationService.class;
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
