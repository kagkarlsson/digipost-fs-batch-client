package no.bekk.java.dpostbatch.transfer;

import java.nio.file.Path;

public interface SftpAccount {

	void upload(Path zip, String string);

}
