package no.bekk.java.dpostbatch.sftp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthNone;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.sftp.SftpSubsystem;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpClient {

	private static final String HOST = "localhost";
	private static final int PORT = 23001;
	private static final String USER = "karlsson";
	private static final String PRIVATE_KEY_LOCATION = "/Users/karlssons/.ssh/id_rsa";

	public static void main(String[] args) throws Exception {
		SshServer sshd = startSSHD();
		connectSftp();
		sshd.stop();
	}

	private static SshServer startSSHD() {
		SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setPort(PORT);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));

		List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
		userAuthFactories.add(new UserAuthNone.Factory());
		sshd.setUserAuthFactories(userAuthFactories);

		sshd.setCommandFactory(new ScpCommandFactory());

		List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
		namedFactoryList.add(new SftpSubsystem.Factory());
		sshd.setSubsystemFactories(namedFactoryList);

		String sftpUserDir = "/Users/karlssons/work/digipost-batch-client/sftp";
		sshd.setFileSystemFactory(new DirectoryNativeFileSystemFactory(new File(sftpUserDir)));

		try {
			sshd.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sshd;
	}

	private static void connectSftp() {
		JSch jSch = new JSch();

		try {
			final byte[] privateKey = Files.readAllBytes(Paths.get(PRIVATE_KEY_LOCATION));
			jSch.addIdentity(USER, // String userName
					privateKey, // byte[] privateKey
					null, // byte[] publicKey
					"changeme".getBytes() // byte[] passPhrase
			);

			Session session = jSch.getSession(USER, HOST, PORT);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword("hej");

			session.connect();
			Channel channel = session.openChannel("sftp");
			ChannelSftp sftp = (ChannelSftp) channel;
			sftp.connect();

			// list all the files from the sftp directory
			final Vector files = sftp.ls(".");
			Iterator itFiles = files.iterator();
			while (itFiles.hasNext()) {
				System.out.println("Index: " + itFiles.next());
			}

			// final ByteArrayInputStream in = new
			// ByteArrayInputStream("This is a sample text".getBytes());

			// upload file
			// sftp.put(in, "test.txt", ChannelSftp.OVERWRITE);

			sftp.disconnect();
			session.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		}
	}

}
