package no.bekk.java.dpostbatch;

import java.nio.file.Paths;
import java.util.Timer;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.model.SettingsProvider.Setting;
import no.bekk.java.dpostbatch.model.SimpleSettingsProvider;
import no.bekk.java.dpostbatch.task.BatchListener;
import no.bekk.java.dpostbatch.task.MonitorActiveBatchesTask;
import no.bekk.java.dpostbatch.task.PackageBatchTask;
import no.bekk.java.dpostbatch.task.TransferBatchTask;
import no.bekk.java.dpostbatch.transfer.LocalSftpAccount;

public class DigipostBatchClient {
	
	private SettingsProvider settingsProvider;
	private Timer timer;

	public DigipostBatchClient(SettingsProvider settingsProvider) {
		this.settingsProvider = settingsProvider;
		timer = new Timer();
	}

	public static void main(String[] args) {
		// TODO: settingsfile as settingsprovider
		new DigipostBatchClient(new SimpleSettingsProvider(Paths.get("."))).start();
	}

	public void start() {
		timer.schedule(new MonitorActiveBatchesTask(settingsProvider, new BatchListener() {
			
			@Override
			public void newBatch(Batch batch) {
				new PackageBatchTask(batch, settingsProvider).run();
				new TransferBatchTask(batch, settingsProvider, 
						new LocalSftpAccount(Paths.get(settingsProvider.getSetting(Setting.BATCHES_DIRECTORY)).resolveSibling("sftp")))
				.run();
			}
		}), 0, 10000);
		
	}
	
	public void stop() {
		timer.cancel();
	}

}
