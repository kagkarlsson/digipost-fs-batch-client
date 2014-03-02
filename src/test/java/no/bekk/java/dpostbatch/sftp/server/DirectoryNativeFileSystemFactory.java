package no.bekk.java.dpostbatch.sftp.server;

import java.io.File;

import org.apache.sshd.common.Session;
import org.apache.sshd.server.FileSystemFactory;
import org.apache.sshd.server.FileSystemView;

public class DirectoryNativeFileSystemFactory implements FileSystemFactory {

	private File home;
	
	public DirectoryNativeFileSystemFactory(File home) {
		this.home = home;
	}

	public FileSystemView createFileSystemView(Session session) {
		String userName = session.getUsername();
		if (!home.exists() && !home.mkdirs()) {
			throw new RuntimeException("Unable to create SFTP home-directory: " + home.getAbsolutePath());
		}
		
		FileSystemView fsView = new DirectoryNativeFileSystemView(userName, home, false);
		return fsView;
	}

}
