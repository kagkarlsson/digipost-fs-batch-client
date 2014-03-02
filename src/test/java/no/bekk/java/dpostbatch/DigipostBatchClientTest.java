package no.bekk.java.dpostbatch;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchBuilder;
import no.bekk.java.dpostbatch.model.DocumentBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DigipostBatchClientTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	private BatchBuilder batchBuilder;
	private DigipostBatchClientForTest app;
	
	@Before
	public void setUp() throws IOException {
		app = DigipostBatchClientForTest.start(tempFolder.getRoot().toPath());

		batchBuilder = BatchBuilder.newBatch(app.batchDir);
	}
	
	@Test
	public void should_upload_packaged_batch() throws IOException {
		Batch batch = batchBuilder.medBrev(DocumentBuilder.newDocument().build()).build();
		app.checkForNewBatches();
		
		assertUploaded(batch);
	}

	private void assertUploaded(Batch batch) {
		Path uploaded = 
				app.sftpDir
				.resolve("masseutsendelse")
				.resolve(batch.getDestinationZip().getFileName() + ".zip");
		assertTrue(Files.exists(uploaded));
	}
	
}
