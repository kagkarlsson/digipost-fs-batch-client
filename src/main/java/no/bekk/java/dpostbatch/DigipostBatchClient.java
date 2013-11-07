package no.bekk.java.dpostbatch;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.FileBatchLogger;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.model.SimpleSettingsProvider;
import no.bekk.java.dpostbatch.task.BatchListener;
import no.bekk.java.dpostbatch.task.MonitorActiveBatchesTask;
import no.bekk.java.dpostbatch.task.PackageBatchTask;
import no.bekk.java.dpostbatch.task.TransferBatchTask;
import no.bekk.java.dpostbatch.task.ValidateBatchTask;
import no.bekk.java.dpostbatch.transfer.LocalSftpAccount;
import no.bekk.java.dpostbatch.transfer.SftpAccount;

public class DigipostBatchClient {
	
	private SftpAccount sftpAccount;
	private SettingsProvider settingsProvider;

	public DigipostBatchClient(SettingsProvider settingsProvider, SftpAccount sftpAccount) {
		this.settingsProvider = settingsProvider;
		this.sftpAccount = sftpAccount;
	}

	public static void main(String[] args) {
		// TODO: settingsfile as settingsprovider
		SettingsProvider settingsProvider = new SimpleSettingsProvider(Paths.get("."));
		SftpAccount sftpAccount = new LocalSftpAccount(Paths.get(settingsProvider.getBatchesDirectory()).resolveSibling("sftp"));
		
		DigipostBatchClient client = new DigipostBatchClient(settingsProvider, sftpAccount);
		
		Timer timer = new Timer();
		timer.schedule(client.processNewBatches(), 0, 5000);
	}

	public TimerTask processNewBatches() {
		return new MonitorActiveBatchesTask(settingsProvider, new BatchListener() {

			@Override
			public void newBatch(Batch batch) {
				
				Path logfile = batch.getLogFile();
				try (FileBatchLogger logger = new FileBatchLogger(new FileWriter(logfile.toFile()))) {
					
					try {
						logger.log("Processing new batch " + batch.getName());
						Files.delete(batch.getReadyFile());
						new ValidateBatchTask(batch, settingsProvider, logger).run();
						new PackageBatchTask(batch, settingsProvider, logger).run();
						new TransferBatchTask(batch, settingsProvider, logger, sftpAccount).run();
						// TODO: wait for receipt through another timer

					} catch (Exception e) {
						StringWriter stacktrace = new StringWriter();
						e.printStackTrace(new PrintWriter(stacktrace));
						logger.log("Failed to process batch" + stacktrace.toString());
					}
				} catch (Exception e) {
					System.err.println("Unable to open batch-logger for batch: " + batch.toString());
					e.printStackTrace(System.err);
				} 
			}
		});
		
	}

}
