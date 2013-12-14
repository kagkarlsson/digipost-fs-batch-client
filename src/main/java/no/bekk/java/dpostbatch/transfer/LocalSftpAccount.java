package no.bekk.java.dpostbatch.transfer;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import no.bekk.java.dpostbatch.sftp.SftpReceipt;

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
	
	@Override
	public Set<SftpReceipt> listReceipts() {
		Set<SftpReceipt> receipts = new HashSet<>();
		Path receiptFolder = masseutsendelse.resolve("kvittering");
		if (!Files.exists(receiptFolder)) {
			return receipts;
		}
		try (DirectoryStream<Path> files = Files.newDirectoryStream(receiptFolder)) {
			for (Path file : files) {
				String filename = localSftpDirectory.relativize(file).normalize().toString();
				if (SftpReceipt.isReceipt(filename)) {
					receipts.add(SftpReceipt.fromReceiptName(filename));
				}
			}
			return receipts;
		} catch (IOException e) {
			throw new RuntimeException("Unable to list files in receipt folder " + receiptFolder, e);
		}
	}

	@Override
	public void download(String remoteFilename, Path downloadTo) {
		Path remoteFile = localSftpDirectory.resolve(remoteFilename);
		if (!Files.exists(remoteFile)) {
			return;
		}
		
		try {
			Files.copy(remoteFile, downloadTo, REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Unable to copy " + remoteFile + " to " + downloadTo, e);
		}
	}

}
