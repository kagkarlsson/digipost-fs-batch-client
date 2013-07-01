package no.bekk.java.dpostbatch.model;

public interface BrevProvider {

	Brev nextBrev();
	BrevProvider reset();
}
