package no.bekk.java.dpostbatch.model;

import java.io.Closeable;

public interface BrevProvider extends Closeable {

	Brev nextBrev();
	void reset();
}
