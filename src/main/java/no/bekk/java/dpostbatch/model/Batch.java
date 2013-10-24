package no.bekk.java.dpostbatch.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Batch {
	
	public static final String BATCH_READY_FILE = "batch.ready";
	public static final String BATCH_DESTINATION_ZIP = "batch.zip";
	private static final String BATCH_SETTINGS_FILE = "batch.properties";
	private static final String LETTERS_FILE = "letters.csv";
	private static final String BATCH_DESTINATION_XML = "masseutsendelse.xml";
	private static final String  LOG_FILE = "batch.log";
	private Path batchDirectory;

	public Batch(Path batchDirectory) {
		this.batchDirectory = batchDirectory;
	}

	private void assertExists(Path dir) {
		if (!exists()) {
			throw new RuntimeException("Batch-directory " + dir + " does not exist.");
		}
	}

	public boolean isReady() {
		assertExists(batchDirectory); 
		return Files.exists(getReadyFile());
	}

	public boolean hasNecessaryFiles() {
		assertExists(batchDirectory);
		return Files.exists(batchDirectory.resolve(LETTERS_FILE));
	}

	public boolean exists() {
		return Files.exists(batchDirectory);
	}
	
	@Override
	public String toString() {
		return batchDirectory.toString();
	}

	public Path getSettingsFile() {
		return batchDirectory.resolve(BATCH_SETTINGS_FILE);
	}
	
	public Path getReadyFile() {
		return batchDirectory.resolve(BATCH_READY_FILE);
	}

	public Path getDestinationXml() {
		return batchDirectory.resolve(BATCH_DESTINATION_XML);
	}

	public Path getLettersCsv() {
		return batchDirectory.resolve(LETTERS_FILE);
	}

	public Path getDirectory() {
		return batchDirectory;
	}

	public Path getDestinationZip() {
		return batchDirectory.resolve(BATCH_DESTINATION_ZIP);
	}

	public void setSent() {
		deleteReadyFile();
	}

	private void deleteReadyFile() {
		try {
			Files.delete(getReadyFile());
		} catch (IOException e) {
			// Possibly allow this with a warning
			throw new RuntimeException("Unable to delete ready-file " + getReadyFile(), e);
		}
	}

	public Path getLogFile() {
		return batchDirectory.resolve(LOG_FILE);
	}

	public boolean hasFailed() {
		return false;
	}

}
