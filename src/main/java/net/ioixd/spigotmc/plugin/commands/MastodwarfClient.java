package net.ioixd.spigotmc.plugin.commands;

import org.bukkit.plugin.java.JavaPlugin;

public class MastodwarfClient extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("mastodwarf").setExecutor(new MastodwarfCommand());
        getLogger().info("Added the 'mastodwarf' command.");
    }
}