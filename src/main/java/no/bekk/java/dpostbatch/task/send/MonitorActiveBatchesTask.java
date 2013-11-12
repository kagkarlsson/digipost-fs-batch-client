package no.bekk.java.dpostbatch.task.send;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TimerTask;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.task.BatchHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorActiveBatchesTask extends TimerTask {

	private static final Logger LOG = LoggerFactory.getLogger(MonitorActiveBatchesTask.class);
	private SettingsProvider settingsProvider;
	private BatchHandler batchHandler;

	public MonitorActiveBatchesTask(SettingsProvider settingsProvider, BatchHandler batchHandler) {
		this.settingsProvider = settingsProvider;
		this.batchHandler = batchHandler;
	}

	@Override
	public void run() {
		LOG.debug("Checking for new batches.");
		Path batchesDirectory = settingsProvider.getBatchesDirectory();
		
		try (DirectoryStream<Path> files = Files.newDirectoryStream(batchesDirectory)) {
			for (Path file : files) {
				Batch batch = new Batch(file);
				if (batch.isReady()) {
					LOG.info("Found new ready batch: " + file.getFileName());
					batchHandler.handle(batch);
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Error when listing active batches.", e);
		}
	}

}
