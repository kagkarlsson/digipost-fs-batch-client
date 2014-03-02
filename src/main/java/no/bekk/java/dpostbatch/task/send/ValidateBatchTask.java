package no.bekk.java.dpostbatch.task.send;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchLogger;
import no.bekk.java.dpostbatch.pack.CsvDocumentProvider;

import com.google.common.base.CharMatcher;

public class ValidateBatchTask {

	public void run(Batch batch, BatchLogger logger) {
		logger.log("Validating batch.");
		Validation validation = new Validation();
		if (!Files.exists(batch.getSettingsFile())) {
			validation.addError("Cannot find settingsfile for batch " + batch.getSettingsFile());
		}
		
		if (!Files.exists(batch.getLettersCsv())) {
			validation.addError("Cannot find letters-file for batch " + batch.getLettersCsv());
		} else {
			validateLetters(batch, validation);
		}
		
		if (validation.hasErrors()) {
			validation.writeToLog(logger);
			throw new RuntimeException("Validation failed. See log-file for details.");
		}
		logger.log("Validation ok.");
	}

	private void validateLetters(Batch batch, Validation validation) {
		try (BufferedReader reader = new BufferedReader(new FileReader(batch.getLettersCsv().toFile()))) {
			int index = 1;
			String line;
			while((line = reader.readLine()) != null) {
				validateLine(line, index, validation);
				index++;
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to read letters file.", e);
		}
	}

	private void validateLine(String line, int index, Validation validation) {
		int occurences = CharMatcher.is(CsvDocumentProvider.SEPARATOR).countIn(line);
		if (occurences != 4) {
			validation.addError("Line " + index + " has wrong number of columns: " + occurences);
		}
	}

	public class Validation {
		private final List<String> errors = new ArrayList<>();

		public void addError(String error) {
			errors.add(error);
		}

		public boolean hasErrors() {
			return !errors.isEmpty();
		}

		public void writeToLog(BatchLogger logger) {
			for (String error : errors) {
				logger.log(error);
			}
		}
	}
}
