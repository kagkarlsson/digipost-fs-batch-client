package no.bekk.java.dpostbatch.transfer;

import java.nio.file.Path;
import java.util.Set;

import no.bekk.java.dpostbatch.sftp.SftpReceipt;

public interface SftpAccount {

	void upload(Path zip, String string);

	void download(String remoteFilename, Path receiptPath);

	Set<SftpReceipt> listReceipts();

}
