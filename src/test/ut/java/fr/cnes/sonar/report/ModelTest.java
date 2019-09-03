package fr.cnes.sonar.report;

import fr.cnes.sonar.report.model.Comment;
import fr.cnes.sonar.report.model.Component;
import fr.cnes.sonar.report.model.Measure;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;

public class ModelTest
{
    @Test
    public void commentTest(){
        Comment comment = new Comment();
        Assert.assertEquals("", comment.getLogin());
        Assert.assertEquals("", comment.getMarkdown());
        Assert.assertEquals("", comment.getCreatedAt());
        Assert.assertEquals("", comment.getHtmlText());
        Assert.assertEquals("", comment.getKey());
    }

    @Test
    public void componentTest(){
        Component component = new Component();
        component.setId("4");
        Assert.assertEquals("4", component.getId());

        component.setName("name");
        Assert.assertEquals("name", component.getName());

        component.setPath("path");
        Assert.assertEquals("path", component.getPath());

        ArrayList measures = new ArrayList();
        measures.add(new Measure());

        Assert.assertThat(component.toString(), instanceOf(String.class));
        Assert.assertThat(component.toMap(), instanceOf(Map.class));


    }
}
