package no.bekk.java.dpostbatch.task;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.SettingsProvider;

public class MonitorActiveBatchesTask extends TimerTask {

	private static final Logger LOG = LoggerFactory.getLogger(MonitorActiveBatchesTask.class);
	private SettingsProvider settingsProvider;
	private BatchListener batchListener;

	public MonitorActiveBatchesTask(SettingsProvider settingsProvider, BatchListener batchListener) {
		this.settingsProvider = settingsProvider;
		this.batchListener = batchListener;
	}

	@Override
	public void run() {
		Path batchesDirectory = settingsProvider.getBatchesDirectory();
		LOG.debug("Checking for new batches.");
		
		if (!Files.exists(batchesDirectory)) {
			throw new RuntimeException("Directory for batches does not exist: " + batchesDirectory);
		}
		
		try (DirectoryStream<Path> files = Files.newDirectoryStream(batchesDirectory)) {
			for (Path file : files) {
				Batch batch = new Batch(file);
				if (batch.isReady()) {
					LOG.info("Found new ready batch: " + file.getFileName());
					batchListener.newBatch(batch);
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Error when listing active batches.", e);
		}
	}

}
