package no.bekk.java.dpostbatch.sftp;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import no.bekk.java.dpostbatch.sftp.SftpClient.SftpFile;
import no.bekk.java.dpostbatch.sftp.server.SftpServerForTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.jcraft.jsch.SftpException;

public class SftpClientTest extends SftpServerTest {

	private SftpClient client;

	@Before
	public void setUp() {
		client = new SftpClient("localhost", SftpServerForTest.PORT, "username",
				getClass().getResourceAsStream("/test_rsa"), "passphrase");
		client.connect();
	}

	@After
	public void tearDown() {
		if (client != null) client.disconnect(); 
	}
	
	@Test
	public void should_upload_and_download_file_in_home_dir() throws SftpException {
		client.upload("file.txt", inputstream("data"));
		
		assertThat(ls("."), hasItem(containsString("file.txt")));
		assertEquals("data", downloadAsString("file.txt"));
	}

	@Test
	public void should_upload_and_download_file_in_sub_dir() throws SftpException {
		client.mkdir("level1");
		client.upload("level1/file1.txt", inputstream("data"));
		
		assertThat(ls("."), hasItem(containsString("level1")));
		assertThat(ls("level1"), hasItem(containsString("level1/file1.txt")));
		assertEquals("data", downloadAsString("level1/file1.txt"));
	}

	private List<String> ls(String path) throws SftpException {
		return newArrayList(transform(client.ls(path), new Function<SftpFile, String>() {
			public String apply(SftpFile input) {
				return input.getHomeRelativePath();
			}
		}));
	}

	private String downloadAsString(String fromPath) throws SftpException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		client.download(fromPath, baos);
		String downloaded = new String(baos.toByteArray());
		return downloaded;
	}

	private ByteArrayInputStream inputstream(String data) {
		return new ByteArrayInputStream(data.getBytes());
	}
	
}
