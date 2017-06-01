package fr.cnes.sonar.report.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a source code file
 * @author begarco
 */
public class CodeFile {
    /**
     * Name of the file
     */
    private String name;
    /**
     * Path of the file
     */
    private String path;
    /**
     * Content of the file
     */
    private ArrayList<String> content;

    /**
     * Complete constructor
     * @param name Name of the file
     * @param path Path to the file
     * @param content Content of the file
     */
    public CodeFile(String name, String path, String[] content) {
        this.name = name;
        this.path = path;
        this.content = new ArrayList<>();
        this.content.addAll(Arrays.asList(content));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get content as an array and not a list
     * @return an array of strings
     */
    public String[] getContent() {
        String[] res = new String[content.size()];
        content.toArray(res);
        return res;
    }

    /**
     * Set the list of content from an array
     * @param content array for setting the list
     */
    public void setContent(String[] content) {
        this.content.addAll(Arrays.asList(content));
    }

    /**
     * Get a range of lines in the corresponding file
     * @param start Index of the line where the range starts
     * @param end Index of the line where the range ends
     * @return Array of strings containing wanted lines
     */
    public String[] getLines(int start, int end) {
        // get the wanted sub list
        List<String> subList = content.subList(start, end);
        // create a new array to conatain the result
        String[] res = new String[subList.size()];
        // convert list to array
        subList.toArray(res);

        return res;
    }
}
