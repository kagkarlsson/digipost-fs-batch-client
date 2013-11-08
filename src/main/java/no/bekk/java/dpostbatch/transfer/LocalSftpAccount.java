package no.bekk.java.dpostbatch.transfer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalSftpAccount implements SftpAccount {

	private Path localSftpDirectory;
	Path masseutsendelse;

	public LocalSftpAccount(Path localSftpDirectory) {
		this.localSftpDirectory = localSftpDirectory;
		masseutsendelse = localSftpDirectory.resolve("masseutsendelse");
		try {
			Files.createDirectories(masseutsendelse);
		} catch (IOException e) {
			throw new RuntimeException("Unable to create folder " + masseutsendelse, e);
		}
	}

	@Override
	public void upload(Path zip, String destinationFilename) {
		Path destination = masseutsendelse.resolve(destinationFilename);
		try {
			Files.deleteIfExists(destination);
			Files.copy(zip, destination);
		} catch (IOException e) {
			throw new RuntimeException("Error when uploading file to " + destination, e);
		}
	}
	
	public boolean downloadReceipt(String batchId, Path destination) {
		Path receiptFolder = masseutsendelse.resolve("kvittering");
		if (!Files.exists(receiptFolder)) {
			return false;
		}
		try (DirectoryStream<Path> files = Files.newDirectoryStream(receiptFolder)) {
			for (Path file : files) {
				if (file.getFileName().toString().startsWith(batchId)) {
					Files.copy(file, destination);
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			throw new RuntimeException("Unable to list files in receipt folder " + receiptFolder, e);
		}
	}

}
