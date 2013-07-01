package no.bekk.java.dpostbatch.pack;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.io.Files;

public class ZipBuilder {

	private List<Entry> entries = new ArrayList<ZipBuilder.Entry>();

	public ZipBuilder addEntry(String entryName, File file) {
		entries.add(new Entry(entryName, file));
		return this;
	}

	public void buildTo(File destination) {
		try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(destination))) {
			for (Entry e : entries) {
				ZipEntry zipEntry = new ZipEntry(e.entryName);
				zout.putNextEntry(zipEntry);
				Files.copy(e.file, zout);
				zout.closeEntry();
			}
			zout.flush();
			zout.finish();
			zout.close();
		} catch (Exception e) {
			throw new RuntimeException("Error when zipping to " + destination, e);
		}
	}

	public static class Entry {
		private String entryName;
		private File file;

		public Entry(String entryName, File file) {
			this.entryName = entryName;
			this.file = file;
		}

	}

}
