package no.bekk.java.dpostbatch.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import no.bekk.java.dpostbatch.model.BatchSettings.BatchSetting;
import no.bekk.java.dpostbatch.pack.CsvDocumentProvider;

public class BatchBuilder {

	private Path batchesDirectory;
	private String batchName = "batch1";
	private boolean ready = true;
	private List<Document> documents = new ArrayList<>();
	private boolean shouldHaveSettingsFile = true;
	private boolean shouldHaveLettersFile = true;
	private boolean awaitingReceipt = false;
	private List<String> csvLines;
	private String uploadedFilename;

	private BatchBuilder(Path batchesDirectory) {
		this.batchesDirectory = batchesDirectory;
	}

	public static BatchBuilder newBatch(Path path) {
		Document document = new Document("id", "kundeId", "01010112345", "emne", "fil.pdf");
		BatchBuilder batchBuilder = new BatchBuilder(path);
		batchBuilder.documents.add(document);
		return batchBuilder;
	}

	public Batch build() throws IOException {
		Path batchFolder = batchesDirectory.resolve(batchName);
		Files.createDirectories(batchFolder);
		Batch batch = new Batch(batchFolder);
		if (ready) {
			Files.createFile(batch.getReadyFile());
		}
		if (shouldHaveSettingsFile) {
			defaultSettings(batch.getSettingsFile());
		}
		if (shouldHaveLettersFile) {
			byte[] content;
			if (csvLines != null) {
				content = Joiner.on("\n").join(csvLines).getBytes();
			} else {
				content = toCsv(documents);
			}
			Files.write(batch.getLettersCsv(), content, StandardOpenOption.CREATE_NEW);
		}

		for (Document b : documents) {
			Files.write(batchFolder.resolve(b.inneholdFil), "fake-pdf-content".getBytes(), StandardOpenOption.CREATE_NEW);
		}
		
		if (awaitingReceipt) {
			Files.write(batch.getAwaitReceiptFile(), uploadedFilename.getBytes());
		}

		return batch;
	}

	private void defaultSettings(Path path) throws IOException {
		Properties props = new Properties();
		props.setProperty(BatchSetting.SENDER_ID.getKey(), "1001");
		props.setProperty(BatchSetting.BATCH_ID.getKey(), "batch-1");
		props.setProperty(BatchSetting.BATCH_NAME.getKey(), "Batch 1");
		props.setProperty(BatchSetting.AUTOCONFIRM.getKey(), "false");
		Files.createFile(path);
		PrintWriter writer = new PrintWriter(path.toFile());
		props.list(writer);
		writer.close();
	}

	private byte[] toCsv(List<Document> documents) {
		StringBuilder csv = new StringBuilder();
		for (Document b : documents) {

			append(csv, b.id);
			append(csv, b.kundeId);
			append(csv, b.foedselsnummer);
			append(csv, b.emne);
			csv.append(b.inneholdFil);
			csv.append("\n");
		}
		return csv.toString().getBytes();
	}

	private void append(StringBuilder csv, String id) {
		csv.append(id);
		csv.append(CsvDocumentProvider.SEPARATOR);
	}

	public BatchBuilder medBrev(Document... b) {
		documents = Lists.newArrayList(b);
		return this;
	}

	public BatchBuilder setNew() {
		ready = false;
		return this;
	}

	public BatchBuilder settings(boolean shouldHaveSettingsFile) {
		this.shouldHaveSettingsFile = shouldHaveSettingsFile;
		return this;
	}

	public BatchBuilder letters(boolean shouldHaveLettersFile) {
		this.shouldHaveLettersFile = shouldHaveLettersFile;
		return this;
	}

	public BatchBuilder medBrevCsv(String... csvLines) {
		this.csvLines = Lists.newArrayList(csvLines);
		return this;
	}

	public BatchBuilder medUploadedFilename(String uploadedFilename) {
		this.uploadedFilename = uploadedFilename;
		return this;
	}

	public BatchBuilder awaitReceipt() {
		this.awaitingReceipt = true;
		return this;
	}

}
