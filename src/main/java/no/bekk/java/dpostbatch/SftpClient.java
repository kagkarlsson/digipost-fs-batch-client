package no.bekk.java.dpostbatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpClient {

	private static final String HOST = "192.168.1.118";
	private static final int PORT = 22;
	private static final String USER = "karlsson";
	private static final String PRIVATE_KEY_LOCATION = "/Users/karlssons/.ssh/id_rsa";

	public static void main(String[] args) {

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

//			final ByteArrayInputStream in = new ByteArrayInputStream("This is a sample text".getBytes());

			// upload file
//			sftp.put(in, "test.txt", ChannelSftp.OVERWRITE);

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
