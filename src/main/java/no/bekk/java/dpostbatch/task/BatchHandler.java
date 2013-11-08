package no.bekk.java.dpostbatch.task;

import no.bekk.java.dpostbatch.model.Batch;

public interface BatchHandler {

	void handle(Batch batch);

}
