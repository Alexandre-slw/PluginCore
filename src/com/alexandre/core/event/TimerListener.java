package com.alexandre.core.event;

import com.alexandre.core.TimerPlugin;
import com.alexandre.core.utils.NmsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.nio.charset.StandardCharsets;

public class TimerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent event) {
        NmsManager.sendPluginMessage(event.getPlayer(), "badlion:timers", "REGISTER|{}".getBytes(StandardCharsets.UTF_8));
        NmsManager.sendPluginMessage(event.getPlayer(), "badlion:timers", "CHANGE_WORLD|{}".getBytes(StandardCharsets.UTF_8));
    }

    @EventHandler
    public void onDisconnect(final PlayerQuitEvent event) {
        TimerPlugin.getTimerApi().clearTimers(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTeleport(final PlayerTeleportEvent event) {
        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            NmsManager.sendPluginMessage(event.getPlayer(), "badlion:timers", "CHANGE_WORLD|{}".getBytes(StandardCharsets.UTF_8));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onRespawn(final PlayerRespawnEvent event) {
        NmsManager.sendPluginMessage(event.getPlayer(), "badlion:timers", "CHANGE_WORLD|{}".getBytes(StandardCharsets.UTF_8));
    }
}