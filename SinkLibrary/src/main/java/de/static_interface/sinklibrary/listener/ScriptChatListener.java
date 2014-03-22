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

package de.static_interface.sinklibrary.listener;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import de.static_interface.sinklibrary.commands.ScriptCommand;
import groovy.lang.GroovyShell;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.logging.Level;

public class ScriptChatListener implements Listener
{
    public HashMap<String, GroovyShell> playerData = new HashMap<>(); // PlayerName - Instance
    public HashMap<String, String> codeData = new HashMap<>(); // PlayerName - Code

    Plugin plugin;

    public ScriptChatListener(Plugin plugin) {this.plugin = plugin;}

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleChatScript(final AsyncPlayerChatEvent event)
    {
        final User user = SinkLibrary.getUser(event.getPlayer());
        if ( !ScriptCommand.isEnabled(user) ) return;
        event.setCancelled(true);
        Bukkit.getScheduler().runTask(plugin, new Runnable()
        {
            String currentLine = ChatColor.stripColor(event.getMessage());
            String code = "";
            String nl = System.getProperty("line.separator");

            public void run()
            {
                String name = event.getPlayer().getName();
                GroovyShell shellInstance;

                if ( !playerData.containsKey(name) )
                {
                    SinkLibrary.getCustomLogger().log(Level.INFO, "Initializing ShellInstance for " + user.getName());
                    shellInstance = new GroovyShell();
                    shellInstance.setVariable("me", user);
                    shellInstance.setVariable("plugin", plugin);
                    playerData.put(name, shellInstance);
                }
                else shellInstance = playerData.get(event.getPlayer().getName());

                boolean codeSet = false;
                String defaultImports = "import de.static_interface.sinklibrary.*;" + nl +
                        "import org.bukkit.*;" + nl + nl;

                if ( !codeData.containsKey(name) )
                {
                            codeData.put(name, currentLine);
                    code = currentLine + defaultImports;
                    codeSet = true;
                }

                boolean useNl = currentLine.startsWith("<");
                if ( useNl )
                {
                    currentLine = currentLine.replaceFirst("<", "");
                    nl = "";
                }

                currentLine = currentLine.trim();

                if ( currentLine.equals(".help") || currentLine.equals(".clear") || currentLine.equals(".execute") || currentLine.equals(".history") || currentLine.equals(".defaultimports") )
                {
                    currentLine = "";
                }
                String prevCode = codeData.get(name);

                if ( !codeSet )
                {
                    if ( currentLine.startsWith("import") ) code = currentLine + nl + prevCode;
                    else code = prevCode + nl + currentLine;
                    codeData.put(name, code);
                }

                String[] args = event.getMessage().split(" ");
                String mode = args[0].toLowerCase();

                user.getPlayer().getAddress().getHostName();

                switch ( mode )
                {
                    case ".defaultimports":
                        codeData.put(name, defaultImports + code);
                        user.sendMessage(ChatColor.GOLD + "Imported default imports!");
                        break;
                    case ".help":
                        user.sendMessage(ChatColor.GREEN + "[Help] " + ChatColor.GRAY + "Available Commands: help, execute, history");
                        break;

                    case ".clear":
                        codeData.remove(name);
                        user.sendMessage(ChatColor.DARK_RED + "History cleared");
                        break;

                    case ".execute":
                        try
                        {
                            String result = String.valueOf(shellInstance.evaluate(code));
                            if ( result != null && !result.isEmpty() && !result.equals("null") )
                                user.sendMessage(ChatColor.AQUA + "Output: " + ChatColor.GREEN + result);
                            codeData.put(name, code);
                        }
                        catch ( Exception e )
                        {
                            sendErrorMessage(user, e.getMessage());
                        }
                        break;

                    case ".history":
                        code = code.replace(".history", "");
                        codeData.put(name, code);
                        user.sendMessage(ChatColor.GOLD + "-------|History|-------");
                        user.sendMessage(ChatColor.WHITE + formatCode(code));
                        user.sendMessage(ChatColor.GOLD + "-----------------------");
                        break;

                    default:
                        if ( mode.startsWith(".") )
                        {
                            user.sendMessage("\"." + mode + "\" is not a valid command");
                            break;
                        }
                        user.sendMessage(ChatColor.DARK_GREEN + "[Input] " + ChatColor.WHITE + formatCode(currentLine));
                        break;
                }
            }
        });
    }

