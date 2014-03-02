package no.bekk.java.dpostbatch.task;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchBuilder;
import no.bekk.java.dpostbatch.model.MockLogger;
import no.bekk.java.dpostbatch.task.send.ValidateBatchTask;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ValidateBatchTaskTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	private BatchBuilder batchBuilder;
	private MockLogger logger;

	@Before
	public void setUp() throws IOException {
		batchBuilder = BatchBuilder.newBatch(tempFolder.newFolder().toPath());
		logger = new MockLogger();
	}

	@Test
	public void requiresSettingsFile() throws IOException {
		Batch batch = batchBuilder.settings(false).build();
		validateBatchThatShouldFail(batch, logger);
		assertThat(logger.logMessages, hasItem(containsString("Cannot find settingsfile")));
	}

	@Test
	public void requiresLettersFile() throws IOException {
		Batch batch = batchBuilder.letters(false).build();
		validateBatchThatShouldFail(batch, logger);
		assertThat(logger.logMessages, hasItem(containsString("Cannot find letters-file")));
	}

	@Test
	public void validatesLettersFormatAndReferencedFiles() throws IOException {
		String line = "not,valid,csv";
		Batch batch = batchBuilder.medBrevCsv(line).build();
		validateBatchThatShouldFail(batch, logger);
		assertThat(logger.logMessages, hasItem(containsString("Line 1 has wrong number of columns")));
	}

	private void validateBatchThatShouldFail(Batch batch, MockLogger logger) {
		try {
			new ValidateBatchTask().run(batch, logger);
			fail();
		} catch (Exception e) {
		}
	}
}
