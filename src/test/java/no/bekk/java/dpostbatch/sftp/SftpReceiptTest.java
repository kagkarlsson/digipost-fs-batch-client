package no.bekk.java.dpostbatch.sftp;

import static org.junit.Assert.*;

import org.junit.Test;

public class SftpReceiptTest {

	@Test
	public void testFormat() {
		String filename = "min-mottakersplitt.resultat.20111201-145344585.zip";
		String path = "/masseutsendelse/kvittering/";
		
		assertTrue(SftpReceipt.isReceipt(filename));
		assertEquals("min-mottakersplitt", SftpReceipt.fromReceiptName(filename).getOriginalName());

		String fullpath = path + filename;
		assertTrue(SftpReceipt.isReceipt(fullpath));
		assertEquals("min-mottakersplitt", SftpReceipt.fromReceiptName(fullpath).getOriginalName());
	}
}
