package no.bekk.java.dpostbatch.task.receipt;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.sftp.SftpReceipt;
import no.bekk.java.dpostbatch.transfer.SftpAccount;

public class NewReceiptHandler {

	private SettingsProvider settingsProvider;
	private SftpAccount sftpAccount;

	public NewReceiptHandler(SettingsProvider settingsProvider, SftpAccount sftpAccount) {
		this.settingsProvider = settingsProvider;
		this.sftpAccount = sftpAccount;
	}

	public void handle(Batch batch, SftpReceipt expectedReceipt) {
		sftpAccount.download(expectedReceipt.getRemotePath(), batch.getReceiptPath());
	}

}
