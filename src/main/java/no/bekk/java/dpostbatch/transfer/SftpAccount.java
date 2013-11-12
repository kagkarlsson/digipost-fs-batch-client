package no.bekk.java.dpostbatch.transfer;

import java.nio.file.Path;
import java.util.Set;

import no.bekk.java.dpostbatch.sftp.SftpReceipt;

public interface SftpAccount {

	void upload(Path zip, String string);

	Set<SftpReceipt> listReceipts();

	boolean download(String remoteFilename, Path receiptPath);

}
