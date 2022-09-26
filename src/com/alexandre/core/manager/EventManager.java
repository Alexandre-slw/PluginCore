package com.alexandre.core.manager;

import com.alexandre.core.Main;
import com.alexandre.core.event.EventJoin;
import com.alexandre.core.event.TimerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class EventManager {

    public void registers() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new EventJoin(), Main.getInstance());
        pm.registerEvents(new TimerListener(), Main.getInstance());
    }

}
