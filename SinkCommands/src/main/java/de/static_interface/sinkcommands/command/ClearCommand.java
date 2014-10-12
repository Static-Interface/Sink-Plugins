/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkcommands.command;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class ClearCommand extends SinkCommand {
    //Todo: convert to command

    public static final String PREFIX = ChatColor.RED + "[Clear] " + ChatColor.RESET;

    public ClearCommand(Plugin plugin) {
        super(plugin);
        getCommandOptions().setPlayerOnly(true);
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] argsArr) {
        List<String> args = Arrays.asList(argsArr);

        Player player;
        IngameUser user = (IngameUser) SinkLibrary.getInstance().getUser(sender);

        boolean clearInvetory = false;
        boolean clearEffects = false;
        boolean clearArmor = false;
        int i = 0;
        if (args.contains("-i") || args.contains("-inventory")) {
            clearInvetory = true;
            i++;
        }
        if (args.contains("-e") || args.contains("-effects")) {
            clearEffects = true;
            i++;
        }
        if (args.contains("-ar") || args.contains("-armor")) {
            clearArmor = true;
            i++;
        }
        if (args.contains("-a") || args.contains("-all")) {
            clearInvetory = true;
            clearEffects = true;
            clearArmor = true;
            i = 3;
        }
        if (!clearInvetory && !clearEffects && !clearArmor) {
            clearInvetory = true;
            i = 1;
        }

        if (argsArr.length >= 1) {
            String name = argsArr[i];
            if (!user.hasPermission("sinkcommands.clear.others")) {
                sender.sendMessage(PREFIX + "Du hast nicht genügend Rechte um das Inventar von anderen Spielern zu leeren!");
                return true;
            }
            player = BukkitUtil.getPlayer(name);
            if (player == null) {
                sender.sendMessage(PREFIX + "Spieler wurde nicht gefunden: " + name);
                return true;
            }
        }

        player = user.getPlayer();

        if (clearInvetory) {
            player.getInventory().clear();
        }

        if (clearEffects) {
            player.removePotionEffect(PotionEffectType.ABSORPTION);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.CONFUSION);
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            player.removePotionEffect(PotionEffectType.HARM);
            player.removePotionEffect(PotionEffectType.HEAL);
            player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
            player.removePotionEffect(PotionEffectType.HUNGER);
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.JUMP);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.removePotionEffect(PotionEffectType.POISON);
            player.removePotionEffect(PotionEffectType.REGENERATION);
            player.removePotionEffect(PotionEffectType.SATURATION);
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
            player.removePotionEffect(PotionEffectType.SPEED);
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            player.removePotionEffect(PotionEffectType.WITHER);
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }
        if (clearArmor) {
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
        }
        if (!player.equals(sender)) {
            sender.sendMessage(PREFIX + player.getDisplayName() + " wurde gecleart.");
        }
        player.sendMessage(PREFIX + ChatColor.RED + "Du wurdest gecleart.");
        return true;
    }

}