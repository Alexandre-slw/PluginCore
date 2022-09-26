package com.alexandre.core.event;

import com.alexandre.core.players.GamePlayer;
import com.alexandre.core.players.GamePlayersRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventJoin implements Listener {
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        GamePlayersRegistry.addPlayer(new GamePlayer(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GamePlayer player = GamePlayersRegistry.getPlayer(event.getPlayer());
        if (player == null) return;
        GamePlayersRegistry.removePlayer(player);
    }

}
