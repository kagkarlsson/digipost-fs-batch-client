package no.bekk.java.dpostbatch.task;

import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchBuilder;
import no.bekk.java.dpostbatch.model.Brev;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PackageBatchTaskTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	private BatchBuilder batchBuilder;
	
	@Before
	public void setUp() throws Exception {
		batchBuilder = BatchBuilder.newBatch(tempFolder.newFolder().toPath());
	}
	
	@Test
	public void test() throws Exception {
		Brev brev = new Brev("id", "kundeId", "01010112345", "emne", "fil.pdf");
		Batch batch = batchBuilder.medBrev(brev).build();
		
		new PackageBatchTask(batch, null, new MockLogger()).run();
		
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


}
