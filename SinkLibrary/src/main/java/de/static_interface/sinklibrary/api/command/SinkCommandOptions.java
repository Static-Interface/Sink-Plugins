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

package de.static_interface.sinklibrary.api.command;

import de.static_interface.sinklibrary.api.annotation.Unstable;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.PrintWriter;
import java.io.Writer;

import javax.annotation.Nonnull;

public class SinkCommandOptions {

    private boolean useNotices = false;
    private boolean isPlayerOnly = false;
    private boolean isIrcOnly = false;
    private boolean isIrcOpOnly = false;
    private boolean isIrcQueryOnly = false;
    private boolean defaultHelpEnabled = true;
    private Options cliOptions = null;
    private String cmdLineSyntax = "";
    private HelpFormatter cliHelpFormatter = null;
    private CommandLineParser cliParser = null;
    private SinkCommandBase parentCommand;
    private boolean ircEnabled = true;

    public SinkCommandOptions(SinkCommandBase parentCommand) {
        if (isPlayerOnly && isIrcOnly) {
            throw new IllegalStateException("A command can't be player only and irc only at the same time");
        }

        if (cmdLineSyntax == null) {
            cmdLineSyntax = "";
        }

        this.parentCommand = parentCommand;
    }

    public CommandLineParser getCliParser() {
        if (cliParser == null) {
            cliParser = new DefaultParser();
        }

        return cliParser;
    }

    public void setCliParser(@Nonnull CommandLineParser cliParser) {
        this.cliParser = cliParser;
    }

    public boolean isPlayerOnly() {
        return isPlayerOnly;
    }

    public void setPlayerOnly(boolean value) {
        this.isPlayerOnly = value;
        if(value) {
            setIrcEnabled(false);
        }
    }

    public boolean isIrcOnly() {
        return isIrcOnly;
    }

    public void setIrcOnly(boolean value) {
        this.isIrcOnly = value;
        if(value) {
            setIrcEnabled(true);
        }
    }

    public boolean isIrcOpOnly() {
        return isIrcOpOnly;
    }

    public void setIrcOpOnly(boolean value) {
        setIrcEnabled(true);
        this.isIrcOpOnly = value;
    }

    public boolean isIrcQueryOnly() {
        return isIrcQueryOnly;
    }

    public void setIrcQueryOnly(boolean value) {
        setIrcEnabled(true);
        this.isIrcQueryOnly = value;
    }

    @Unstable
    public void setUseNotices(boolean useNotices) {
        setIrcEnabled(true);
        this.useNotices = useNotices;
    }

    public boolean useNotices() {
        return useNotices;
    }

    public Options getCliOptions() {
        if (cliOptions != null && !cliOptions.hasOption("h") && !cliOptions.hasLongOption("help") && isDefaultHelpEnabled()) {
            cliOptions.addOption("h", "help", false, "Shows this message");
        }
        return cliOptions;
    }

    public void setCliOptions(Options cliOptions) {
        this.cliOptions = cliOptions;
    }

    public String getCmdLineSyntax() {
        return cmdLineSyntax;
    }

    public void setCmdLineSyntax(String cmdLineSyntax) {
        if (cmdLineSyntax == null) {
            cmdLineSyntax = "";
        }
        this.cmdLineSyntax = cmdLineSyntax;
    }

    public void setCliHelpFormatter(HelpFormatter cliHelpFormatter) {
        this.cliHelpFormatter = cliHelpFormatter;
    }

    public HelpFormatter getCliHelpFormatter(CommandSender sender, Command cmd, Writer writer) {
        if (cliHelpFormatter == null) {
            cliHelpFormatter = new HelpFormatter();
            cliHelpFormatter.setNewLine(System.lineSeparator());
            cliHelpFormatter.printHelp(new PrintWriter(writer), HelpFormatter.DEFAULT_WIDTH,
                                       getCmdLineSyntax()
                                               .replaceAll("\\{ALIAS\\}", cmd.getName())
                                               .replaceAll("\\{PREFIX\\}", parentCommand.getCommandPrefix(sender)), null, getCliOptions(),
                                       HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        }
        return cliHelpFormatter;
    }

    public boolean isDefaultHelpEnabled() {
        return defaultHelpEnabled;
    }

    public void setDefaultHelpEnabled(boolean defaultHelpEnabled) {
        this.defaultHelpEnabled = defaultHelpEnabled;
    }

    public boolean isIrcEnabled() {
        return ircEnabled;
    }

    public void setIrcEnabled(boolean ircEnabled) {
        if (ircEnabled && isPlayerOnly) {
            throw new IllegalStateException("Command is player only, can not enable IRC features!");
        }
        this.ircEnabled = ircEnabled;
    }
}
