package net.ioixd.spigotmc.plugin.commands;

import java.io.StringWriter;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.Pageable;
import com.sys1yagi.mastodon4j.api.Range;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Timelines;

import okhttp3.OkHttpClient;



public class MastodwarfCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        ItemStack book = this.newBook("timeline");

        Player p = Bukkit.getServer().getPlayer(sender.getName());
        int slot = p.getInventory().getHeldItemSlot();
        ItemStack old = p.getInventory().getItem(slot);
        p.getInventory().setItem(slot, book);

        try {
             PacketContainer pc = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
             pc.getModifier().writeDefaults();
             pc.getStrings().write(0, "MC|BOpen");
             ProtocolLibrary.getProtocolManager().sendServerPacket(p, pc);
        } catch (Exception e) {
             e.printStackTrace();
             return false;
        }

        p.getInventory().setItem(slot, old);
        return true;
    }

    public ItemStack newBook(String arg) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        MastodonClient client = new MastodonClient.Builder("mstdn.jp", new OkHttpClient.Builder(), new Gson()).build();
        Timelines timelines = new Timelines(client);

        try {
            StringWriter writer = new StringWriter();
            Pageable<Status> statuspages = timelines.getHome(new Range()).execute();
            List<Status> statuses = statuspages.getPart();

            statuses.forEach(status->{
                writer.write("=============");
                writer.write(status.getAccount().getDisplayName());
                writer.write(status.getContent());
                if(writer.getBuffer().length() >= 256) {
                    meta.addPage(writer.getBuffer().toString());
                    writer.flush();
                }
            });
          } catch (Exception e) {
            e.printStackTrace();
          }

        return book;
    }
}
