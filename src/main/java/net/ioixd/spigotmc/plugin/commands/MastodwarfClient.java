package net.ioixd.spigotmc.plugin.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MastodwarfClient extends JavaPlugin {
    public static String instance;

    public void main() {
        // dummy
    }

    @Override
    public void onEnable() {
        FileConfiguration config = this.getConfig();
        config.addDefault("instance", "https://wetdry.world");
        this.saveDefaultConfig();
        instance = config.getString("instance");


        this.getCommand("mastodwarf").setExecutor(new MastodwarfCommand());
    }

    public void log(String msg) {
        getLogger().info(msg);
    }
}