package no.bekk.java.dpostbatch.sftp.server;

import java.io.File;

import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.SshFile;
import org.apache.sshd.server.filesystem.NativeFileSystemView;
import org.apache.sshd.server.filesystem.NativeSshFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryNativeFileSystemView implements FileSystemView {

	private final Logger LOG = LoggerFactory.getLogger(NativeFileSystemView.class);
	private String currDir;
	private String userName;
	private boolean caseInsensitive = false;

	public DirectoryNativeFileSystemView(String userName, File home, boolean caseInsensitive) {
		if (userName == null) {
			throw new IllegalArgumentException("user can not be null");
		}

		this.caseInsensitive = caseInsensitive;

		currDir = home.getAbsolutePath();
		this.userName = userName;

		LOG.debug("Native filesystem view created for user \"{}\" with root \"{}\"", userName, currDir);
	}

	public SshFile getFile(String file) {
		return getFile(currDir, file);
	}

	public SshFile getFile(SshFile baseDir, String file) {
		return getFile(baseDir.getAbsolutePath(), file);
	}

	protected SshFile getFile(String dir, String file) {
		String physicalName = NativeSshFile.getPhysicalName("/", dir, file, caseInsensitive);
		File fileObj = new File(physicalName);

		String userFileName = physicalName.substring("/".length() - 1);
		return new ExposedNativeSshFile(userFileName, fileObj, userName);
	}

}
