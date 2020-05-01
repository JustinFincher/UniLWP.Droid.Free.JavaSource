package com.justzht.unity.lwp;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowInsets;

import static com.justzht.unity.lwp.LiveWallpaperUtils.logD;

public enum LiveWallpaperPresentationEventWrapper
{
    INSTANCE();

    /**
     * Default Singleton Instance Getter Method
     * @return Instance
     */
    public static LiveWallpaperPresentationEventWrapper getInstance() {
        return INSTANCE;
    }

    SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            logD("surfaceCreated for holder " + surfaceHolder);
            LiveWallpaperManager.getInstance().connectUnityDisplay(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            logD("surfaceChanged for holder " + surfaceHolder);
            LiveWallpaperManager.getInstance().connectUnityDisplay(surfaceHolder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            logD("surfaceDestroyed for holder " + surfaceHolder);
            LiveWallpaperManager.getInstance().disconnectUnityDisplay(surfaceHolder);
        }
    };

    public void setupSurfaceViewInActivityOnCreate(SurfaceView surfaceView)
    {
        surfaceView.getHolder().addCallback(surfaceHolderCallback);
    }

    public void setupSurfaceViewInActivityOnDestroy(SurfaceView surfaceView)
    {
        surfaceView.getHolder().removeCallback(surfaceHolderCallback);
    }

    public void setupSurfaceHolderInServiceOnCreate(SurfaceHolder surfaceHolder)
    {
        surfaceHolder.addCallback(surfaceHolderCallback);
    }

    public void setupSurfaceHolderInServiceOnDestroy(SurfaceHolder surfaceHolder)
    {
        surfaceHolder.removeCallback(surfaceHolderCallback);
    }
}
