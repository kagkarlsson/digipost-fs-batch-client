package no.bekk.java.dpostbatch.model;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBatchLogger implements AutoCloseable, BatchLogger {
	
	private static final Logger LOG = LoggerFactory.getLogger(FileBatchLogger.class);

	private Writer writer;

	public FileBatchLogger(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void log(String message) {
		try {
			writer.write(message);
			writer.write("\n");
			LOG.info(message);
		} catch (IOException e) {
			System.err.println("Unable to log: " + message);
			LOG.error("Unable to log: {}", message);
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
