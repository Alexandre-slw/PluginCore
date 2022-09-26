package com.alexandre.core.utils;

import com.alexandre.core.Main;
import com.alexandre.core.players.GamePlayer;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BungeeCordUtils {

    public enum GameType { AFK, LOBBY, BEDWARS_SOLO }

    public static void joinGame(GameType game, GamePlayer player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            out.writeUTF("JoinGame");
            out.writeUTF(game.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }

        player.getPlayer().sendPluginMessage(Main.getInstance(), "CoreServer", out.toByteArray());
    }

    public static void setCanAutoJoin(boolean canJoin) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            out.writeUTF("CanJoin");
            out.writeBoolean(canJoin);
        } catch(Exception e) {
            e.printStackTrace();
        }

        Player messageSender = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (messageSender == null) return;
        messageSender.sendPluginMessage(Main.getInstance(), "CoreServer", out.toByteArray());
    }

    public static void sendCloseRequest() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            out.writeUTF("Close");
        } catch(Exception e) {
            e.printStackTrace();
        }

        Player messageSender = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (messageSender == null) return;
        messageSender.sendPluginMessage(Main.getInstance(), "CoreServer", out.toByteArray());
    }

}
