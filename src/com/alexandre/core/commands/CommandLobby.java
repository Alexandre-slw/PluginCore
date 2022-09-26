package com.alexandre.core.commands;

import com.alexandre.core.players.GamePlayersRegistry;
import com.alexandre.core.utils.BungeeCordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLobby implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;

        BungeeCordUtils.joinGame(BungeeCordUtils.GameType.LOBBY, GamePlayersRegistry.getPlayer((Player) commandSender));

        return true;
    }

}
