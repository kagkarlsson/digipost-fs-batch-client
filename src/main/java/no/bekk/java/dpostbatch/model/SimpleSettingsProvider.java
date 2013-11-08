package no.bekk.java.dpostbatch.model;

import java.nio.file.Path;

public class SimpleSettingsProvider implements SettingsProvider {

	private Path rootFolder;

	public SimpleSettingsProvider(Path rootFolder) {
		this.rootFolder = rootFolder;
	}

	@Override
	public Path getBatchesDirectory() {
		return rootFolder.resolve("batches");
	}

}
