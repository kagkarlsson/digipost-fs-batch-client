package no.bekk.java.dpostbatch.task;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchSettings;
import no.bekk.java.dpostbatch.model.BatchSettings.BatchSetting;
import no.bekk.java.dpostbatch.model.Brev;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PackageBatchTaskTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	private Path batchFolder;
	private Batch batch;
	
	@Before
	public void setUp() throws Exception {
		batchFolder = tempFolder.newFolder().toPath();
		batch = new Batch(batchFolder);
	}
	
	@Test
	public void test() throws Exception {
		Files.createFile(batchFolder.resolve(Batch.BATCH_READY_FILE));
		Brev brev = new Brev("id", "kundeId", "01010112345", "emne", "fil.pdf");
		defaultSettings(batch.getSettingsFile());
		Files.write(batch.getLettersCsv(), toCsv(brev), StandardOpenOption.CREATE_NEW);
		Files.write(batchFolder.resolve("fil.pdf"), "fake-pdf-content".getBytes(), StandardOpenOption.CREATE_NEW);
		
		new PackageBatchTask(batch, null).run();
		assertTrue(Files.exists(batch.getDestinationZip()));
		ZipFile zip = new ZipFile(batch.getDestinationZip().toFile());
		
		contains(zip, "masseutsendelse.xml");
		contains(zip, "fil.pdf");
		
		zip.close();
	}

	private boolean contains(ZipFile zip, String string) {
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) entries.nextElement();
			if (zipEntry.getName().equals(string)) {
				return true;
			}
		}
		return false;
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

	private byte[] toCsv(Brev brev) {
		StringBuilder csv = new StringBuilder();
		append(csv, brev.id);
		append(csv, brev.kundeId);
		append(csv, brev.foedselsnummer);
		append(csv, brev.emne);
		append(csv, brev.brevFil);
		return csv.toString().getBytes();
	}

	private void append(StringBuilder csv, String id) {
		csv.append(id);
		csv.append(",");
	}

}
