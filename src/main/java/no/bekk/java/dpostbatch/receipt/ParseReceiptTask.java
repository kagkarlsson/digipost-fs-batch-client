package no.bekk.java.dpostbatch.receipt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchLogger;
import no.bekk.java.dpostbatch.model.BatchSettings;

public class ParseReceiptTask {

	public BatchSettings run(Batch batch, BatchLogger logger) {
		Path receiptPath = batch.getReceiptPath();
		if (!Files.exists(receiptPath)) {
			throw new RuntimeException("Receipt does not exist " + receiptPath);
		}
		
		try (InputStream is = Files.newInputStream(receiptPath)) {
			new ReceiptParser().parse(is);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read receipt " + receiptPath, e);
		}
		
		return null;
	}

}
