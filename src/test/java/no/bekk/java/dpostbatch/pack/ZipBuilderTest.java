package no.bekk.java.dpostbatch.pack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.zip.ZipFile;

import no.bekk.java.dpostbatch.pack.ZipBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.CharStreams;

public class ZipBuilderTest {
	
	private static final String FILE_CONTENTS = "content";
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	private File file1;
	private File file2;

	@Before
	public void setUp() throws IOException {
		file1 = tempFolder.newFile();
		file2 = tempFolder.newFile();
		Files.write(file1.toPath(), FILE_CONTENTS.getBytes());
		Files.write(file2.toPath(), FILE_CONTENTS.getBytes());
	}
	
	@Test
	public void shouldZipMultipleEntries() throws Exception {
		File zipFile = tempFolder.newFile();
		new ZipBuilder()
		.addEntry("file1.pdf", file1)
		.addEntry("file2.pdf", file2)
		.buildTo(zipFile);
		
		try (ZipFile archive = new ZipFile(zipFile)) {
			assertTrue(archive.getEntry("file1.pdf") != null);
			assertTrue(archive.getEntry("file2.pdf") != null);
			String content = CharStreams.toString(new InputStreamReader(archive.getInputStream(archive.getEntry("file1.pdf"))));
			assertEquals(FILE_CONTENTS, content);
		}
	}
	
}
