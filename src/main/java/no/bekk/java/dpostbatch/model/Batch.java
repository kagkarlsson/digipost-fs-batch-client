package no.bekk.java.dpostbatch.model;

import java.nio.file.Files;
import java.nio.file.Path;

public class Batch {
	
	public static final String BATCH_READY_FILE = "batch_ready";
	public static final String AWAIT_RECEIPT_FILE = "await_receipt";
	public static final String BATCH_DESTINATION_ZIP = "batch.zip";
	private static final String BATCH_SETTINGS_FILE = "batch.properties";
	private static final String LETTERS_FILE = "letters.csv";
	private static final String BATCH_DESTINATION_XML = "masseutsendelse.xml";
	private static final String  LOG_FILE = "batch.log";
	private static final String BATCH_RECEIPT = "receipt.zip";
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

	public Path getLogFile() {
		return batchDirectory.resolve(LOG_FILE);
	}

	public boolean hasFailed() {
		return false;
	}

	public String getName() {
		return batchDirectory.getFileName().toString();
	}

	public Path getReceiptPath() {
		return batchDirectory.resolve(BATCH_RECEIPT);
	}

	public Path getAwaitReceiptFile() {
		return batchDirectory.resolve(AWAIT_RECEIPT_FILE);
	}

}
