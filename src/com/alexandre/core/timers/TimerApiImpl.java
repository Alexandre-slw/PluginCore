package com.alexandre.core.timers;

import com.alexandre.core.utils.NmsManager;
import com.alexandre.core.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TimerApiImpl extends TimerApi {
    private final Main plugin;
    private final AtomicInteger idGenerator;
    private final Set<TimerImpl> allTimers;

    public TimerApiImpl(final Main plugin) {
        this.plugin = plugin;
        this.idGenerator = new AtomicInteger(1);
        this.allTimers = Collections.newSetFromMap(new ConcurrentHashMap<>());
        TimerApi.instance = this;
    }

    @Override
    public Timer createTickTimer(final ItemStack item, final boolean repeating, final long time) {
        return this.createTickTimer(null, item, repeating, time);
    }

    @Override
    public Timer createTickTimer(final String name, final ItemStack item, final boolean repeating, final long time) {
        final TimerImpl timer = new TimerImpl(this.plugin, this.idGenerator.getAndIncrement(), name, item, repeating, time);
        this.allTimers.add(timer);
        return timer;
    }

    @Override
    public Timer createTimeTimer(final ItemStack item, final boolean repeating, final long time, final TimeUnit timeUnit) {
        return this.createTimeTimer(null, item, repeating, time, timeUnit);
    }

    @Override
    public Timer createTimeTimer(final String name, final ItemStack item, final boolean repeating, final long time, final TimeUnit timeUnit) {
        final TimerImpl timer = new TimerImpl(this.plugin, this.idGenerator.getAndIncrement(), name, item, repeating, time, timeUnit);
        this.allTimers.add(timer);
        return timer;
    }

    @Override
    public void removeTimer(final Timer timer) {
        if (timer instanceof TimerImpl) {
            final TimerImpl timerImpl = (TimerImpl) timer;
            this.allTimers.remove(timerImpl);
        }
        timer.clearReceivers();
    }

    @Override
    public void clearTimers(final Player player) {
        for (final TimerImpl timer : this.allTimers) {
            timer.getReceivers().remove(player);
        }
        NmsManager.sendPluginMessage(player, "badlion:timers", "REMOVE_ALL_TIMERS|{}".getBytes(StandardCharsets.UTF_8));
    }

    public void tickTimers() {
        for (final TimerImpl timer : this.allTimers) {
            timer.tick();
        }
    }

    public void syncTimers() {
        for (final TimerImpl timer : this.allTimers) {
            timer.syncTimer();
        }
    }
}
