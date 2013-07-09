package no.bekk.java.dpostbatch.model;

public interface BrevProvider {

	Brev nextBrev();
	void reset();
}
