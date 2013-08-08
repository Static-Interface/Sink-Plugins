package de.static_interface.commandsplugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TradechatListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        if (event.getMessage().startsWith("$"))
        {
            String message = event.getMessage().replaceFirst("(.*)$(.*)", "");
            String formattedMessage = event.getFormat().replace("%1$s", event.getPlayer().getDisplayName());
            formattedMessage = formattedMessage.replace("%2$s", message);
            Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Handel" + ChatColor.GRAY + "] " + formattedMessage);
            event.setCancelled(true);
        }
    }
}