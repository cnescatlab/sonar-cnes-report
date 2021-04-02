/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.plugin.tools;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class FileTools {

    /** Logger of this class */
    private static final Logger LOGGER = Logger.getLogger(FileTools.class.getName());

    private FileTools(){
        throw new IllegalStateException("Utility class");
    }
    /**
     * Delete a folder
     * WARNING: Recursive call, maybe it's not a good idea for large folder with lots of subfolder.
     * (created to clean the output folder when report is generated with sonarqube plugin).
     * @param folder
     */
    public static void deleteFolder(File folder){
        File[] files =  folder.listFiles();

        for(File toDelete: files != null ? files : new File[0]){

            if (toDelete.isDirectory()) {
                deleteFolder(toDelete);
            }
            else {
                try {
                    Files.deleteIfExists(toDelete.toPath());
                } catch (IOException e) {
                    LOGGER.warning(e.getMessage());
                }
            }
        }
        try {
            Files.deleteIfExists(folder.toPath());
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
    }
}