    private String formatCode(String code)
    {
        ChatColor defaultColor = ChatColor.DARK_BLUE;
        ChatColor codeColor = ChatColor.RESET;
        ChatColor classColor = ChatColor.BLUE;
        ChatColor stringColor = ChatColor.RED;

        String tmpCode = "";

        for ( String word : code.split(" ") )
        {
            word = word.replace("import", ChatColor.GOLD + "import" + codeColor);
            word = word.replace("package", ChatColor.GOLD + "package" + codeColor);

            word = word.replace("class", defaultColor + "class" + codeColor);
            word = word.replace("implements", defaultColor + "implements" + codeColor);
            word = word.replace("extends", defaultColor + "extends" + codeColor);
            word = word.replace("enum", defaultColor + "enum" + codeColor);
            word = word.replace("interface", defaultColor + "interface" + codeColor);

            word = word.replace("private", defaultColor + "private" + codeColor);
            word = word.replace("public", defaultColor + "public" + codeColor);
            word = word.replace("protected", defaultColor + "protected" + codeColor);

            word = word.replace("final", defaultColor + "final" + codeColor);
            word = word.replace("static", defaultColor + "static" + codeColor);
            word = word.replace("native", defaultColor + "native" + codeColor);
            word = word.replace("throws", defaultColor + "throws" + codeColor);
            word = word.replace("transient", defaultColor + "transient" + codeColor);
            word = word.replace("volatile", defaultColor + "volatile" + codeColor);
            word = word.replace("synchronized", defaultColor + "synchronized" + codeColor);
            word = word.replace("strictfp", defaultColor + "strictfp" + codeColor);
            word = word.replace("const", defaultColor + "const" + codeColor);

            word = word.replace("try", defaultColor + "try" + codeColor);
            word = word.replace("catch", defaultColor + "catch" + codeColor);
            word = word.replace("finally", defaultColor + "finally" + codeColor);
            word = word.replace("throw", defaultColor + "throw" + codeColor);

            word = word.replace("while", defaultColor + "while" + codeColor);
            word = word.replace("continue", defaultColor + "continue" + codeColor);

            word = word.replace("void", defaultColor + "void" + codeColor);
            word = word.replace("return", defaultColor + "return" + codeColor);
            word = word.replace("switch", defaultColor + "switch" + codeColor);
            word = word.replace("case", defaultColor + "case" + codeColor);
            word = word.replace("break", defaultColor + "break" + codeColor);
            word = word.replace("super", defaultColor + "super" + codeColor);
            word = word.replace("new", defaultColor + "new" + codeColor);
            word = word.replace("this", defaultColor + "this" + codeColor);
            word = word.replace("goto", defaultColor + "goto" + codeColor);

            word = word.replace("if", defaultColor + "if" + codeColor);
            word = word.replace("else", defaultColor + "else" + codeColor);
            word = word.replace("true", defaultColor + "void" + codeColor);
            word = word.replace("false", defaultColor + "void" + codeColor);
            word = word.replace("instanceof", defaultColor + "instanceof" + codeColor);
            word = word.replace("for", defaultColor + "for" + codeColor);
            word = word.replace("while", defaultColor + "while" + codeColor);
            word = word.replace("assert", defaultColor + "assert" + codeColor);

            word = word.replace("String", classColor + "String" + codeColor);
            word = word.replace("Bukkit", classColor + "Bukkit" + codeColor);
            word = word.replace("BukkitUtil", classColor + "BukkitUtil" + codeColor);
            word = word.replace("SinkLibrary", classColor + "SinkLibrary" + codeColor);
            word = word.replace("User", classColor + "User" + codeColor);
            word = word.replace("Logger", classColor + "Logger" + codeColor);
            word = word.replace("Player", classColor + "Player" + codeColor);
            word = word.replace("Plugin", classColor + "Plugin" + codeColor);

            word = word.replace("int", defaultColor + "int" + codeColor);
            word = word.replace("boolean", defaultColor + "boolean" + codeColor);
            word = word.replace("long", defaultColor + "long" + codeColor);
            word = word.replace("short", defaultColor + "short" + codeColor);
            word = word.replace("float", defaultColor + "float" + codeColor);
            word = word.replace("byte", defaultColor + "byte" + codeColor);
            word = word.replace("char", defaultColor + "char" + codeColor);

            tmpCode += word + ' ';
        }

        code = tmpCode;

        boolean stringStart = false;

        //Set String colro
        String tmp = "";
        for ( char Char : code.toCharArray() )
        {
            if ( Char == '"' )
            {
                if ( !stringStart )
                {
                    tmp += stringColor;
                    stringStart = true;
                }

                tmp += Char;

                if ( stringStart )
                {
                    stringStart = false;
                    tmp += codeColor;
                }
            }
        }

        return tmp;
    }

    private void sendErrorMessage(User user, String message)
    {
        user.sendMessage(ChatColor.DARK_RED + "Exception: " + ChatColor.RED + message);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        String name = event.getPlayer().getName();
        ScriptCommand.disable(SinkLibrary.getUser(event.getPlayer()));
        if ( playerData.containsKey(name) )
        {
            playerData.get(name).getClassLoader().clearCache();
            playerData.remove(name);
        }
    }
}
