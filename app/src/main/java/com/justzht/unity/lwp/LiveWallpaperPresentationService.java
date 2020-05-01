package com.justzht.unity.lwp;

import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import static com.justzht.unity.lwp.LiveWallpaperUtils.logD;


public class LiveWallpaperPresentationService extends WallpaperService
{
    @Override
    public Engine onCreateEngine()
    {
        logD("onCreateEngine");
        return new LiveWallpaperPresentationServiceEngine();
    }

    public class LiveWallpaperPresentationServiceEngine extends WallpaperService.Engine
    {
        LiveWallpaperPresentationServiceEngine() {
            logD("LiveWallpaperPresentationServiceEngine");
            setTouchEventsEnabled(true);
            setOffsetNotificationsEnabled(true);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            logD("onCreate");
            LiveWallpaperPresentationEventWrapper.getInstance().setupSurfaceHolderInServiceOnCreate(surfaceHolder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            LiveWallpaperPresentationEventWrapper.getInstance().setupSurfaceHolderInServiceOnDestroy(getSurfaceHolder());
            logD("onDestroy");
        }
    }
}
