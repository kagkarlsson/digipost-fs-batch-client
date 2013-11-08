package no.bekk.java.dpostbatch.task;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.stream.XMLStreamException;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchLogger;
import no.bekk.java.dpostbatch.model.BatchSettings;
import no.bekk.java.dpostbatch.model.Brev;
import no.bekk.java.dpostbatch.model.BrevProvider;
import no.bekk.java.dpostbatch.model.SettingsProvider;
import no.bekk.java.dpostbatch.pack.CSVBrevProvider;
import no.bekk.java.dpostbatch.pack.MasseutsendelseWriter;
import no.bekk.java.dpostbatch.pack.ZipBuilder;

public class PackageBatchTask extends BatchTask {

	public PackageBatchTask(Batch batch, SettingsProvider settingsProvider, BatchLogger logger) {
		super(batch, settingsProvider, logger);
	}

	public void run() {
		logger.log("Packaging batch.");
		BatchSettings batchSettings = new BatchSettings(batch.getSettingsFile());
		
		Path writtenXml = writeBatchXml(batchSettings);
		logger.log("Wrote masseutsendelse.xml.");
		
		ZipBuilder zipBuilder = new ZipBuilder();
		zipBuilder.addEntry("masseutsendelse.xml", writtenXml.toFile());
		try (CSVBrevProvider brevProvider = new CSVBrevProvider(batch.getLettersCsv())) {
			Brev brev = null;
			while ((brev = brevProvider.nextBrev()) != null) {
				Path file = batch.getDirectory().resolve(brev.brevFil);
				if (!Files.exists(file)) {
					throw new RuntimeException("Referenced file does not exist " + file);
				}
				zipBuilder.addEntry(brev.brevFil, file.toFile());
			}
		}
		
		zipBuilder.buildTo(batch.getDestinationZip().toFile());
		logger.log("Packaged to " + batch.getDestinationZip().toAbsolutePath());
	}

	private Path writeBatchXml(BatchSettings batchSettings) {
		Path destinationXml = batch.getDestinationXml();
		try (OutputStream out = Files.newOutputStream(destinationXml);
				BrevProvider brevProvider = new CSVBrevProvider(batch.getLettersCsv())) {
			
			new MasseutsendelseWriter(true).write(batchSettings, brevProvider, out);
			return destinationXml;
		} catch (IOException e) {
			throw new RuntimeException("Error when packaging batch " + batch, e);
		} catch (XMLStreamException e) {
			throw new RuntimeException("Error when writing to batch-xml", e);
		}
	}

}
