package net.ioixd.spigotmc.plugin.commands;

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
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class MastodwarfCommand implements CommandExecutor {

    Pattern html = Pattern.compile("<(.*?)>");

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
        long id;
        String content;
        Account account;

    }

    class Account {
        String username;
        String display_name;
    }

    public ItemStack newBook(String arg) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        meta.setTitle("Mastodwarf");
        meta.setAuthor("god");

        try {
            URL url = new URL("https://wetdry.world/api/v1/timelines/public?local=true");
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

            int advance = (ChatColor.BOLD.toString()+ChatColor.RESET.toString()+
                    ChatColor.ITALIC.toString()+ChatColor.GRAY.toString()+
                    ChatColor.RESET).length();

            int offset = 0;
            StringWriter writer = new StringWriter();
            for(int i = 0; i < statuses.length; i++) {
                Status status = statuses[i];

                if(status.content.compareTo("") != 0) {
                    writer.write(ChatColor.BOLD+status.account.display_name+" "+ChatColor.RESET
                            +ChatColor.ITALIC+ChatColor.GRAY+"(@"+status.account.username+"):"+ChatColor.RESET
                            +"\n");
                    offset += advance;

                    String content = (status.content).replaceAll("</p>","\n");
                    content = (content).replaceAll("<br>","\n");
                    content = html.matcher(content).replaceAll("");


                    writer.write(content+"\n\n");
                }

                StringBuffer buf = writer.getBuffer();
                int len = buf.length();
                final int MAX = 150;
                if(len > MAX) {
                    meta.addPage(buf.toString());
                    String remaining = buf.substring(MAX-offset, len);
                    buf.setLength(0);
                    writer.write(remaining);
                    offset = 0;
                }
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
