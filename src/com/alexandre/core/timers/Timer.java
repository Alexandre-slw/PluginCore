package com.alexandre.core.timers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface Timer {
    long getId();

    String getName();

    void setName(final String p0);

    ItemStack getItem();

    void setItem(final ItemStack p0);

    boolean isRepeating();

    void setRepeating(final boolean p0);

    long getTime();

    void setTime(final long p0);

    long getMillis();

    void setTime(final long p0, final TimeUnit p1);

    void addReceiver(final Player p0);

    void removeReceiver(final Player p0);

    void clearReceivers();

    Collection<Player> getReceivers();

    void reset();
}