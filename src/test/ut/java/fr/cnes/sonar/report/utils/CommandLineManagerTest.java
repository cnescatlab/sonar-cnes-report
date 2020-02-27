package fr.cnes.sonar.report.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

class CommandLineManagerTest {

	/**
	 * Test valid parameter with value
	 */
	@Test
	public void parseWithValidArguments() {
		final CommandLineManager commandLineManager = new CommandLineManager();
		commandLineManager.parse(new String[] { "-s", "localhost" });
		assertThat(commandLineManager.getOptionValue("-s"), is("localhost"));
	}

	/**
	 * Test help parameter
	 */
	@Test
	public void parseWithHelp() {
		final CommandLineManager commandLineManager = new CommandLineManager();
		commandLineManager.parse(new String[] { "-h", "this parameter is ignored" });
		assertThat(commandLineManager.hasOption("-h"), is(true));
	}

	/**
	 * Test incomplete arguments
	 */
	@Test(expected = IllegalArgumentException.class)
	public void parseWithMissingOption() {
		final CommandLineManager commandLineManager = new CommandLineManager();
		commandLineManager.parse(new String[] { "-s" });
	}
}