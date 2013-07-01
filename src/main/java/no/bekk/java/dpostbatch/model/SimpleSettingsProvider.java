package no.bekk.java.dpostbatch.model;

import java.nio.file.Path;

public class SimpleSettingsProvider implements SettingsProvider {

	private Path rootFolder;

	public SimpleSettingsProvider(Path rootFolder) {
		this.rootFolder = rootFolder;
	}

	@Override
	public String getSetting(Setting setting) {
		switch (setting) {
		case ACTIVE_BATCHES_DIRECTORY:
			Path activeDir = rootFolder.resolve("active");
			return activeDir.toString();

		case BATCHES_DIRECTORY:
			Path batchesDir = rootFolder.resolve("batches");
			return batchesDir.toString();

		default:
			throw new RuntimeException("Unknown setting: " + setting.name());
		}
	}

}
