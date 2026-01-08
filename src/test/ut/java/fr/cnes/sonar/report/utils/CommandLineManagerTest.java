package fr.cnes.sonar.report.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
@Ignore
public class CommandLineManagerTest {

	@Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	/**
	 * Test valid parameter with value
	 */
	@Test
	public void parseWithValidArguments() {
		final CommandLineManager commandLineManager = new CommandLineManager();
		commandLineManager.parse(new String[] { "-s", "localhost" });
		assertEquals("localhost" , commandLineManager.getOptionValue("s"));
	}

	/**
	 * Test incomplete arguments
	 */
	@Test(expected = IllegalArgumentException.class)
	public void parseWithMissingOption() {
		final CommandLineManager commandLineManager = new CommandLineManager();
		commandLineManager.parse(new String[] { "-s" });
	}

	/**
	 * Test command line helper
	 */
	@Test
	public void parseWithHelperOption(){
		final CommandLineManager commandLineManager = new CommandLineManager();
		exit.expectSystemExitWithStatus(0);
		commandLineManager.parse(new String[] { "-h", "this parameter is ignored" });
		assertTrue(commandLineManager.hasOption("-h"));
	}

	/**
	 * Test command line version argument
	 */
	@Test
	public void parseWithVersionOption() {
		final CommandLineManager commandLineManager = new CommandLineManager();
		exit.expectSystemExitWithStatus(0);
		commandLineManager.parse(new String[] { "-v", "this parameter is ignored" });
		assertTrue(commandLineManager.hasOption("-v"));
	}
}
