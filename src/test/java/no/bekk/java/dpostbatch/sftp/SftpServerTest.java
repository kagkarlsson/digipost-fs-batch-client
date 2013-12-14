package no.bekk.java.dpostbatch.sftp;

import java.io.IOException;

import no.bekk.java.dpostbatch.sftp.server.SftpServerForTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public abstract class SftpServerTest {

	@Rule 
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	private static SftpServerForTest sftpServer;

	protected SftpServerForTest getSftpServer() {
		return sftpServer;
	}
	
	@Before
	public void initSftpServer() throws IOException {
		if (sftpServer == null) {
			sftpServer = new SftpServerForTest(tempFolder.newFolder("sftp").toPath());
			sftpServer.start();

			Runtime.getRuntime().addShutdownHook(new Thread(){
				@Override
				public void run() {
					try {
						sftpServer.stop();
					} catch (InterruptedException e) {
						e.printStackTrace(System.err);
					}
				}
			});
		}
		
	}
	
	
}
