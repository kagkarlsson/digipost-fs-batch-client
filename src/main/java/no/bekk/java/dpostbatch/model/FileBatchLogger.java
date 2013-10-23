package no.bekk.java.dpostbatch.model;

import java.io.IOException;
import java.io.Writer;

public class FileBatchLogger implements AutoCloseable, BatchLogger {

	private Writer writer;

	public FileBatchLogger(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void log(String message) {
		try {
			writer.write(message);
			writer.write("\n");
		} catch (IOException e) {
			System.err.println("Unable to log: " + message);
		}
	}

	@Override
	public void close() throws Exception {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

}
