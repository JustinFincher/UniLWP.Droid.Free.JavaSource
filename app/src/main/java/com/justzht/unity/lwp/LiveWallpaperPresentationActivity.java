package com.justzht.unity.lwp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class LiveWallpaperPresentationActivity extends Activity
{
    protected SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        LiveWallpaperPresentationEventWrapper.getInstance().setupSurfaceViewInActivityOnCreate(surfaceView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiveWallpaperPresentationEventWrapper.getInstance().setupSurfaceViewInActivityOnDestroy(surfaceView);
    }
}
