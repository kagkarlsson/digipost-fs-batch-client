package no.bekk.java.dpostbatch.task;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchBuilder;
import no.bekk.java.dpostbatch.model.SettingsProvider.Setting;
import no.bekk.java.dpostbatch.model.SettingsProviderMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MonitorActiveBatchesTaskTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	@Mock
	private BatchListener batchListener;
	private SettingsProviderMock settingsProvider;
	private MonitorActiveBatchesTask task;
	private Path batchesDir;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		settingsProvider = new SettingsProviderMock(tempFolder.getRoot().toPath());
		batchesDir = Paths.get(settingsProvider.getSetting(Setting.BATCHES_DIRECTORY));
		task = new MonitorActiveBatchesTask(settingsProvider, batchListener);
		
	}

	@Test
	public void shouldPackageBatchIfNewAndReady() throws IOException {
		BatchBuilder.newBatch(batchesDir).build();
		task.run();
		verify(batchListener, times(1)).newBatch((Batch) any());
	}

	@Test
	public void shouldDoNothingIfNoBatchDir() throws IOException {
		Files.createDirectories(batchesDir);
		task.run();
		verifyZeroInteractions(batchListener);
	}

	@Test
	public void shouldDoNothingIfNoReadyBatches() throws IOException {
		BatchBuilder.newBatch(batchesDir).setNew().build();
		task.run();
		verifyZeroInteractions(batchListener);
	}

	@Test
	public void shouldFailIfBatchDirectoryDoesNotExist() throws IOException {
		try {
			task.run();
			fail();
		} catch (RuntimeException e) {
			assertThat(e.getMessage(), containsString("Directory for batches"));
		}
	}
}
