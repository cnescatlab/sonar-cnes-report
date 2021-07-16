package fr.cnes.sonar.report.model;

import fr.cnes.sonar.report.CommonTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class LanguagesTest extends CommonTest {

    @Test
    public void getLanguageTest() {
        Languages languages = new Languages();
        Map<String, Language> hashMap = new HashMap<>();
        Language cpp = new Language();
        cpp.setKey("cpp");
        cpp.setName("C++");
        hashMap.put("cpp", cpp);
        languages.setLanguages(hashMap);

        String language = languages.getLanguage("notalanguage");
        Assert.assertEquals("?", language);

        String language2 = languages.getLanguage("cpp");
        Assert.assertEquals("C++", language2);
    }
}