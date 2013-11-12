package no.bekk.java.dpostbatch.model;

import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleSettingsProvider implements SettingsProvider {

	private Path rootFolder;

	public SimpleSettingsProvider(Path rootFolder) {
		this.rootFolder = rootFolder;
	}

	@Override
	public Path getBatchesDirectory() {
		Path batchesDirectory = rootFolder.resolve("batches");
		if (!Files.exists(batchesDirectory)) {
			throw new RuntimeException("Directory for batches does not exist: " + batchesDirectory);
		}

		return batchesDirectory;
	}

}
