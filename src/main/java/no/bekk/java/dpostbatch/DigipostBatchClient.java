package no.bekk.java.dpostbatch;

import java.nio.file.Paths;
import java.util.Timer;

import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.model.SimpleSettingsProvider;
import no.bekk.java.dpostbatch.task.receipt.CheckForReceiptTask;
import no.bekk.java.dpostbatch.task.receipt.NewReceiptHandler;
import no.bekk.java.dpostbatch.task.send.MonitorActiveBatchesTask;
import no.bekk.java.dpostbatch.task.send.NewBatchHandler;
import no.bekk.java.dpostbatch.transfer.LocalSftpAccount;
import no.bekk.java.dpostbatch.transfer.SftpAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigipostBatchClient {
	
	private static final Logger LOG = LoggerFactory.getLogger(DigipostBatchClient.class);
	
	public static void main(String[] args) {
		// TODO: settingsfile as settingsprovider
		SettingsProvider settingsProvider = new SimpleSettingsProvider(Paths.get("."));

		SftpAccount sftpAccount = new LocalSftpAccount(settingsProvider.getBatchesDirectory().resolveSibling("sftp"));
		
		NewBatchHandler newBatchHandler = new NewBatchHandler(settingsProvider, sftpAccount);
		MonitorActiveBatchesTask checkForNewBatches = 
				new MonitorActiveBatchesTask(settingsProvider, newBatchHandler);
		
		NewReceiptHandler newReceiptHandler = new NewReceiptHandler(settingsProvider, sftpAccount);
		CheckForReceiptTask checkForReceipt = 
				new CheckForReceiptTask(settingsProvider, sftpAccount, newReceiptHandler);
		
		Timer timer = new Timer();
		timer.schedule(checkForNewBatches, 0, 5_000);
		timer.schedule(checkForReceipt, 0, 30_000);
		LOG.info("Started monitoring " + settingsProvider.getBatchesDirectory().toAbsolutePath());
	}

}
