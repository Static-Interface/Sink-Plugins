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

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.Aliases;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.exception.NotEnoughPermissionsException;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

@DefaultPermission
@Aliases({"clear", "invclear", "ci"})
@Description("Clear your or an other players inventory")
@Usage("[options] [player]")
public class ClearCommand extends SinkCommand {
    public static final String PREFIX = ChatColor.RED + "[Clear] " + ChatColor.RESET;

    public ClearCommand(Plugin plugin, Configuration config) {
        super(plugin, config);
        getCommandOptions().setPlayerOnly(true);
        getCommandOptions().setCliOptions(buildOptions());
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

        Option armor = Option.builder("r")
                .longOpt("armor")
                .desc("Clear armor")
                .build();

        Option xp = Option.builder("x")
                .longOpt("xp")
                .desc("Clear XP")
                .build();

        Option all = Option.builder("a")
                .longOpt("all")
                .desc("Clear everything")
                .build();

        options.addOption(inventory);
        options.addOption(effects);
        options.addOption(armor);
        options.addOption(xp);
        options.addOption(all);
        return options;
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException {
        Player player = (Player) sender;

        IngameUser user = (IngameUser) SinkLibrary.getInstance().getUser(sender);
        String targetName = StringUtil.formatArrayToString(getCommandLine().getArgs(), " ").trim();
        if (!targetName.equalsIgnoreCase("")) {
            if (!user.hasPermission("sinkcommands.commands.clear.others")) {
                throw new NotEnoughPermissionsException("sinkcommands.commands.clear.others");
            }

            player = Bukkit.getPlayer(targetName);

            if (player == null) {
                throw new UserNotFoundException(args[0]);
            }

            if (sender instanceof Player) {
                if (!((Player) sender).canSee(player) && !sender.hasPermission("sinklibrary.bypassvanish")) {
                    throw new UserNotFoundException(args[0]);
                }
            }
        }

        boolean clearInventory;
        boolean clearEffects;
        boolean clearArmor;
        boolean clearXp;

        clearInventory = getCommandLine().hasOption('i');
        clearEffects = getCommandLine().hasOption('e');
        clearArmor = getCommandLine().hasOption('r');
        clearXp = getCommandLine().hasOption('x');

        if (getCommandLine().hasOption('a')) {
            //invert other options, for queries like "clear everything except these options"
            if (clearInventory) {
                clearInventory = false;
            }
            if (clearEffects) {
                clearEffects = false;
            }
            if (clearArmor) {
                clearArmor = false;
            }
            if (clearXp) {
                clearXp = false;
            }
        } else if (!clearInventory && !clearArmor && !clearEffects && !clearXp) {
            //default options for /clear without any args
            clearInventory = true;
            clearArmor = true;
        }

        if (clearInventory) {
            player.getInventory().clear();
        }

        if (clearEffects) {
            for (PotionEffectType effect : PotionEffectType.values()) {
                player.removePotionEffect(effect);
            }
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
            sender.sendMessage(PREFIX + ChatColor.RED + player.getDisplayName() + " wurde gecleart.");
        }
        player.sendMessage(PREFIX + ChatColor.RED + "Du wurdest gecleart.");
        return true;
    }
}