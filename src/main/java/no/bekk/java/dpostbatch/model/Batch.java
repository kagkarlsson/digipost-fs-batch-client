package no.bekk.java.dpostbatch.model;

import java.nio.file.Files;
import java.nio.file.Path;

public class Batch {

	public static final String BATCH_READY_FILE = "batch.ready";
	private static final String LETTERS_FILE = "letters.csv";
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
		return Files.exists(batchDirectory.resolve(BATCH_READY_FILE));
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


	/*
	public class ActiveBatch {

		private Path file;

		public ActiveBatch(Path file) {
			this.file = file;
		}

		public String getName() {
			return Files.getNameWithoutExtension(file.getFileName().toString());
		}

		public boolean isReadyToSend() {
			return "new".equals(Files.getFileExtension(file.getFileName().toString()));
		}
		
		@Override
		public String toString() {
			return file.toString();
		}

	}
	*/
}
