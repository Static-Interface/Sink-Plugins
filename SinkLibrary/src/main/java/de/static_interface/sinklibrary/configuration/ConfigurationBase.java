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

package de.static_interface.sinklibrary.configuration;

import com.google.common.io.Files;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.util.FileUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

@SuppressWarnings(
        {"OverlyBroadCatchBlock", "InstanceMethodNamingConvention", "BooleanMethodNameMustStartWithQuestion", "InstanceMethodNamingConvention"})
public abstract class ConfigurationBase {

    private static HashMap<String, ConfigurationBase> configs = new HashMap<>();
    protected File yamlFile = null;
    protected YamlConfiguration yamlConfiguration = null;
    protected HashMap<String, Object> defaultValues = null;
    HashMap<String, String> comments = new HashMap<>();

    /**
     * Create a new configuration
     * @param file Configurations YAML file (will be created if doesn't exist)
     */
    public ConfigurationBase(File file) {
        yamlFile = file;
    }

    /**
     * Inits a new configuration
     *
     * @param file   Configurations YAML file (will be created if doesn't exist)
     * @param init If true, the config file will be created if it doesn't exists
     */
    public ConfigurationBase(File file, boolean init) {
        yamlFile = file;
        if (init) {
            init();
        }
    }

    public static HashMap<String, ConfigurationBase> getConfigs() {
        return configs;
    }

    /**
     * Called when the config is generated, you can add e.g. values which won't be regenerated if you delete them
     */
    public void onCreate() {
    }

    /**
     * Inits the configuration
     */
    public void init() {
        try {
            boolean createNewConfiguration = !exists();

            if (createNewConfiguration) {
                SinkLibrary.getInstance().getCustomLogger().log(Level.INFO, "Creating new configuration: " + yamlFile);
            }

            if (yamlFile != null) {
                Files.createParentDirs(yamlFile);

                if (createNewConfiguration && !yamlFile.createNewFile()) {
                    SinkLibrary.getInstance().getCustomLogger().log(Level.SEVERE, "Couldn't create configuration: " + yamlFile);
                }
            }

            defaultValues = new HashMap<>();

            yamlConfiguration = new YamlConfiguration();

            if (yamlFile != null) {
                yamlConfiguration.load(yamlFile);
            }
            addDefaults();
            if (createNewConfiguration) {
                onCreate();
            }

            configs.put(yamlFile.getCanonicalPath(), this);
            save();

        } catch (InvalidConfigurationException e) {
            SinkLibrary.getInstance().getCustomLogger().log(Level.SEVERE, "Invalid configuration file: " + yamlFile);
            SinkLibrary.getInstance().getCustomLogger().log(Level.SEVERE, e.getMessage());
            recreate();
        } catch (Exception e) {
            SinkLibrary.getInstance().getCustomLogger().log(Level.SEVERE, "Couldn't create configuration file: " + getFile().getName());
            SinkLibrary.getInstance().getCustomLogger().log(Level.SEVERE, "Exception occurred: ", e);
        }
    }

