package no.bekk.java.dpostbatch.task;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchLogger;
import no.bekk.java.dpostbatch.model.SettingsProvider;

public abstract class BatchTask {

	protected Batch batch;
	protected SettingsProvider settingsProvider;
	protected BatchLogger logger;

	public BatchTask(SettingsProvider settingsProvider) {
		this.batch = batch;
		this.settingsProvider = settingsProvider;
		this.logger = logger;
	}

}
