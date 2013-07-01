package no.bekk.java.dpostbatch.task;

import no.bekk.java.dpostbatch.model.Batch;

public interface BatchListener {

	void newBatch(Batch batch);

}
