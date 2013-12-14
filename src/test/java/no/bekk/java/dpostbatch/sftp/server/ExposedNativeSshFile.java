package no.bekk.java.dpostbatch.sftp.server;

import java.io.File;

import org.apache.sshd.server.filesystem.NativeSshFile;

public class ExposedNativeSshFile extends NativeSshFile {

	protected ExposedNativeSshFile(String fileName, File file, String userName) {
		super(fileName, file, userName);
	}

}
