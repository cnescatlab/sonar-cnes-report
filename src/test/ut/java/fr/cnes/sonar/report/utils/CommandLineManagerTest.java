package fr.cnes.sonar.report.utils;

import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;

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
		assertThat(commandLineManager.getOptionValue("s"), is("localhost"));
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
		assertThat(commandLineManager.hasOption("-h"), is(true));
	}

	/**
	 * Test command line version argument
	 */
	@Test
	public void parseWithVersionOption() {
		final CommandLineManager commandLineManager = new CommandLineManager();
		exit.expectSystemExitWithStatus(0);
		commandLineManager.parse(new String[] { "-v", "this parameter is ignored" });
		assertThat(commandLineManager.hasOption("-v"), is(true));
	}
}
