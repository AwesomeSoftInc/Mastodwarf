package net.ioixd.spigotmc.plugin.commands.event;

import net.ioixd.spigotmc.plugin.commands.MastodwarfClient;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.logging.Logger;

public class MovementListener implements Listener {

    private final Logger logger;

    public MovementListener(MastodwarfClient plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.logger = plugin.getLogger();
    }

    @EventHandler
    public void movement(PlayerMoveEvent event) {

    }
}
