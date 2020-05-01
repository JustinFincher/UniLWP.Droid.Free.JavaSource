package com.justzht.unity.lwp;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.unity3d.player.IUnityPlayerLifecycleEvents;
import com.unity3d.player.UnityPlayer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.justzht.unity.lwp.LiveWallpaperUtils.logD;
import static com.justzht.unity.lwp.LiveWallpaperUtils.logE;

public enum LiveWallpaperManager implements IUnityPlayerLifecycleEvents
{
    INSTANCE();

    /**
     * Default Singleton Instance Getter Method
     * @return Instance
     */
    public static LiveWallpaperManager getInstance() {
        return INSTANCE;
    }

    /**
     * Unity Instance
     */
    public UnityPlayer unityPlayer;
    private WeakReference<Context> applicationContextWeakReference = new WeakReference<>(null);
    public Context getContext()
    {
        return applicationContextWeakReference.get();
    }
    public Class defaultActivity = LiveWallpaperPresentationActivity.class;
    public Class defaultService = LiveWallpaperPresentationService.class;
    public boolean hasContext()
    {
        return getContext() != null;
    }
    private volatile HashMap<Integer,WeakReference<SurfaceHolder>> holdersOfUnity = new HashMap<>();
    public volatile boolean unityInstancePaused = true;

    public void Init(Context applicationContext)
    {
        logD("Init -> " + applicationContext);
        applicationContextWeakReference = new WeakReference<>(applicationContext);
        LoadUnity(applicationContext);
    }

    public void DeInit(Context applicationContext)
    {
        UnloadUnity();
        unityPlayer.quit();
    }

    public void LoadUnity(Context context)
    {
        logD("LiveWallpaperManager.LoadUnity");
        logD("context.getPackageName() -> " + context.getPackageName());
        logD("getResources.getIdentifier() -> " + context.getResources().getIdentifier("game_view_content_description", "string", context.getPackageName()));
        unityPlayer = new UnityPlayer(context, this);
        unityPlayer.requestFocus();
    }

    public void UnloadUnity()
    {
        logD("LiveWallpaperManager.UnloadUnity");
        unityPlayer.unload();
    }

    public synchronized void connectUnityDisplay(SurfaceHolder holder)
    {
        logD("connectUnityDisplay -> " + holder);
        cleanUpHolders(holder);
        int nextIndex = holdersOfUnity.size();
        WeakReference<SurfaceHolder> reference = new WeakReference<>(holder);
        logD(String.format("holdersOfUnity.put(%s, %s)",nextIndex, holder));
        holdersOfUnity.put(nextIndex,reference);
        updateLifeStageUsingBackingCount();
    }

    public synchronized void disconnectUnityDisplay(SurfaceHolder holder)
    {
        logD("disconnectUnityDisplay -> " + holder);
        cleanUpHolders(holder);
        updateLifeStageUsingBackingCount();
    }

    private synchronized void cleanUpHolders()
    {
        cleanUpHolders(null);
    }

    private synchronized void cleanUpHolders(SurfaceHolder holderToRemove)
    {
        List<WeakReference<SurfaceHolder>> surfaceRefs = holdersOfUnity.values()
                .stream()
                .filter(ref -> ref != null && ref.get() != holderToRemove)
                .collect(Collectors.toList());
        holdersOfUnity.clear();
        for (int i = 0; i < surfaceRefs.size(); i++) {
            holdersOfUnity.put(i, surfaceRefs.get(i));
        }
    }

    private synchronized void updateLifeStageUsingBackingCount()
    {
        cleanUpHolders();
        int surfaceHolderCount = holdersOfUnity.size();
        boolean hasHolder = surfaceHolderCount > 0;
        Integer newestSurfaceHolderKey = Math.max(surfaceHolderCount - 1, 0);
        WeakReference<SurfaceHolder> surfaceHolderReference = holdersOfUnity.containsKey(newestSurfaceHolderKey) ? holdersOfUnity.get(newestSurfaceHolderKey) : null;
        SurfaceHolder surfaceHolder = surfaceHolderReference == null ? null : surfaceHolderReference.get();
        Surface surface = surfaceHolder == null ? null : surfaceHolder.getSurface();

        if (hasHolder)
        {
            logD(String.format("calling unityPlayer.displayChanged(%s, %s)",0, surface));
            unityPlayer.displayChanged(0, surface); // let unity cast to that surface with newestDisplayIndex as the Display index in Unity
        }
        logD("unityLifeCycleRunnable run");
        if (hasHolder && unityInstancePaused)
        {
            unityInstancePaused = false;
            logD("calling unityPlayer.resume");
            unityPlayer.resume();
            unityPlayer.windowFocusChanged(true);
        }else if (!hasHolder && !unityInstancePaused)
        {
            unityInstancePaused = true;
            logD("calling unityPlayer.pause");
            unityPlayer.pause();
        }
    }

    @Override public void onUnityPlayerUnloaded() {
        logD("onUnityPlayerUnloaded");
    }

    @Override public void onUnityPlayerQuitted() {
        logD("onUnityPlayerQuitted");
    }

    public void openLiveWallpaperPreview(String serviceClassName)
    {
        try {
            openLiveWallpaperPreview(Class.forName(serviceClassName));
        } catch (ClassNotFoundException e) {
            logE(e.toString());
            e.printStackTrace();
        }
    }

    public void openLiveWallpaperPreview(Class serviceClass)
    {
        Context context = getContext();
        Intent intent;

        try {
            intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(context, serviceClass));
            context.startActivity(intent);
            return;
        }
        catch (Exception e)
        {
            logE(e.toString());
        }

        try {
            intent = new Intent(Intent.ACTION_SET_WALLPAPER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        }
        catch (Exception e)
        {
            logE(e.toString());
        }
    }

    public void openLiveWallpaperPreview()
    {
        openLiveWallpaperPreview(defaultService);
    }

    public void launchUnityActivity()
    {
        launchUnityActivity(defaultActivity);
    }
    public void launchUnityActivity(Class activityClass)
    {
        Intent intent = new Intent(this.getContext(), activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.getContext().startActivity(intent);
    }
    public void launchUnityActivity(String activityName)
    {
        try {
            launchUnityActivity(Class.forName(activityName));
        } catch (ClassNotFoundException e) {
            logE(e.toString());
            e.printStackTrace();
        }
    }

    public void launchUnityService()
    {
        launchUnityService(defaultService);
    }
    public void launchUnityService(Class serviceClass)
    {
        getContext().startService(new Intent(getContext(), serviceClass));
    }
}
