package no.bekk.java.dpostbatch.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.bekk.java.dpostbatch.model.BatchLogger;

public class MockLogger implements BatchLogger {
	
	private static final Logger LOG = LoggerFactory.getLogger(MockLogger.class);

	public List<String> logMessages = new ArrayList<>();
	
	@Override
	public void log(String message) {
		logMessages.add(message);
		LOG.info(message);
	}


}
