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

package de.static_interface.sinklibrary.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class FileUtil {

    /**
     * Create a file
     *
     * @param file file to create
     * @return true if file exists or has been created, false if it failed
     */
    public static boolean createFile(File file) {
        if (file.exists()) {
            return true;
        }

        if (!file.mkdirs()) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't create Directorys for file " + file + "!");
            return false;
        }

        try {
            return file.createNewFile();
        } catch (IOException ignored) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't create file: " + file);
            return false;
        }
    }

    /**
     * Backup a file
     *
     * @param file   File to backup
     * @param notify Send message to Console?
     * @throws java.lang.RuntimeException When backup fails
     */
    public static void backupFile(File file, boolean notify) throws IOException {
        if (notify) {
            SinkLibrary.getInstance().getCustomLogger().log(Level.INFO, "Creating backup of " + file + "...");
        }

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY-hh.mm");
        String date = format.format(new Date());

        Path sourcePath = file.getAbsoluteFile().toPath();
        Path targetPath = Paths.get(file.getPath() + '.' + date + ".backup");
        try {
            Files.copy(sourcePath, targetPath, REPLACE_EXISTING);
        } catch (IOException e) {
            SinkLibrary.getInstance().getCustomLogger().log(Level.SEVERE, "Couldn't backup file: " + file.getAbsolutePath());
            throw e;
        }
    }
}
