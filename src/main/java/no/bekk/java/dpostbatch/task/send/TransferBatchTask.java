package no.bekk.java.dpostbatch.task.send;

import java.nio.file.Files;
import java.nio.file.Path;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchLogger;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.transfer.SftpAccount;

public class TransferBatchTask {

	private SftpAccount sftpAccount;

	public TransferBatchTask(SettingsProvider settingsProvider, SftpAccount sftpAccount) {
		this.sftpAccount = sftpAccount;
	}

	public void run(Batch batch, BatchLogger logger) {
		logger.log("Transfer batch.");
		Path zip = batch.getDestinationZip();
		if (!Files.exists(zip)) {
			throw new RuntimeException("Batch-zip does not exist: " + zip);
		}
		
		sftpAccount.upload(zip, zip.getFileName().toString() + ".zip");
		logger.log("Transfer ok.");
	}

}
