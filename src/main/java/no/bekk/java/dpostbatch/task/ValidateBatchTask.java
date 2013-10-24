package no.bekk.java.dpostbatch.task;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import com.google.common.base.CharMatcher;

import au.com.bytecode.opencsv.CSVReader;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchLogger;
import no.bekk.java.dpostbatch.model.SettingsProvider;

public class ValidateBatchTask extends BatchTask {

	public ValidateBatchTask(Batch batch, SettingsProvider settingsProvider, BatchLogger logger) {
		super(batch, settingsProvider, logger);
	}

	public void run() {
		boolean validationSucceeded = true;
		if (!Files.exists(batch.getSettingsFile())) {
			logger.log("Cannot find settingsfile for batch " + batch.getSettingsFile());
			validationSucceeded = false;
		}
		
		if (!Files.exists(batch.getLettersCsv())) {
			logger.log("Cannot find letters-file for batch " + batch.getLettersCsv());
			validationSucceeded = false;
		} else {
			boolean letterValidationErrors = validateLetters();
			if (letterValidationErrors) {
				logger.log("Validation of letter-file failed.");
				validationSucceeded = false;
			}
		}
		if (!validationSucceeded) {
			throw new RuntimeException("Validation failed. See log-file for details.");
		}
	}

	private boolean validateLetters() {
		boolean hasErrors = false;
		try (BufferedReader reader = new BufferedReader(new FileReader(batch.getLettersCsv().toFile()))) {
			int index = 1;
			String line;
			while((line = reader.readLine()) != null) {
				hasErrors = hasErrors || validateLine(line, index);
				index++;
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to read letters file.", e);
		}
		return !hasErrors;
	}

	private boolean validateLine(String line, int index) {
		int occurences = CharMatcher.is(',').countIn(line);
		if (occurences != 4) {
			logger.log("Line " + index + " has wrong number of columns: " + occurences);
			return false;
		}
		return true;
	}

}
