package net.ioixd.spigotmc.plugin.commands;

import org.bukkit.plugin.java.JavaPlugin;

public class MastodwarfClient extends JavaPlugin {

    public void main() {
        // dummy
    }

    @Override
    public void onEnable() {
        this.getCommand("mastodwarf").setExecutor(new MastodwarfCommand());
        getLogger().info("Added the 'mastodwarf' command.");
    }

    public void log(String msg) {
        getLogger().info(msg);
    }
}