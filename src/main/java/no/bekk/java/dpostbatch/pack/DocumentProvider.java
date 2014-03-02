package no.bekk.java.dpostbatch.pack;

import java.io.Closeable;

import no.bekk.java.dpostbatch.model.Document;

public interface DocumentProvider extends Closeable{

	Document next();

	void reset();

}