    /**
     * @author dumptruckman
     */
    private void writeToFile(File file) throws IOException {
        getYamlConfiguration().save(getFile());
        // if there's comments to add and it saved fine, we need to add comments
        if (!comments.isEmpty()) {
            // String array of each line in the config file
            String[] yamlContents = FileUtil.convertFileToString(file).split("[" + System.getProperty("line.separator") + "]");

            // This will hold the newly formatted line
            String newContents = "";
            // This holds the current path the lines are at in the config
            String currentPath = "";
            // This tells if the specified path has already been commented
            boolean commentedPath = false;
            // This flags if the line is a node or unknown text.
            boolean node;
            // The depth of the path. (number of words separated by periods - 1)
            int depth = 0;
            // Loop through the config lines
            for (String line : yamlContents) {
                String whiteSpacePrefix = "";

                // If the line is a node (and not something like a list value)
                if (line.contains(": ") || (line.length() > 1 && line.charAt(line.length() - 1) == ':')) {

                    // This is a new node so we need to mark it for commenting (if there are comments)
                    commentedPath = false;
                    // This is a node so flag it as one
                    node = true;

                    // Grab the index of the end of the node name
                    int index = 0;
                    index = line.indexOf(": ");
                    if (index < 0) {
                        index = line.length() - 1;
                    }
                    // If currentPath is empty, store the node name as the currentPath. (this is only on the first iteration, i think)
                    if (currentPath.isEmpty()) {
                        currentPath = line.substring(0, index);
                    } else {
                        // Calculate the whitespace preceding the node name
                        int whiteSpace = 0;
                        for (int n = 0; n < line.length(); n++) {
                            if (line.charAt(n) == ' ') {
                                whiteSpace++;
                            } else {
                                break;
                            }
                        }
                        // Find out if the current depth (whitespace * 2) is greater/lesser/equal to the previous depth
                        if (whiteSpace / 2 > depth) {
                            // Path is deeper.  Add a . and the node name
                            currentPath += "." + line.substring(whiteSpace, index);
                            depth++;
                        } else if (whiteSpace / 2 < depth) {
                            // Path is shallower, calculate current depth from whitespace (whitespace / 2) and subtract that many levels from the currentPath
                            int newDepth = whiteSpace / 2;
                            for (int i = 0; i < depth - newDepth; i++) {
                                currentPath = currentPath.replace(currentPath.substring(currentPath.lastIndexOf(".")), "");
                            }
                            // Grab the index of the final period
                            int lastIndex = currentPath.lastIndexOf(".");
                            if (lastIndex < 0) {
                                // if there isn't a final period, set the current path to nothing because we're at root
                                currentPath = "";
                            } else {
                                // If there is a final period, replace everything after it with nothing
                                currentPath = currentPath.replace(currentPath.substring(currentPath.lastIndexOf(".")), "");
                                currentPath += ".";
                            }
                            // Add the new node name to the path
                            currentPath += line.substring(whiteSpace, index);
                            // Reset the depth
                            depth = newDepth;
                        } else {
                            // Path is same depth, replace the last path node name to the current node name
                            int lastIndex = currentPath.lastIndexOf(".");
                            if (lastIndex < 0) {
                                // if there isn't a final period, set the current path to nothing because we're at root
                                currentPath = "";
                            } else {
                                // If there is a final period, replace everything after it with nothing
                                currentPath = currentPath.replace(currentPath.substring(currentPath.lastIndexOf(".")), "");
                                currentPath += ".";
                            }
                            //currentPath = currentPath.replace(currentPath.substring(currentPath.lastIndexOf(".")), "");
                            currentPath += line.substring(whiteSpace, index);
                        }
                        for (int i = 0; i < whiteSpace; i++) {
                            whiteSpacePrefix += " ";
                        }
                    }

                } else {
                    node = false;
                }

                if (node) {
                    String comment = null;
                    if (!commentedPath) {
                        // If there's a comment for the current path, retrieve it and flag that path as already commented
                        if (comments.get(currentPath) != null) {
                            // whiteSpaces for comments
                            comment = whiteSpacePrefix + "# " + comments.get(currentPath);
                        }
                    }
                    if (comment != null) {
                        // Add the comment to the beginning of the current line
                        line = comment + System.getProperty("line.separator") + line + System.getProperty("line.separator");
                        commentedPath = true;
                    } else {
                        // Add a new line as it is a node, but has no comment
                        line += System.getProperty("line.separator");
                    }
                }
                // Add the (modified) line to the total config String
                newContents += line + ((!node) ? System.getProperty("line.separator") : "");
            }
            /*
             * Due to a bukkit bug we need to strip any extra new lines from the
			 * beginning of this file, else they will multiply.
			 */
            while (newContents.startsWith(System.getProperty("line.separator"))) {
                newContents = newContents.replaceFirst(System.getProperty("line.separator"), "");
            }

            try {
                // Write the string to the config file
                FileUtil.stringToFile(newContents, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set comment of path, dont forget to call {@link #save()}
     * @param path YAML Path
     * @param comment Comment
     */
    public void setComment(String path, String comment) {
        comments.put(path, comment);
    }


    /**
     * Create Configuration File
     * @deprecated use {@link #init()} instead
     */
    @Deprecated
    public void create() {
        init();
    }

    /**
     * Add default values to the configuration using {@link #addDefault(String, Object)}
     */
    public abstract void addDefaults();

    /**
     * Save config file
     */
    public void save() {
        if (getFile() == null) {
            return;
        }
        if (!exists()) {
            return;
        }

        try {
            writeToFile(getFile());
        } catch (IOException e) {
            if (SinkLibrary.getInstance().getSettings().isDebugEnabled()) {
                SinkLibrary.getInstance().getCustomLogger().log(Level.SEVERE, "Couldn't save configuration file: " + getFile() + '!', e);
            } else {
                SinkLibrary.getInstance().getCustomLogger().log(Level.SEVERE, "Couldn't save configuration file: " + getFile() + '!');
            }
        }
    }

    /**
     * @param path  Path to value
     * @param value Value of path
     */
    public void set(String path, Object value) {
        try {
            if (getFile() == null || getYamlConfiguration() == null) {
                addDefault(path, value);
            }
            getYamlConfiguration().set(path, value);
            save();
        } catch (Exception e) {
            SinkLibrary.getInstance().getCustomLogger()
                    .log(Level.WARNING, "Configuration:" + getFile() + ": Couldn't save " + value + " to path " + path, e);
        }
    }

    /**
     * Get value from configuration. If it doesn't exists, it will return the default value.
     *
     * @param path Path to value
     * @return Value of path
     */
    public Object get(String path) {
        Object value;
        if (getYamlConfiguration() == null || getFile() == null) {
            return getDefault(path);
        }
        try {
            value = getYamlConfiguration().get(path);
            if (!path.equals("General.EnableDebug") && !path.equals("General.EnableLog")) {
                SinkLibrary.getInstance().getCustomLogger().debug(getFile().getName() + ": Loaded value: " + value + " for path: " + path);
            }
            if (value == null) {
                throw new NullPointerException("Path " + path + " returned null!");
            }
        } catch (Exception e) {
            value = getDefault(path);
        }

        if (value instanceof String) {
            String stringValue = String.valueOf(value);

            // fix old configs
            if (stringValue.contains("%s")) {
                int i = 0;
                String tmp = "";
                for (String s : stringValue.split(" ")) {
                    if (tmp.equals("")) {
                        tmp = s.replaceFirst("%\\s", "{" + i + "}");
                    } else {
                        tmp += " " + s.replaceFirst("%\\s", "{" + i + "}");
                    }
                    i++;
                }
                value = tmp;
                set(path, value);
            }
        }
        return value;
    }

    public Object get(String path, Object defaultValue) {
        try {
            return get(path);
        } catch (NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Get YAML Configuration
     */
    public YamlConfiguration getYamlConfiguration() {
        if (yamlConfiguration == null) {
            throw new NullPointerException("yamlConfiguration is null! Did you forgot to call load()?");
        }
        return yamlConfiguration;
    }

    /**
     * @return True if the config file exists
     */
    public boolean exists() {
        return getFile() != null && getFile().exists();
    }

    /**
     * Load Configuration
     * @deprecated use {@link #init()} instead
     */
    @Deprecated
    public void load() {
        init();
    }

    /**
     * Get default value for path
     *
     * @param path Path to value
     */
    public Object getDefault(String path) {
        if (getDefaults() == null) {
            throw new RuntimeException("defaultValues are null! Couldn't read value from path: " + path);
        }
        return getDefaults().get(path);
    }

    /**
     * Add a default value, it will be used when the config is beeing generated or loading
     * values fails
     * @param path  Path to value
     * @param value Value of path
     */
    public void addDefault(String path, Object value) {
        addDefault(path, value, null);
    }

    public void addDefault(String path, Object value, String comment) {
        if (getDefaults() == null) {
            throw new NullPointerException("getDefaults() equals null!");
        }
        if (getYamlConfiguration() == null) {
            throw new NullPointerException("getYamlConfiguration() equals null!");
        }

        if (!getYamlConfiguration().isSet(path) || getYamlConfiguration().get(path) == null) {
            getYamlConfiguration().set(path, value);
            if (comment != null) {
                setComment(path, comment);
            }
        }
        getDefaults().put(path, value);
    }


    /**
     * Get Defaults
     * @return Default values
     */
    public HashMap<String, Object> getDefaults() {
        return defaultValues;
    }

    /**
     * Backup Configuration.
     * @throws IOException if backup fails
     */
    public void backup() throws IOException {
        FileUtil.backupFile(getFile(), true);
    }

    /**
     * Delete Configuration
     */
    public void delete() {
        if (getFile().exists() && !getFile().delete()) {
            throw new RuntimeException("Couldn't delete file: " + getFile());
        }
    }

    /**
     * Get configuration file
     *
     * @return configuration file
     */
    public File getFile() {
        return yamlFile;
    }

    /**
     * Recreate configuration
     */
    public void recreate() {
        SinkLibrary.getInstance().getCustomLogger().log(Level.WARNING, "Recreating Configuration: " + getFile());
        try {
            backup();
        } catch (IOException e) {
            SinkLibrary.getInstance().getCustomLogger().log(Level.SEVERE, "Couldn't backup configuration: " + getFile(), e);
            return;
        }
        delete();
        init();
    }

    /**
     * Reload a config
     */
    public void reload() {
        if (!exists()) {
            return;
        }
        init();
    }
}
