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

import fr.cnes.sonar.report.factory.ReportFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFolder {
    private ZipFolder(){
        throw new IllegalStateException("Utility class");
    }
    /**
     * Create a zip file from a directory
     *
     * INFO: This method is taken from https://stackoverflow.com/a/32052016
     *
     * @param sourceDirPath directory you want to compress
     * @param zipFilePath output path
     * @throws IOException
     */
    public static <Stream> void pack(String sourceDirPath, String zipFilePath) throws IOException {
        Path p = Files.createFile(Paths.get(zipFilePath));
        java.util.stream.Stream<Path> file = null;
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(sourceDirPath);
            file = Files.walk(pp);
            file.filter(path -> !path.toFile().isDirectory())
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            Logger LOGGER = Logger.getLogger(ZipFolder.class.getName());
                            LOGGER.warning(e.getMessage());
                        }
                    });
            file.close();
        }
        catch(IOException e){
            if(file!=null)
                file.close();
        }
    }
}
