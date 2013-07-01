package no.bekk.java.dpostbatch.model;

public interface SettingsProvider {

	String getSetting(Setting setting);
	
	public static enum Setting {
		ACTIVE_BATCHES_DIRECTORY, BATCHES_DIRECTORY
		
	}
	
}
