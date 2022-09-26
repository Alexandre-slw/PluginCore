package com.alexandre.core.timers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public abstract class TimerApi {
    static TimerApi instance;

    public static TimerApi getInstance() {
        return TimerApi.instance;
    }

    public abstract Timer createTickTimer(final ItemStack p0, final boolean p1, final long p2);

    public abstract Timer createTickTimer(final String p0, final ItemStack p1, final boolean p2, final long p3);

    public abstract Timer createTimeTimer(final ItemStack p0, final boolean p1, final long p2, final TimeUnit p3);

    public abstract Timer createTimeTimer(final String p0, final ItemStack p1, final boolean p2, final long p3, final TimeUnit p4);

    public abstract void removeTimer(final Timer p0);

    public abstract void clearTimers(final Player p0);
}
