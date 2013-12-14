package no.bekk.java.dpostbatch.task;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchBuilder;
import no.bekk.java.dpostbatch.model.SettingsProviderMock;
import no.bekk.java.dpostbatch.task.send.MonitorActiveBatchesTask;

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
	private BatchHandler batchListener;
	private SettingsProviderMock settingsProvider;
	private MonitorActiveBatchesTask task;
	private Path batchesDir;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		settingsProvider = new SettingsProviderMock(tempFolder.getRoot().toPath());
		batchesDir = settingsProvider.getBatchesDirectory();
		task = new MonitorActiveBatchesTask(settingsProvider, batchListener);
	}

	@Test
	public void shouldPackageBatchIfNewAndReady() throws IOException {
		BatchBuilder.newBatch(batchesDir).build();
		task.run();
		verify(batchListener, times(1)).handle((Batch) any());
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

}
