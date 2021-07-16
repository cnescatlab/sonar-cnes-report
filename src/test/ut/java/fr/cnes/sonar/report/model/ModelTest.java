package fr.cnes.sonar.report.model;

import fr.cnes.sonar.report.CommonTest;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;

public class ModelTest extends CommonTest {
    @Test
    public void commentTest() {
        Comment comment = new Comment();
        Assert.assertEquals("", comment.getLogin());
        Assert.assertEquals("", comment.getMarkdown());
        Assert.assertEquals("", comment.getCreatedAt());
        Assert.assertEquals("", comment.getHtmlText());
        Assert.assertEquals("", comment.getKey());
    }

    @Test
    public void componentTest() {
        Component component = new Component();
        component.setId("4");
        Assert.assertEquals("4", component.getId());

        component.setName("name");
        Assert.assertEquals("name", component.getName());

        component.setPath("path");
        Assert.assertEquals("path", component.getPath());

        List<Measure> measures = new ArrayList<>();
        measures.add(new Measure());

        Assert.assertThat(component.toString(), instanceOf(String.class));
        Assert.assertThat(component.toMap(), instanceOf(Map.class));

    }

    @Test
    public void projectTest() {
        Project project = new Project(PROJECT_KEY, "CNES Report", "Lequal", "1.0.0", "Un simple test.", BRANCH);

        Assert.assertEquals(PROJECT_KEY, project.getKey());
        project.setKey("NewKey");
        Assert.assertEquals("NewKey", project.getKey());

        Assert.assertEquals("CNES Report", project.getName());
        project.setName("NewName");
        Assert.assertEquals("NewName", project.getName());

        Assert.assertEquals(BRANCH, project.getBranch());
        project.setBranch("NewBranch");
        Assert.assertEquals("NewBranch", project.getBranch());

        Assert.assertEquals("Lequal", project.getOrganization());
        project.setOrganization("NewOrganization");
        Assert.assertEquals("NewOrganization", project.getOrganization());

        Assert.assertEquals("1.0.0", project.getVersion());
        project.setVersion("NewVersion");
        Assert.assertEquals("NewVersion", project.getVersion());

        Assert.assertEquals("Un simple test.", project.getDescription());
        project.setDescription("NewDescription");
        Assert.assertEquals("NewDescription", project.getDescription());

        Assert.assertEquals(true, project.getLanguages().isEmpty());
    }
}
