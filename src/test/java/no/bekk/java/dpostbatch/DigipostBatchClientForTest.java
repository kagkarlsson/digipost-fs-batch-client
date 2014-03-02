package no.bekk.java.dpostbatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import no.bekk.java.dpostbatch.transfer.LocalSftpAccount;

public class DigipostBatchClientForTest {

	public final DigipostBatchClient client;
	public final Path batchDir;
	public final Path sftpDir;

	private DigipostBatchClientForTest(DigipostBatchClient digipostBatchClient, Path batchDir, Path sftpDir) {
		this.client = digipostBatchClient;
		this.batchDir = batchDir;
		this.sftpDir = sftpDir;}
	
	public static DigipostBatchClientForTest start(Path tempDirectory) throws IOException {
		Path batchDir = Files.createDirectory(tempDirectory.resolve("batches"));
		Path sftpDir = Files.createDirectory(tempDirectory.resolve("sftp"));
		
		DigipostBatchClient digipostBatchClient = DigipostBatchClient.init()
			.withBatchesDirectory(batchDir)
			.withSftpAccount(new LocalSftpAccount(sftpDir))
			.checkForNewBatches(5)
			.checkForReceipt(30)
			.start();

		return new DigipostBatchClientForTest(digipostBatchClient, batchDir, sftpDir);

	}

	public void checkForNewBatches() {
		client.checkForNewBatches();
	}
	
	
}
