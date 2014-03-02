package no.bekk.java.dpostbatch;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;

import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.task.receipt.CheckForReceiptTask;
import no.bekk.java.dpostbatch.task.receipt.NewReceiptHandler;
import no.bekk.java.dpostbatch.task.send.MonitorActiveBatchesTask;
import no.bekk.java.dpostbatch.task.send.NewBatchHandler;
import no.bekk.java.dpostbatch.transfer.LocalSftpAccount;
import no.bekk.java.dpostbatch.transfer.SftpAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigipostBatchClient implements SettingsProvider {
	
	private static final Logger LOG = LoggerFactory.getLogger(DigipostBatchClient.class);

	private Path batchesDirectory;
	private SftpAccount sftpAccount;
	private int checkForNewBatchesSeconds;
	private int checkForReceiptsSeconds;

	private MonitorActiveBatchesTask checkForNewBatches;
	private CheckForReceiptTask checkForReceipt;
	
	public static void main(String[] args) {
		Path root = Paths.get(".");
		
		DigipostBatchClient.init()
			.withBatchesDirectory(root)
			.withSftpAccount(new LocalSftpAccount(root.resolveSibling("sftp")))
			.checkForNewBatches(5)
			.checkForReceipt(30)
			.start();
	}
	
	private DigipostBatchClient() {}
	
	public static DigipostBatchClient init() { return new DigipostBatchClient(); }

	
	public DigipostBatchClient start() {
	
		NewBatchHandler newBatchHandler = new NewBatchHandler(this, sftpAccount);
		checkForNewBatches = new MonitorActiveBatchesTask(this, newBatchHandler);
		
		NewReceiptHandler newReceiptHandler = new NewReceiptHandler(this, sftpAccount);
		checkForReceipt = new CheckForReceiptTask(this, sftpAccount, newReceiptHandler);
		
		Timer timer = new Timer();
		if (checkForNewBatchesSeconds > 0) {
			timer.schedule(checkForNewBatches, 0, checkForNewBatchesSeconds * 1000);
			LOG.info("Checking {} for new batches every {}s.", 
					batchesDirectory.toAbsolutePath(), checkForNewBatchesSeconds);
		}
		if (checkForReceiptsSeconds > 0) {
			timer.schedule(checkForReceipt, 0, checkForReceiptsSeconds * 1000);
			LOG.info("Checking for new receipts every {}s", checkForReceiptsSeconds);
		}
		
		return this;
	}

	public DigipostBatchClient withBatchesDirectory(Path batchesDirectory) {
		this.batchesDirectory = batchesDirectory;
		return this;
	}
	
	public DigipostBatchClient withSftpAccount(SftpAccount sftpAccount) {
		this.sftpAccount = sftpAccount;
		return this;
	}
	
	public DigipostBatchClient checkForNewBatches(int checkForNewBatchesSeconds) {
		this.checkForNewBatchesSeconds = checkForNewBatchesSeconds;
		return this;
	}
	
	public DigipostBatchClient checkForReceipt(int checkForReceiptsSeconds) {
		this.checkForReceiptsSeconds = checkForReceiptsSeconds;
		return this;
	}
	
	@Override
	public Path getBatchesDirectory() {
		return batchesDirectory;
	}
	
	public void checkForNewBatches() {
		checkForNewBatches.run();
	}
	
	public void checkForReceipts() {
		checkForReceipt.run();
	}

}
