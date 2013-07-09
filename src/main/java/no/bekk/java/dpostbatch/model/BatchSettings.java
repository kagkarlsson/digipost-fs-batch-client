package no.bekk.java.dpostbatch.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;

public class BatchSettings {
	
	public enum BatchSetting {
		SENDER_ID("sender.id"),
		BATCH_ID("batch.id"),
		BATCH_NAME("batch.name"),
		AUTOCONFIRM("batch.autoconfirm");
		
		private String key;
		
		BatchSetting(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
	
	public final String avsenderId;
	public final String jobbId;
	public final String jobbNavn;
	public final String autogodkjenn;

	public BatchSettings(String avsenderId, String jobbId, String jobbNavn, String autogodkjenn) {
		this.avsenderId = avsenderId;
		this.jobbId = jobbId;
		this.jobbNavn = jobbNavn;
		this.autogodkjenn = autogodkjenn;
	}

	public BatchSettings(Path settingsFile) {
		Properties settings = new Properties();
		try (InputStream settingsStream = new FileInputStream(settingsFile.toFile())) {
			settings.load(settingsStream);
			avsenderId = settings.getProperty("sender.id");
			jobbId = settings.getProperty("batch.id", UUID.randomUUID().toString());
			jobbNavn = settings.getProperty("batch.navn", jobbId);
			autogodkjenn = settings.getProperty("batch.autoconfirm", "false");
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot find settings file " + settingsFile);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read settings file " + settingsFile);
		}
	}
	
}
