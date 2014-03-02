package no.bekk.java.dpostbatch.task.receipt;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchSettings;
import no.bekk.java.dpostbatch.model.FileBatchLogger;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.receipt.ParseReceiptTask;
import no.bekk.java.dpostbatch.sftp.SftpReceipt;
import no.bekk.java.dpostbatch.transfer.SftpAccount;

public class NewReceiptHandler {

	private SettingsProvider settingsProvider;
	private SftpAccount sftpAccount;
	private ParseReceiptTask parseReceiptTask;

	public NewReceiptHandler(SettingsProvider settingsProvider, SftpAccount sftpAccount) {
		this.settingsProvider = settingsProvider;
		this.sftpAccount = sftpAccount;
		this.parseReceiptTask = new ParseReceiptTask();
	}

	public void handle(Batch batch, SftpReceipt expectedReceipt) {
		
		Path logfile = batch.getLogFile();
		try (FileBatchLogger logger = new FileBatchLogger(new FileWriter(logfile.toFile()))) {
			
			try {
				logger.log("Detected receipt for batch " + batch.getName());
				Files.delete(batch.getAwaitReceiptFile());
				//download
				sftpAccount.download(expectedReceipt.getRemotePath(), batch.getReceiptPath());
				//parse receipt
				parseReceiptTask.run(batch, logger);
			} catch (Exception e) {
				StringWriter stacktrace = new StringWriter();
				e.printStackTrace(new PrintWriter(stacktrace));
				logger.log("Failed to process receipt\n" + stacktrace.toString());
			}
		} catch (Exception e) {
			System.err.println("Unable to open batch-logger for batch: " + batch.toString());
			e.printStackTrace(System.err);
		}
	}

}
