package no.bekk.java.dpostbatch.task.receipt;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.sftp.SftpReceipt;
import no.bekk.java.dpostbatch.transfer.SftpAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckForReceiptTask extends TimerTask {

	private static final Logger LOG = LoggerFactory.getLogger(CheckForReceiptTask.class);
	private SettingsProvider settingsProvider;
	private SftpAccount sftpAccount;
	private NewReceiptHandler newReceiptHandler;

	public CheckForReceiptTask(SettingsProvider settingsProvider, SftpAccount sftpAccount, NewReceiptHandler newReceiptHandler) {
		this.settingsProvider = settingsProvider;
		this.sftpAccount = sftpAccount;
		this.newReceiptHandler = newReceiptHandler;
	}

	@Override
	public void run() {
		settingsProvider.getBatchesDirectory();

		Set<Batch> batches = listBatchesWaitingForReceipt();
		if (batches.isEmpty()) {
			LOG.debug("No batches are waiting for a receipt. Skipping SFTP-check.");
			return;
		}

		Set<SftpReceipt> receipts =  sftpAccount.listReceipts();
		
		for (Batch batch : batches) {
			String id = readJobId(batch);
			for (SftpReceipt receipt : receipts) {
				if (receipt.getOriginalName().equals(id)) {
					newReceiptHandler.handle(batch, receipt);
				}
			}
		}
	}

	private String readJobId(Batch batch) {
		Path file = batch.getAwaitReceiptFile();
		try {
			return new String(Files.readAllBytes(file));
		} catch (IOException e) {
			throw new RuntimeException("Unable to read await-receipt-file: " + file.toAbsolutePath());
		}
	}

	private Set<Batch> listBatchesWaitingForReceipt() {
		try {
			final Set<Batch> batches = new HashSet<>();
			Files.walkFileTree(settingsProvider.getBatchesDirectory(), new HashSet<FileVisitOption>(), 2, 
					new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.getFileName().toString().equals(Batch.AWAIT_RECEIPT_FILE)) {
						batches.add(new Batch(file.getParent()));
						return FileVisitResult.SKIP_SIBLINGS;
					}
					return FileVisitResult.CONTINUE;
				}
				
			});
			return batches;
		} catch (IOException e) {
			throw new RuntimeException("Error when listing batches that await receipt.", e);
		}
	}

}
