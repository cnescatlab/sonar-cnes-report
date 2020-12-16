package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Language;
import fr.cnes.sonar.report.providers.*;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ProviderFactoryTest extends CommonTest {

    private static final String TOKEN = "token";
    private static final String PROJECT = "project";
    private static final String BRANCH = "branch";

    @Test
    public void createTest() {
        final ProviderFactory providerFactory = new ProviderFactory(sonarQubeServer, TOKEN, PROJECT, BRANCH);
        Assert.assertNotNull(providerFactory);

        AbstractDataProvider provider = providerFactory.create(IssuesProvider.class);
        Assert.assertTrue(provider instanceof IssuesProvider);

        provider = providerFactory.create(MeasureProvider.class);
        Assert.assertTrue(provider instanceof MeasureProvider);

        provider = providerFactory.create(ProjectProvider.class);
        Assert.assertTrue(provider instanceof ProjectProvider);

        provider = providerFactory.create(QualityProfileProvider.class);
        Assert.assertTrue(provider instanceof QualityProfileProvider);

        provider = providerFactory.create(QualityGateProvider.class);
        Assert.assertTrue(provider instanceof QualityGateProvider);

        provider = providerFactory.create(LanguageProvider.class);
        Assert.assertTrue(provider instanceof LanguageProvider);
    }



    @Test
    public void languageProviderTest() throws NoSuchFieldException, BadSonarQubeRequestException, SonarQubeException, IllegalAccessException {

        // Use introspection to inject language and avoid network call
        LanguageProvider languageProvider = new LanguageProvider(sonarQubeServer, TOKEN, PROJECT);
        Field field = LanguageProvider.class.getDeclaredField("languages");
        field.setAccessible(true);
        HashMap hashMap = new HashMap();
        Language cpp = new Language();
        cpp.setKey("cpp");
        cpp.setName("C++");
        hashMap.put("cpp", cpp);
        field.set(languageProvider, hashMap);

        // Test
        String language = languageProvider.getLanguage("notalanguage");
        Assert.assertEquals("?", language);

        String language2 = languageProvider.getLanguage("cpp");
        Assert.assertEquals("C++", language2);


    }

}
