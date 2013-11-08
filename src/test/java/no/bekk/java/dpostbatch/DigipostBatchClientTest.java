package no.bekk.java.dpostbatch;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchBuilder;
import no.bekk.java.dpostbatch.model.BrevBuilder;
import no.bekk.java.dpostbatch.model.SimpleSettingsProvider;
import no.bekk.java.dpostbatch.task.send.MonitorActiveBatchesTask;
import no.bekk.java.dpostbatch.task.send.NewBatchHandler;
import no.bekk.java.dpostbatch.transfer.LocalSftpAccount;
import no.bekk.java.dpostbatch.transfer.SftpAccount;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DigipostBatchClientTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	private Path rootFolder;
	private Path sftpFolder;
	private Path batchFolder;
	private BatchBuilder batchBuilder;
	private SimpleSettingsProvider settingsProvider;
	private MonitorActiveBatchesTask checkForNewBatches;
	
	@Before
	public void setUp() throws IOException {
		rootFolder = tempFolder.getRoot().toPath();
		batchFolder = tempFolder.newFolder("batches").toPath();
		sftpFolder = tempFolder.newFolder("sftp").toPath();
		
		settingsProvider = new SimpleSettingsProvider(rootFolder);
		SftpAccount sftpAccount = new LocalSftpAccount(settingsProvider.getBatchesDirectory().resolveSibling("sftp"));
		
		NewBatchHandler newBatchHandler = new NewBatchHandler(settingsProvider, sftpAccount);
		checkForNewBatches = new MonitorActiveBatchesTask(settingsProvider, newBatchHandler);

		batchBuilder = BatchBuilder.newBatch(batchFolder);
	}
	
	@Test
	public void should_upload_packaged_batch() throws IOException {
		Batch batch = batchBuilder.medBrev(BrevBuilder.newBrev().build()).build();
		checkForNewBatches.run();
		
		assertUploaded(batch);
	}

	private void assertUploaded(Batch batch) {
		Path uploaded = 
				sftpFolder
				.resolve("masseutsendelse")
				.resolve(batch.getDestinationZip().getFileName() + ".zip");
		assertTrue(Files.exists(uploaded));
	}
	
}
