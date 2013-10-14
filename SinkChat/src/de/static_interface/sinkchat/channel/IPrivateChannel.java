package de.static_interface.sinkchat.channel;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface IPrivateChannel {

    /**
     * Add player to existing conversation
     * @param invitor
     * Player who invited target
     * @param target
     * Player who was invited
     */

    public void addPlayer(CommandSender invitor, Player target);

    /**
     * Kicks a player off a conversation
     * @param player
     * Player to kick
     * @param kicker
     * Player who kicked
     * @param reason
     * (If given) reason, else standard message.
     */

    public void kickPlayer(Player player, Player kicker, String reason);

    /**
     * Sends message to conversation.
     * @param message
     * Message to send.
     */

    public void sendMessage(String message);

    /**
     * Registers conversation at PrivateChannelHandler
     */

    public void registerConversation();

}