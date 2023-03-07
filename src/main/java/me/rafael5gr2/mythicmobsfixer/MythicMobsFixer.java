package me.rafael5gr2.mythicmobsfixer;

import me.rafael5gr2.mythicmobsfixer.listeners.EntityDeathListeners;

import org.bukkit.plugin.java.JavaPlugin;

public class MythicMobsFixer extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EntityDeathListeners(this), this);
    }
}
