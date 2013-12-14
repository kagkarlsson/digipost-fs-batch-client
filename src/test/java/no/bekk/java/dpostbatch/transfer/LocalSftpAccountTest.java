package no.bekk.java.dpostbatch.transfer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import no.bekk.java.dpostbatch.sftp.SftpReceipt;

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
		String receiptName = "myjob.resultat.20120101-123456789.zip";
		Files.createFile(receiptFolder.resolve(receiptName));
		Set<SftpReceipt> receipts = localSftpAccount.listReceipts();
		assertEquals(1, receipts.size());
		
		SftpReceipt r = receipts.iterator().next();
		localSftpAccount.download(r.getRemotePath(), receiptDestination);
		
		assertTrue(Files.exists(receiptDestination));
	}

}
