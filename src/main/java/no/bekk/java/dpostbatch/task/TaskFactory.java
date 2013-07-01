package no.bekk.java.dpostbatch.task;

import no.bekk.java.dpostbatch.model.Batch;


public class TaskFactory {

	public PackageBatchTask newPackageBatchTask(Batch batch) {
		return new PackageBatchTask(batch);
	}

}
