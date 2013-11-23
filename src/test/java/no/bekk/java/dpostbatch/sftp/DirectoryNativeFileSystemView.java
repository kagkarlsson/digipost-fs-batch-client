package no.bekk.java.dpostbatch.sftp;

import java.io.File;

import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.SshFile;
import org.apache.sshd.server.filesystem.NativeFileSystemFactory;
import org.apache.sshd.server.filesystem.NativeFileSystemView;
import org.apache.sshd.server.filesystem.NativeSshFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * File system view based on native file system. Here the root directory will be
 * user virtual root (/).
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class DirectoryNativeFileSystemView implements FileSystemView {

	private final Logger LOG = LoggerFactory.getLogger(NativeFileSystemView.class);

	// the first and the last character will always be '/'
	// It is always with respect to the root directory.
	private String currDir;

	private String userName;

	private boolean caseInsensitive = false;

	/**
	 * Constructor - internal do not use directly, use
	 * {@link NativeFileSystemFactory} instead
	 */
	public DirectoryNativeFileSystemView(String userName, File home, boolean caseInsensitive) {
		if (userName == null) {
			throw new IllegalArgumentException("user can not be null");
		}

		this.caseInsensitive = caseInsensitive;

		currDir = home.getAbsolutePath();
		this.userName = userName;

		// add last '/' if necessary
		LOG.debug("Native filesystem view created for user \"{}\" with root \"{}\"", userName, currDir);
	}

	/**
	 * Get file object.
	 */
	public SshFile getFile(String file) {
		return getFile(currDir, file);
	}

	public SshFile getFile(SshFile baseDir, String file) {
		return getFile(baseDir.getAbsolutePath(), file);
	}

	protected SshFile getFile(String dir, String file) {
		// get actual file object
		String physicalName = NativeSshFile.getPhysicalName("/", dir, file, caseInsensitive);
		File fileObj = new File(physicalName);

		// strip the root directory and return
		String userFileName = physicalName.substring("/".length() - 1);
		return new ExposedNativeSshFile(userFileName, fileObj, userName);
	}

}
