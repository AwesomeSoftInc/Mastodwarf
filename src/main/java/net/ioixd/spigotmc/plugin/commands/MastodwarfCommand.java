package net.ioixd.spigotmc.plugin.commands;

import net.ioixd.spigotmc.plugin.commands.MastodwarfClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class MastodwarfCommand implements CommandExecutor {

    Pattern html = Pattern.compile("<(.*?)>");
    Pattern emote = Pattern.compile(":(.*?):");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {

        ItemStack book = this.newBook("timeline");
        Player p = Bukkit.getServer().getPlayer(sender.getName());

        int slot = p.getInventory().getHeldItemSlot();
        ItemStack old = p.getInventory().getItem(slot);
        p.getInventory().setItem(slot, book);
        p.openBook(book);
        p.getInventory().setItem(slot, old);
        return true;
    }

    class Status{
        String content;
        Account account;
        Media media;
    }

    class Account {
        String username;
        String display_name;
    }

    class Media {
        Attachment[] attachments;
    }

    class Attachment {
        String url;
    }

    public ItemStack newBook(String arg) {
        String instance = MastodwarfClient.instance;

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        meta.setTitle("Mastodwarf");
        meta.setAuthor("god");

        try {
            URL url = new URL(instance+"/api/v1/timelines/public?local=true");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int response = conn.getResponseCode();
            String data;
            if(response == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                data = br.lines().collect(Collectors.joining());
            } else {
                data = conn.getResponseMessage();
            }
            Gson gson = new Gson();
            Status[] statuses = gson.fromJson(data, Status[].class);

            int advance = (ChatColor.ITALIC.toString()+ChatColor.GRAY.toString()+
                    ChatColor.RESET+"\n\n").length();

            StringWriter writer = new StringWriter();
            for(int i = 0; i < statuses.length; i++) {
                Status status = statuses[i];

                // for now, skip images with attachments in them.
                // come back to this if i can get custom fonts working ever.

                if(status.media != null) {
                    continue;
                }

                if(status.content.compareTo("") != 0) {
                    String display_name = status.account.display_name;
                    display_name = emote.matcher(display_name).replaceAll("");

                    writer.write(
                            // display name
                            display_name + " "
                            // username
                            +ChatColor.ITALIC+ChatColor.GRAY+"(@"+status.account.username+"):"+ChatColor.RESET
                            +"\n"
                    );

                    // post contents
                    String content = (status.content);
                    content = content.replaceAll("</p>","\n");
                    content = content.replaceAll("<br>","\n");
                    content = content.replaceAll("&#39;","\'");
                    content = emote.matcher(content).replaceAll("");
                    content = html.matcher(content).replaceAll("");

                    writer.write(content);
                }

                StringBuffer buf = writer.getBuffer();
                int len = buf.length();

                final int MAX = 250;
                String remaining = "";
                if(len > MAX) {
                    remaining = buf.substring(MAX, len);
                }

                meta.addPage(buf.toString());
                buf.setLength(0);
                writer.write(remaining);
            }
            writer.close();
        } catch (Exception e) {
            String msg = e.getMessage();
            if(e.getMessage().length() != 0) {
                meta.addPage("Error: "+msg);
            } else {
                meta.addPage("Error: fucking none the code gave us None. I Love Java.");
            }
            e.printStackTrace();
        }
        book.setItemMeta(meta);
        return book;
    }
}
