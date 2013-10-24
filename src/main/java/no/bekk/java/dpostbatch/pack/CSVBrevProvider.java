package no.bekk.java.dpostbatch.pack;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import au.com.bytecode.opencsv.CSVReader;
import no.bekk.java.dpostbatch.model.Brev;
import no.bekk.java.dpostbatch.model.BrevProvider;

public class CSVBrevProvider implements BrevProvider, Closeable {

	public static final char SEPARATOR = ';';
	private Path lettersFile;
	private CSVReader csvReader;

	public CSVBrevProvider(Path lettersFile) {
		this.lettersFile = lettersFile;
		initReader(lettersFile);
	}

	private void initReader(Path lettersFile) {
		try {
			csvReader = new CSVReader(new FileReader(lettersFile.toFile()), SEPARATOR);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot find file " + lettersFile);
		}
	}

	@Override
	public Brev nextBrev() {
		try {
			String[] line = csvReader.readNext();
			if (line == null) {
				return null;
			}
			return new Brev(get(line, 0), get(line,1), get(line,2), get(line,3), get(line, 4));
		} catch (IOException e) {
			throw new RuntimeException("Error while reading csv " + lettersFile, e);
		}
	}

	private String get(String[] line, int i) {
		return line[i];
	}

	@Override
	public void reset() {
		initReader(lettersFile);
	}

	@Override
	public void close() {
		if (csvReader != null) {
			try {
				csvReader.close();
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
	}

}
