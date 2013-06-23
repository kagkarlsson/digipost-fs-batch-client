package no.bekk.java.sax;

import java.nio.file.Files;
import java.nio.file.Path;

public class Batch {

	public static final String BATCH_READY_FILE = "batch_ready";
	private static final String LETTERS_FILE = "letters.csv";
	private Path batchDirectory;

	public Batch(Path batchDirectory) {
		assertExists(batchDirectory);
		this.batchDirectory = batchDirectory;
	}

	private void assertExists(Path dir) {
		if (!Files.exists(dir)) {
			throw new RuntimeException("Batch-directory " + dir + " does not exist.");
		}
	}

	public boolean isReady() {
		return Files.exists(batchDirectory.resolve(BATCH_READY_FILE));
	}

	public boolean hasNecessaryFiles() {
		return Files.exists(batchDirectory.resolve(LETTERS_FILE));
	}

}
