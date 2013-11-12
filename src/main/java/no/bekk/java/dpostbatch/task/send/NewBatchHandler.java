package no.bekk.java.dpostbatch.task.send;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchSettings;
import no.bekk.java.dpostbatch.model.FileBatchLogger;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.task.BatchHandler;
import no.bekk.java.dpostbatch.transfer.SftpAccount;

public class NewBatchHandler implements BatchHandler {

	private ValidateBatchTask validateBatchTask;
	private PackageBatchTask packageBatchTask;
	private TransferBatchTask transferBatchTask;

	public NewBatchHandler(SettingsProvider settingsProvider, SftpAccount sftpAccount) {
		validateBatchTask = new ValidateBatchTask();
		packageBatchTask = new PackageBatchTask();
		transferBatchTask = new TransferBatchTask(settingsProvider, sftpAccount);
	}

	@Override
	public void handle(Batch batch) {
		
		Path logfile = batch.getLogFile();
		try (FileBatchLogger logger = new FileBatchLogger(new FileWriter(logfile.toFile()))) {
			
			try {
				logger.log("Processing new batch " + batch.getName());
				Files.delete(batch.getReadyFile());
				validateBatchTask.run(batch, logger);
				BatchSettings batchSettings = packageBatchTask.run(batch, logger);
				transferBatchTask.run(batch, logger);

				// Change to await-receipt state
				Files.write(batch.getAwaitReceiptFile(), batchSettings.jobbId.getBytes());

			} catch (Exception e) {
				StringWriter stacktrace = new StringWriter();
				e.printStackTrace(new PrintWriter(stacktrace));
				logger.log("Failed to process batch\n" + stacktrace.toString());
			}
		} catch (Exception e) {
			System.err.println("Unable to open batch-logger for batch: " + batch.toString());
			e.printStackTrace(System.err);
		} 
	}


}
