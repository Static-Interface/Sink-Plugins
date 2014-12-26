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

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.*;

import de.static_interface.sinklibrary.*;
import de.static_interface.sinklibrary.api.command.*;
import de.static_interface.sinklibrary.user.*;
import org.apache.commons.cli.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import org.bukkit.potion.*;

public class ClearCommand extends SinkCommand {
    //Todo: convert to command

    public static final String PREFIX = ChatColor.RED + "[Clear] " + ChatColor.RESET;

    public ClearCommand(Plugin plugin) {
        super(plugin);
        getCommandOptions().setPlayerOnly(true);
        getCommandOptions().setCliOptions(buildOptions());
        getCommandOptions().setCmdLineSyntax("{PREFIX}{ALIAS} <options>");
    }

    private Options buildOptions() {
        Options options = new Options();
        Option inventory = Option.builder("i")
                .desc("Remove inventory")
                .longOpt("inventory")
                .build();

        Option effects = Option.builder("e")
                .longOpt("effects")
                .desc("Clear effects")
                .build();

        Option armor = Option.builder("a")
                .longOpt("armor")
                .desc("Clear armor")
                .build();

        Option xp = Option.builder("x")
                .longOpt("xp")
                .desc("Clear XP")
                .build();

        Option all = Option.builder("A")
                .longOpt("all")
                .desc("Clear everything")
                .build();

        Option target = Option.builder("t")
                .longOpt("target")
                .desc("Target player")
                .hasArgs()
                .argName("player")
                .type(String.class)
                .build();

        options.addOption(inventory);
        options.addOption(effects);
        options.addOption(armor);
        options.addOption(xp);
        options.addOption(all);
        options.addOption(target);

        return options;
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] argsArr) throws ParseException {
        Player player = (Player) sender;

        IngameUser user = (IngameUser) SinkLibrary.getInstance().getUser(sender);

        if (getCommandLine().hasOption('t')) {
            if (!user.hasPermission("sinkcommands.clear.others")) {
                sender.sendMessage(PREFIX + m("Permissions.General"));
                return true;
            }

            String targetName = getCommandLine().getParsedOptionValue("t").toString();

            player = Bukkit.getPlayer(targetName);

            if (player == null) {
                sender.sendMessage(PREFIX + m("General.NotOnline", targetName));
                return true;
            }
        }

        boolean clearInventory;
        boolean clearEffects;
        boolean clearArmor;
        boolean clearXp;

        if (getCommandLine().hasOption('A')) {
            clearInventory = true;
            clearEffects = true;
            clearArmor = true;
            clearXp = true;
        } else {
            clearInventory = getCommandLine().hasOption('i');
            clearEffects = getCommandLine().hasOption('e');
            clearArmor = getCommandLine().hasOption('a');
            clearXp = getCommandLine().hasOption('x');
        }

        if (clearInventory) {
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

        if (clearXp) {
            player.setTotalExperience(0);
        }

        if (!player.equals(sender)) {
            sender.sendMessage(PREFIX + player.getDisplayName() + " wurde gecleart.");
        }
        player.sendMessage(PREFIX + ChatColor.RED + "Du wurdest gecleart.");
        return true;
    }

}