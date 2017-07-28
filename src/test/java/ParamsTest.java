import fr.cnes.sonar.report.exceptions.MalformedParameterException;
import fr.cnes.sonar.report.exceptions.MissingParameterException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.input.Params;
import fr.cnes.sonar.report.input.ParamsFactory;
import org.junit.Test;

/**
 * Tests for Params and ParamsFactory classes
 * @author garconb
 */
public class ParamsTest {

    /**
     * Classical case
     * @throws Exception ...
     */
    @Test
    public void paramsCreationTest() throws Exception {
        String [] args = {"--sonar.project.id", "bob", "--sonar.url", "alice"};

        ParamsFactory paramsFactory = new ParamsFactory();

        Params params = paramsFactory.create(args);

        assert(params.isReliable());
    }

    /**
     * When a mandatory parameter is missing, an exception should be thrown
     * @throws Exception ...
     */
    @Test(expected = MissingParameterException.class)
    public void paramsCreationMissingMandatoryParameterTest() throws Exception {
        String [] args = {"--sonar.project.id", "bob"};

        ParamsFactory paramsFactory = new ParamsFactory();

        Params params = paramsFactory.create(args);

        assert(params.isReliable());
    }

    /**
     * When a parameter is malformed, for example, -- are missing
     * an exception should be thrown
     * @throws Exception ...
     */
    @Test(expected = MalformedParameterException.class)
    public void paramsCreationMalformedParameterTest() throws Exception {
        String [] args = {"sonar.project.id", "bob"};

        ParamsFactory paramsFactory = new ParamsFactory();

        Params params = paramsFactory.create(args);

        assert(params.isReliable());
    }

    /**
     * When a parameter has no value,
     * an exception should be thrown
     * @throws Exception ...
     */
    @Test(expected = UnknownParameterException.class)
    public void paramsCreationMissingValueParameterTest() throws Exception {
        String [] args = {"--sonar.project.id", "bob", "--sonar.url"};

        ParamsFactory paramsFactory = new ParamsFactory();

        Params params = paramsFactory.create(args);

        assert(params.isReliable());
    }

    /**
     * When a parameter is unknown,
     * an exception should be thrown
     * @throws Exception ...
     */
    @Test(expected = UnknownParameterException.class)
    public void paramsCreationUnknownParameterTest() throws Exception {
        String [] args = {"--alice", "bob"};

        ParamsFactory paramsFactory = new ParamsFactory();

        Params params = paramsFactory.create(args);

        assert(params.isReliable());
    }

}
