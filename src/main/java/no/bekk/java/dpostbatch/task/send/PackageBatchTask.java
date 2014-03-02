package no.bekk.java.dpostbatch.task.send;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.stream.XMLStreamException;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchLogger;
import no.bekk.java.dpostbatch.model.BatchSettings;
import no.bekk.java.dpostbatch.model.Document;
import no.bekk.java.dpostbatch.pack.CsvDocumentProvider;
import no.bekk.java.dpostbatch.pack.DocumentProvider;
import no.bekk.java.dpostbatch.pack.MasseutsendelseWriter;
import no.bekk.java.dpostbatch.pack.ZipBuilder;

public class PackageBatchTask  {

	public BatchSettings run(Batch batch, BatchLogger logger) {
		logger.log("Packaging batch.");
		BatchSettings batchSettings = new BatchSettings(batch.getSettingsFile());
		
		Path writtenXml = writeBatchXml(batch, batchSettings);
		logger.log("Wrote masseutsendelse.xml.");
		
		ZipBuilder zipBuilder = new ZipBuilder();
		zipBuilder.addEntry("masseutsendelse.xml", writtenXml.toFile());
		try (CsvDocumentProvider documentProvider = new CsvDocumentProvider(batch.getLettersCsv())) {
			Document document = null;
			while ((document = documentProvider.next()) != null) {
				Path file = batch.getDirectory().resolve(document.inneholdFil);
				if (!Files.exists(file)) {
					throw new RuntimeException("Referenced file does not exist " + file);
				}
				zipBuilder.addEntry(document.inneholdFil, file.toFile());
			}
		}
		
		zipBuilder.buildTo(batch.getDestinationZip().toFile());
		logger.log("Packaged to " + batch.getDestinationZip().toAbsolutePath());
		return batchSettings;
	}

	private Path writeBatchXml(Batch batch, BatchSettings batchSettings) {
		Path destinationXml = batch.getDestinationXml();
		try (OutputStream out = Files.newOutputStream(destinationXml);
				DocumentProvider documentProvider = new CsvDocumentProvider(batch.getLettersCsv())) {
			
			new MasseutsendelseWriter(true).write(batchSettings, documentProvider, out);
			return destinationXml;
		} catch (IOException e) {
			throw new RuntimeException("Error when packaging batch " + batch, e);
		} catch (XMLStreamException e) {
			throw new RuntimeException("Error when writing to batch-xml", e);
		}
	}

}
