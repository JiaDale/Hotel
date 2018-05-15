package com.jdy.router;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

public class Router {
    private HashMap<String, Object> services = new HashMap<>();
    //注册的组件的集合
    private static HashMap<String, IApplicationLike> components = new HashMap<>();

    private static volatile Router instance;

    private Router() {
    }

    public static Router getInstance() {
        if (instance == null) {
            synchronized (Router.class) {
                if (instance == null) {
                    instance = new Router();
                }
            }
        }
        return instance;
    }

    public synchronized void addService(String serviceName, Object serviceImpl) {
        if (serviceName == null || serviceImpl == null) {
            return;
        }
        services.put(serviceName, serviceImpl);
    }

    public synchronized Object getService(String serviceName) {
        if (serviceName == null) {
            return null;
        }
        return services.get(serviceName);
    }

    public synchronized void removeService(String serviceName) {
        if (serviceName == null) {
            return;
        }
        services.remove(serviceName);
    }

    public static void registerComponent(@Nullable String classname) {
        if (TextUtils.isEmpty(classname)) {
            return;
        }
        if (components.keySet().contains(classname)) {
            return;
        }
        updateComponents(classname, true);
    }

    public static void unregisterComponent(@Nullable String classname) {
        if (TextUtils.isEmpty(classname)) {
            return;
        }
        if (components.keySet().contains(classname)) {
            components.get(classname).onStop();
            components.remove(classname);
            return;
        }
        updateComponents(classname, false);
    }

    private static void updateComponents(@Nullable String classname, boolean isCreate) {
        try {
            Class clazz = Class.forName(classname);
            IApplicationLike applicationLike = (IApplicationLike) clazz.newInstance();
            if (isCreate){
                applicationLike.onCreate();
                components.put(classname, applicationLike);
            } else {
                applicationLike.onStop();
                components.remove(classname);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
