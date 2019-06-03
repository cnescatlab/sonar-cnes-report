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

public class FileTools {

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
        String[] files =  folder.list();
        File toDelete;
        for(String f: files != null ? files : new String[0]){
            toDelete = new File(f);
            if (toDelete.isDirectory()) deleteFolder(toDelete);
            else {
                try {
                    Files.delete(toDelete.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            Files.delete(folder.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
