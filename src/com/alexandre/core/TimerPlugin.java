package com.alexandre.core;

import com.alexandre.core.timers.TimerApiImpl;

public class TimerPlugin {
    private static TimerApiImpl timerApi;

    public static void init() {
        timerApi = new TimerApiImpl(Main.getInstance());
    }

    public static TimerApiImpl getTimerApi() {
        return TimerPlugin.timerApi;
    }
}
