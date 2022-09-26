package com.alexandre.core;

import com.alexandre.core.api.inventory.FastInvManager;
import com.alexandre.core.commands.CommandLobby;
import com.alexandre.core.manager.EventManager;
import com.alexandre.core.utils.NmsManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        Main.instance = this;

        NmsManager.init(this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "CoreServer");

        new EventManager().registers();
        FastInvManager.register(this);

        TimerPlugin.init();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "badlion:timers");
        this.getServer().getScheduler().runTaskTimer(this, () -> TimerPlugin.getTimerApi().tickTimers(), 1L, 1L);
        this.getServer().getScheduler().runTaskTimer(this, () -> TimerPlugin.getTimerApi().syncTimers(), 60L, 60L);

        this.getServer().getPluginCommand("lobby").setExecutor(new CommandLobby());
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    public static Main getInstance() {
        return Main.instance;
    }

}
