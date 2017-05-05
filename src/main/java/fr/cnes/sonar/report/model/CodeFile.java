package fr.cnes.sonar.report.model;

import java.util.ArrayList;

/**
 * Represents a source code file
 * @author begarco
 */
public class CodeFile {
    private String name;
    private String path;
    private ArrayList<String> content;

    public CodeFile(String name, String path, String[] content) {
        this.setName(name);
        this.setPath(path);
        this.setContent(content);
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
        for (String s : content) {
            this.content.add(s);
        }
    }

    public String[] getLines(int start, int end) {
        return (String[]) content.subList(start, end).toArray();
    }
}
