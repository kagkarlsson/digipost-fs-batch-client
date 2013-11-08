package no.bekk.java.dpostbatch.model;

import java.nio.file.Path;

public class SettingsProviderMock implements SettingsProvider {

	private Path rootFolder;

	public SettingsProviderMock(Path rootFolder) {
		this.rootFolder = rootFolder;
	}

	@Override
	public Path getBatchesDirectory() {
		return rootFolder.resolve("batches");
	}

}
