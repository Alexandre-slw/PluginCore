package com.alexandre.core.players;

import org.bukkit.entity.Player;

import java.util.concurrent.CopyOnWriteArrayList;

public class GamePlayersRegistry {

    private static GamePlayerModifier modifier = new GamePlayerModifier();
    private static final CopyOnWriteArrayList<GamePlayer> players = new CopyOnWriteArrayList<>();

    public static void addPlayer(GamePlayer player) {
        GamePlayersRegistry.getPlayers().add(GamePlayersRegistry.getModifier().onAddPlayer(player));
    }

    public static void removePlayer(GamePlayer player) {
        GamePlayersRegistry.getModifier().onRemovePlayer(player);
        GamePlayersRegistry.getPlayers().remove(player);
    }

    public static GamePlayer getPlayer(Player player) {
        return GamePlayersRegistry.getPlayers().stream().filter(gp -> gp.getPlayer().equals(player)).findAny().orElse(null);
    }

    public static CopyOnWriteArrayList<GamePlayer> getPlayers() {
        return GamePlayersRegistry.players;
    }

    public static void setModifier(GamePlayerModifier modifier) {
        GamePlayersRegistry.modifier = modifier;
    }

    public static GamePlayerModifier getModifier() {
        return GamePlayersRegistry.modifier;
    }
}
