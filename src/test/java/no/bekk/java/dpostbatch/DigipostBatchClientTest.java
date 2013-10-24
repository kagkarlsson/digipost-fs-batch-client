package no.bekk.java.dpostbatch;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchBuilder;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.model.SimpleSettingsProvider;
import no.bekk.java.dpostbatch.transfer.LocalSftpAccount;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DigipostBatchClientTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	private Path rootFolder;
	private DigipostBatchClient client;
	private Path sftpFolder;
	
	@Before
	public void setUp() throws IOException {
		rootFolder = tempFolder.getRoot().toPath();
		tempFolder.newFolder("batches");
		sftpFolder = tempFolder.newFolder("sftp").toPath();
		client = new DigipostBatchClient(
				new SimpleSettingsProvider(rootFolder), 
				new LocalSftpAccount(sftpFolder));
	}
	
	@Test
	public void test() throws IOException {
		Batch batch = BatchBuilder.newBatch(rootFolder.resolve("batch1")).build();
		client.processNewBatches().run();
		
		// TODO: route batch-logger for tests to sysout
		assertUploaded(batch);
	}

	private void assertUploaded(Batch batch) {
		assertTrue(Files.exists(sftpFolder.resolve(Batch.BATCH_READY_FILE)));
	}
	
}
