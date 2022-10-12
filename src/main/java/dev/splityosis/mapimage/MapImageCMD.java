package dev.splityosis.mapimage;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.MalformedURLException;
import java.net.URL;

public class MapImageCMD implements CommandExecutor {
    private Main plugin;

    public MapImageCMD(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        if (!sender.hasPermission("mapimage.use")){
            sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
            return false;
        }

        if (args.length == 0){
            sender.sendMessage(ChatColor.RED + "Please provide an image link.");
            return false;
        }

        sender.sendMessage(ChatColor.YELLOW + "Loading...");

        new BukkitRunnable(){
            @Override
            public void run() {
                URL imageURL;
                try {
                    imageURL = new URL(args[0]);
                } catch (MalformedURLException e) {
                    sender.sendMessage(ChatColor.RED + "Couldn't load an image from the URL, Please make sure the link is directly of an image.");
                    return;
                }

                Player player = (Player) sender;
                try {
                    player.getInventory().addItem(MapImageManager.getMapItem(imageURL));
                    player.sendMessage(ChatColor.GREEN + "You received an image.");
                }catch (Exception e){
                    player.sendMessage(ChatColor.RED + "Couldn't load an image from the URL, Please make sure the link is directly of an image.");
                }
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }
}
