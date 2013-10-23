package no.bekk.java.dpostbatch.task;

import java.util.ArrayList;
import java.util.List;

import no.bekk.java.dpostbatch.model.BatchLogger;

public class MockLogger implements BatchLogger {

	public List<String> logMessages = new ArrayList<>();
	
	@Override
	public void log(String message) {
		logMessages.add(message);
	}


}
