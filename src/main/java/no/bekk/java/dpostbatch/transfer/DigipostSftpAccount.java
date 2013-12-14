package no.bekk.java.dpostbatch.transfer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import no.bekk.java.dpostbatch.sftp.SftpClient;
import no.bekk.java.dpostbatch.sftp.SftpClient.SftpFile;
import no.bekk.java.dpostbatch.sftp.SftpReceipt;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class DigipostSftpAccount implements SftpAccount {

	private SftpClient sftpClient;

	public DigipostSftpAccount(SftpClient sftpClient) {
		this.sftpClient = sftpClient;
	}
	
	@Override
	public void upload(Path zip, String toPath) {
		try (InputStream data = new BufferedInputStream(Files.newInputStream(zip))) {
			sftpClient.upload(toPath, data);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read from file " + zip.toAbsolutePath().toString(), e);
		}
	}

	@Override
	public void download(String remoteFilename, Path receiptPath) {
		try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(receiptPath))) {
			sftpClient.download(remoteFilename, out);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write to open file for writing " + 
					receiptPath.toAbsolutePath().toString());
		}
	}

	@Override
	public Set<SftpReceipt> listReceipts() {
		return 
			FluentIterable
			.from(sftpClient.ls("masseutsendelse/kvittering"))
			.filter(isReceipt())
			.transform(toSftpRecipt())
			.toSet();
	}

	private Function<SftpFile, SftpReceipt> toSftpRecipt() {
		return new Function<SftpFile, SftpReceipt>() {
			public SftpReceipt apply(SftpFile input) {
				return SftpReceipt.fromReceiptName(input.getFilename());
			}
		};
	}

	private Predicate<SftpFile> isReceipt() {
		return new Predicate<SftpFile>() {
			public boolean apply(SftpFile input) {
				return input.isFile() && SftpReceipt.isReceipt(input.getFilename());
			}
		};
	}

}
