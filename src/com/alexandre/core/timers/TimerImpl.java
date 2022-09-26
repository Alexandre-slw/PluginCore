package com.alexandre.core.timers;

import com.alexandre.core.utils.NmsManager;
import com.google.gson.*;
import com.alexandre.core.Main;
import com.alexandre.core.TimerPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimerImpl implements Timer {
    private static final Gson GSON;
    private final Main plugin;
    private final long id;
    private final RemoveReceiverRequest removeReceiverRequest;
    private String name;
    private ItemStack item;
    private boolean repeating;
    private long time;
    private long millis;
    private AtomicBoolean updated;
    private Set<Player> receivers;
    private long currentTime;
    private long lastTick;

    public TimerImpl(final Main plugin, final long id, final String name, final ItemStack item, final boolean repeating, final long time) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.item = item;
        this.repeating = repeating;
        this.time = time;
        this.millis = -1L;
        this.currentTime = time;
        this.lastTick = System.currentTimeMillis();
        this.updated = new AtomicBoolean(false);
        this.receivers = Collections.newSetFromMap(new ConcurrentHashMap<>());
        (this.removeReceiverRequest = new RemoveReceiverRequest()).id = id;
    }

    public TimerImpl(final Main plugin, final int id, final String name, final ItemStack item, final boolean repeating, final long time, final TimeUnit timeUnit) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.item = item;
        this.repeating = repeating;
        this.time = -1L;
        this.millis = timeUnit.toMillis(time);
        this.currentTime = this.millis;
        this.lastTick = System.currentTimeMillis();
        this.updated = new AtomicBoolean(false);
        this.receivers = Collections.newSetFromMap(new ConcurrentHashMap<>());
        (this.removeReceiverRequest = new RemoveReceiverRequest()).id = id;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        final String old = this.name;
        this.name = name;
        if (!old.equals(name)) {
            this.updated.set(true);
        }
    }

    @Override
    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public void setItem(final ItemStack item) {
        final ItemStack old = this.item;
        this.item = item;
        if (old != item) {
            this.updated.set(true);
        }
    }

    @Override
    public boolean isRepeating() {
        return this.repeating;
    }

    @Override
    public void setRepeating(final boolean repeating) {
        final boolean old = this.repeating;
        this.repeating = repeating;
        if (old != repeating) {
            this.updated.set(true);
        }
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public void setTime(final long time) {
        this.time = time;
        this.millis = -1L;
        this.updated.set(true);
        this.reset();
    }

    @Override
    public long getMillis() {
        return this.millis;
    }

    @Override
    public void setTime(final long time, final TimeUnit timeUnit) {
        this.time = -1L;
        this.millis = timeUnit.toMillis(time);
        this.updated.set(true);
        this.reset();
    }

    @Override
    public void addReceiver(final Player player) {
        if (this.receivers.add(player)) {
            this.send(player, "ADD_TIMER", this);
        }
    }

    @Override
    public void removeReceiver(final Player player) {
        if (this.receivers.remove(player)) {
            this.send(player, "REMOVE_TIMER", this.removeReceiverRequest);
        }
    }

    @Override
    public void clearReceivers() {
        this.send(this.receivers, "REMOVE_TIMER", this.removeReceiverRequest);
        this.receivers.clear();
    }

    @Override
    public Collection<Player> getReceivers() {
        return this.receivers;
    }

    @Override
    public void reset() {
        this.currentTime = ((this.time != -1L) ? this.time : this.millis);
        this.syncTimer();
    }

    public void tick() {
        final long currentMillis = System.currentTimeMillis();
        if (this.time != -1L) {
            final long currentTime = this.currentTime - 1L;
            this.currentTime = currentTime;
            if (currentTime <= 0L) {
                if (!this.repeating) {
                    TimerPlugin.getTimerApi().removeTimer(this);
                    return;
                }
                this.currentTime = this.time;
            }
        } else {
            final long diff = currentMillis - this.lastTick;
            final long currentTime2 = this.currentTime - diff;
            this.currentTime = currentTime2;
            if (currentTime2 <= 0L) {
                if (!this.repeating) {
                    TimerPlugin.getTimerApi().removeTimer(this);
                    return;
                }
                this.currentTime += this.millis;
            }
        }
        this.lastTick = currentMillis;
        if (this.updated.compareAndSet(true, false)) {
            this.send(this.receivers, "UPDATE_TIMER", this);
        }
    }

    public void syncTimer() {
        final SyncTimerRequest syncTimerRequest = new SyncTimerRequest();
        syncTimerRequest.id = this.id;
        syncTimerRequest.time = this.currentTime;
        this.send(this.receivers, "SYNC_TIMERS", syncTimerRequest);
    }

    private <T> void send(final Player player, final String requestName, final T request) {
        NmsManager.sendPluginMessage(player, "badlion:timers", (requestName + "|" + TimerImpl.GSON.toJson(request)).getBytes(StandardCharsets.UTF_8));
    }

    private <T> void send(final Collection<Player> players, final String requestName, final T request) {
        final byte[] data = (requestName + "|" + TimerImpl.GSON.toJson(request)).getBytes(StandardCharsets.UTF_8);
        for (final Player player : players) {
            NmsManager.sendPluginMessage(player, "badlion:timers", data);
        }
    }

    static {
        GSON = new GsonBuilder().registerTypeAdapter(TimerImpl.class, new TimerSerializer()).create();
    }

    private static class TimerSerializer implements JsonSerializer<TimerImpl> {
        public JsonElement serialize(final TimerImpl timer, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.add("id", new JsonPrimitive(timer.id));
            jsonObject.add("name", ((timer.name == null) ? JsonNull.INSTANCE : new JsonPrimitive(timer.name)));
            jsonObject.add("item", TimerImpl.GSON.toJsonTree(timer.item.serialize()));
            jsonObject.add("repeating", new JsonPrimitive(timer.repeating));
            jsonObject.add("time", new JsonPrimitive(timer.time));
            jsonObject.add("millis", new JsonPrimitive(timer.millis));
            jsonObject.add("currentTime", new JsonPrimitive(timer.currentTime));
            return jsonObject;
        }
    }

    private static class RemoveReceiverRequest {
        private long id;
    }

    private static class SyncTimerRequest {
        private long id;
        private long time;
    }
}
