package no.bekk.java.dpostbatch.task;

import java.nio.file.Files;
import java.nio.file.Path;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.transfer.SftpAccount;

public class TransferBatchTask {

	private Batch batch;
	private SettingsProvider settingsProvider;
	private SftpAccount sftpAccount;

	public TransferBatchTask(Batch batch, SettingsProvider settingsProvider, SftpAccount sftpAccount) {
		this.batch = batch;
		this.settingsProvider = settingsProvider;
		this.sftpAccount = sftpAccount;
	}

	public void run() {
		Path zip = batch.getDestinationZip();
		if (!Files.exists(zip)) {
			throw new RuntimeException("Batch-zip does not exist: " + zip);
		}
		
		sftpAccount.upload(zip, zip.getFileName().toString());
		
	}

}
