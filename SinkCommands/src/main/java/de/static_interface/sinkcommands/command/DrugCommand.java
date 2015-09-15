/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkcommands.command;

import static de.static_interface.sinklibrary.Constants.TICK;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DefaultPermission
@Description("Take drugs!")
public class DrugCommand extends SinkCommand {

    public static final String PREFIX = ChatColor.AQUA + "[Drogen] " + ChatColor.RESET;

    public static Player lastPlayer;

    public DrugCommand(Plugin plugin) {
        super(plugin);
        getCommandOptions().setPlayerOnly(true);
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) {
        IngameUser user = (IngameUser) SinkLibrary.getInstance().getUser(sender);

        Player player = user.getPlayer();

        if (!player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
            player.sendMessage(PREFIX + ChatColor.BLUE + "Du hast Drogen genommen...");
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 1), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 20, 1), true);
        } else {
            for (PotionEffect pe : player.getActivePotionEffects()) {
                if (!pe.getType().equals(PotionEffectType.BLINDNESS)) {
                    continue;
                }
                int duration = pe.getDuration();
                int amplifier = pe.getAmplifier();
                if (amplifier == 1) {
                    player.sendMessage(PREFIX + ChatColor.BLUE + "Du hast mehr Drogen genommen...");
                } else if (amplifier == 2) {
                    player.sendMessage(PREFIX + ChatColor.BLUE + "Vielleicht solltest du nicht so viele Drogen nehmen.");
                } else if (amplifier == 3) {
                    player.sendMessage(PREFIX + ChatColor.BLUE + "Noch mehr Drogen...");
                } else if (amplifier == 4) {
                    player.sendMessage(PREFIX + ChatColor.BLUE + "Wow, du hast so viele Drogen genommen... und lebst immer noch!");
                } else if (amplifier >= 5) {
                    lastPlayer = player;
                    player.setHealth(0.0);
                    return true;
                }
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.removePotionEffect(PotionEffectType.CONFUSION);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) (duration + (TICK * 30)), amplifier + 1), true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int) (duration + (TICK * 30)), amplifier + 1), true);
            }
        }
        return true;
    }
}
