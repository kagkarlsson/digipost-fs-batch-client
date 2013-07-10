package no.bekk.java.dpostbatch.transfer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LocalSftpAccountTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	private LocalSftpAccount localSftpAccount;
	private Path receiptFolder;
	private Path uploadFolder;
	
	@Before
	public void setUp() throws IOException {
		localSftpAccount = new LocalSftpAccount(tempFolder.newFolder().toPath());
		uploadFolder = localSftpAccount.masseutsendelse;
		receiptFolder = uploadFolder.resolve("kvittering");
		Files.createDirectories(receiptFolder);
	}
	
	@Test
	public void shouldUploadZip() throws IOException {
		String filename = "batch1.zip";
		Path zip = tempFolder.newFile(filename).toPath();
		
		localSftpAccount.upload(zip, filename);
		assertTrue(Files.exists(uploadFolder.resolve(filename)));
	}
	
	@Test
	public void shouldDownloadReceipt() throws IOException {
		Path receiptDestination = tempFolder.getRoot().toPath().resolve("receipt.zip");
		Files.createFile(receiptFolder.resolve("batch1-1234.zip"));
		boolean result = localSftpAccount.downloadReceipt("batch1", receiptDestination);
		
		assertTrue(result);
		assertTrue(Files.exists(receiptDestination));
	}

	@Test
	public void shouldNotDownloadReceiptIfNotExisting() throws IOException {
		Path receiptDestination = tempFolder.getRoot().toPath().resolve("receipt.zip");
		boolean result = localSftpAccount.downloadReceipt("batch1", receiptDestination);
		
		assertFalse(result);
		assertFalse(Files.exists(receiptDestination));
	}
	
}
