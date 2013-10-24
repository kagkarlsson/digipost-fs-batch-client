package no.bekk.java.dpostbatch.task;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TimerTask;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.SettingsProvider;

public class MonitorActiveBatchesTask extends TimerTask {

	private SettingsProvider settingsProvider;
	private BatchListener batchListener;

	public MonitorActiveBatchesTask(SettingsProvider settingsProvider, BatchListener batchListener) {
		this.settingsProvider = settingsProvider;
		this.batchListener = batchListener;
	}

	@Override
	public void run() {
		Path batchesDirectory = Paths.get(settingsProvider.getBatchesDirectory());
		
		if (!Files.exists(batchesDirectory)) {
			throw new RuntimeException("Directory for batches does not exist: " + batchesDirectory);
		}
		
		try (DirectoryStream<Path> files = Files.newDirectoryStream(batchesDirectory)) {
			for (Path file : files) {
				Batch batch = new Batch(file);
				if (batch.isReady()) {
					batchListener.newBatch(batch);
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Error when listing active batches.", e);
		}
	}

}
