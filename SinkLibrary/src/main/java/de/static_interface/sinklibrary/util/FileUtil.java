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

package de.static_interface.sinklibrary.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
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
            SinkLibrary.getInstance().getLogger().info("Creating backup of " + file + "...");
        }

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY-hh.mm");
        String date = format.format(new Date());

        Path sourcePath = file.getAbsoluteFile().toPath();
        Path targetPath = Paths.get(file.getPath() + '.' + date + ".backup");
        try {
            Files.copy(sourcePath, targetPath, REPLACE_EXISTING);
        } catch (IOException e) {
            SinkLibrary.getInstance().getLogger().severe("Couldn't backup file: " + file.getAbsolutePath());
            throw e;
        }
    }

    /**
     * Pass a file and it will return it's contents as a string.
     *
     * @param file File to read.
     * @return Contents of file. String will be empty in case of any errors.
     * @author ElgarL
     */
    public static String convertFileToString(File file) {

        if (file != null && file.exists() && file.canRead() && !file.isDirectory()) {
            Writer writer = new StringWriter();
            InputStream is = null;

            char[] buffer = new char[1024];
            try {
                is = new FileInputStream(file);
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (IOException e) {
                System.out.println("Exception ");
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignore) {
                    }
                }
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    /**
     * Writes the contents of a string to a file.
     *
     * @param source String to write.
     * @param file File to write to.
     * @return True on success.
     * @throws IOException
     * @author ElgarL
     */
    public static boolean stringToFile(String source, File file) throws IOException {

        try {

            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");

            //BufferedWriter out = new BufferedWriter(new FileWriter(FileName));

            source.replaceAll("\n", System.getProperty("line.separator"));

            out.write(source);
            out.close();
            return true;

        } catch (IOException e) {
            System.out.println("Exception ");
            return false;
        }
    }

}
