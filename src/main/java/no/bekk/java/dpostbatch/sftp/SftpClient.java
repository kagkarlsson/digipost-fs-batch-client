package no.bekk.java.dpostbatch.sftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpClient {

	private JSch jSch;
	private Session session;
	private ChannelSftp sftpChannel;
	private String host;
	private int port;
	private String username;
	private String passphrase;
	private InputStream privatekey;
	
	public SftpClient(String host, int port, String username, InputStream privatekey, String passphrase) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.privatekey = privatekey;
		this.passphrase = passphrase;
	}
	
	public void connect() {
		jSch = new JSch();

		try {
			final byte[] keyBytes = ByteStreams.toByteArray(privatekey);
			jSch.addIdentity(username, // String userName
					keyBytes, // byte[] privateKey
					null, // byte[] publicKey
					passphrase.getBytes() // byte[] passPhrase
			);

			session = jSch.getSession(username, host, port);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword("hej");

			session.connect();
			Channel channel = session.openChannel("sftp");
			sftpChannel = (ChannelSftp) channel;
			sftpChannel.connect();

		} catch (JSchException e) {
			throw new SftpClientException("Unable to connect to SFTP-server.", e);
		} catch (IOException e) {
			throw new SftpClientException("Unable to connect to SFTP-server.", e);
		}
	}
	
	public void disconnect() {
		if (jSch != null) {
			if (sftpChannel != null) {
				sftpChannel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
	}
	
	public boolean isConnected() {
		return session != null && session.isConnected() && sftpChannel != null && sftpChannel.isConnected();
	}

	private void ensureConnected() {
		if (!isConnected()) {
			connect();
		}
	}

	
	@SuppressWarnings("unchecked")
	public List<SftpFile> ls(final String directory) {
		ensureConnected();
		try {
			Vector<LsEntry> files = sftpChannel.ls(directory);

			return Lists.newArrayList(Iterables.transform(files, new Function<LsEntry, SftpFile> () {
				public SftpFile apply(LsEntry input) {
					return new SftpFile(directory, input.getFilename(), input.getAttrs().isDir());
				}
			}));
		} catch (SftpException e) {
			throw new SftpClientException("Failed to list files in " + directory, e);
		}
	}

	public void upload(String toPath, InputStream data) {
		ensureConnected();
		try {
			sftpChannel.put(data, toPath);
		} catch (SftpException e) {
			throw new SftpClientException("Failed to uplaod to path " + toPath, e);
		}
	}

	public void download(String fromPath, OutputStream downloadTo) {
		ensureConnected();
		try {
			sftpChannel.get(fromPath, downloadTo);
		} catch (SftpException e) {
			throw new SftpClientException("Failed to download from path " + fromPath, e);
		}
	}

	public void mkdir(String directoryPath) {
		ensureConnected();
		try {
			sftpChannel.mkdir(directoryPath);
		} catch (SftpException e) {
			throw new SftpClientException("Failed to create directory " + directoryPath, e);
		}
	}

	public static class SftpFile {

		private String directory;
		private String filename;
		private boolean isDirectory;

		public SftpFile(String directory, String filename, boolean isDirectory) {
			this.directory = directory;
			this.filename = filename;
			this.isDirectory = isDirectory;
		}

		public String getHomeRelativePath() {
			String dir = "";
			if (!Strings.isNullOrEmpty(directory)) {
				dir = directory + "/";
			}
			return dir + filename;
		}

		public String getFilename() {
			return filename;
		}

		public boolean isFile() {
			return !isDirectory;
		}
		
	}

}
