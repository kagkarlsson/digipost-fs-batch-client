package no.bekk.java.dpostbatch;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.FileBatchLogger;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.model.SettingsProvider.Setting;
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
	private Timer timer;

	public DigipostBatchClient(SettingsProvider settingsProvider, SftpAccount sftpAccount) {
		this.settingsProvider = settingsProvider;
		this.sftpAccount = sftpAccount;
		timer = new Timer();
	}

	public static void main(String[] args) {
		// TODO: settingsfile as settingsprovider
		SettingsProvider settingsProvider = new SimpleSettingsProvider(Paths.get("."));
		SftpAccount sftpAccount = new LocalSftpAccount(Paths.get(settingsProvider.getSetting(Setting.BATCHES_DIRECTORY)).resolveSibling("sftp"));
		new DigipostBatchClient(settingsProvider, sftpAccount).start();
	}

	public void start() {
		timer.schedule(new MonitorActiveBatchesTask(settingsProvider, new BatchListener() {

			@Override
			public void newBatch(Batch batch) {
				
				Path logfile = batch.getLogFile();
				try (FileBatchLogger logger = new FileBatchLogger(new FileWriter(logfile.toFile()))) {
					
					try {
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
		}), 0, 5000);
		
	}
	
	public void stop() {
		timer.cancel();
	}

}
