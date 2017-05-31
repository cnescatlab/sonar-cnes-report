package fr.cnes.sonar.report.model;

import java.util.ArrayList;
import java.util.Arrays;

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
        this.setName(name);
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

    public String[] getContent() {
        return (String[]) content.toArray();
    }

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
        return (String[]) content.subList(start, end).toArray();
    }
}
