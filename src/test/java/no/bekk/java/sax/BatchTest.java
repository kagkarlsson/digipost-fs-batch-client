package no.bekk.java.sax;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BatchTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void readyToSendWhenDoneFileExists() throws IOException {
		tempFolder.newFile("letters.csv");
		tempFolder.newFile("file.pdf");
		tempFolder.newFile(Batch.BATCH_READY_FILE);
		assertTrue(new Batch(tempFolder.getRoot().toPath()).isReady());
		assertTrue(new Batch(tempFolder.getRoot().toPath()).hasNecessaryFiles());
	}

	@Test
	public void notReadyToSendWhenDoneFileDoesNotExists() throws IOException {
		tempFolder.newFile("letters.csv");
		tempFolder.newFile("file.pdf");
		assertFalse(new Batch(tempFolder.getRoot().toPath()).isReady());
		assertTrue(new Batch(tempFolder.getRoot().toPath()).hasNecessaryFiles());
	}
	
	@Test
	public void lettersIsNecessary() throws IOException {
		tempFolder.newFile("file.pdf");
		tempFolder.newFile(Batch.BATCH_READY_FILE);
		assertTrue(new Batch(tempFolder.getRoot().toPath()).isReady());
		assertFalse(new Batch(tempFolder.getRoot().toPath()).hasNecessaryFiles());
	}
	
}
