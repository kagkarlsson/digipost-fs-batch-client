package no.bekk.java.dpostbatch.sftp.server;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthNone;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.sftp.SftpSubsystem;

public class SftpServerForTest {

	public static final int PORT = 23001;
	private SshServer sshd;
	private Path sftpUserDir;

	public SftpServerForTest(Path path) {
		this.sftpUserDir = path;
	}

	public SshServer start() {
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(PORT);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));

		List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
		userAuthFactories.add(new UserAuthNone.Factory());
		sshd.setUserAuthFactories(userAuthFactories);

		sshd.setCommandFactory(new ScpCommandFactory());

		List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
		namedFactoryList.add(new SftpSubsystem.Factory());
		sshd.setSubsystemFactories(namedFactoryList);

		sshd.setFileSystemFactory(new DirectoryNativeFileSystemFactory(sftpUserDir.toFile()));

		try {
			sshd.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sshd;
	}
	
	public void stop() throws InterruptedException {
		if (sshd != null) sshd.stop();
	}

}
