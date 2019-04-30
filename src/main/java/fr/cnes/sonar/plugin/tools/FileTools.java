package fr.cnes.sonar.plugin.tools;


import java.io.File;

public class FileTools {
    public static void deleteFolder(File folder){
        String[] files =  folder.list();
        File toDelete;
        for(String f: files != null ? files : new String[0]){
            toDelete = new File(f);
            if (toDelete.isDirectory()) deleteFolder(toDelete);
            else toDelete.delete();
        }
        folder.delete();
    }
}
